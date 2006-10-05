/*
 *                Doelan development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public Licence.  This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/gpl.txt
 *
 * Copyright (c) 2004-2005 ENS Microarray Platform
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the Doelan project and its aims,
 * or to join the Doelan mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/doelan
 */

package fr.ens.transcriptome.doelan.tests;

import org.apache.commons.collections.primitives.ArrayDoubleList;

import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.DoelanChart;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.doelan.algorithms.QualityUnitTest;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult.SummaryResult;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.util.SystemUtils;
import fr.ens.transcriptome.nividic.util.graphics.GraphicsUtil;
import fr.ens.transcriptome.nividic.util.parameter.FixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterBuilder;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class defines a test for Test Heterogeneity of features.
 * @author Laurent Jourdren
 */
public class HeterogeneousFeatureTest extends QualityUnitTest implements Module {

  private static final int DEFAULT_BINS = 16;
  private static AboutModule aboutModule;

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    if (aboutModule == null) {

      ModuleDescription md = null;
      try {
        md = new ModuleDescription("HeterogeneousFeatureTest",
            "Test Heterogeneity of features");
        md.setWebsite(DoelanRegistery.getAppURL());
        md.setHTMLDocumentation(SystemUtils.readTextRessource("/files/test-"
            + SystemUtils.getClassShortName(this.getClass()) + ".html"));
        md.setStability(AboutModule.STATE_STABLE);
        md.setVersion(Defaults.DEFAULT_TEST_VERSION);
      } catch (PlatformException e) {
        getLogger().error("Unable to create the module description");
      }

      aboutModule = md;
    }

    return aboutModule;
  }

  /**
   * Set the parameters of the element.
   * @return The defaults parameters to set.
   */
  protected Parameters defineParameters() {

    try {

      final Parameter threshold = new ParameterBuilder().withName("threshold")
          .withLongName("Maximal threshold of bad spots").withType(
              Parameter.DATATYPE_DOUBLE).withDescription(
              "Threshold of invalid spots to reject the chip")
          .withGreaterThanValue(0).withDefaultValue("10").withUnit("%")
          .getParameter();

      final Parameter max = new ParameterBuilder().withName("max")
          .withLongName("Maximal value of standard deviation").withType(
              Parameter.DATATYPE_INTEGER).withDescription(
              "Minimal value of the standard deviation for a spot")
          .withGreaterThanValue(0).withLowerThanValue(100).withDefaultValue(
              "500").getParameter();

      final Parameter filterFlags = new ParameterBuilder().withType(
          Parameter.DATATYPE_YESNO).withName("filterFlags").withLongName(
          "Remove bad spots from output array list").withDescription(
          "Remove invalid spots in output arraylist file.").withDefaultValue(
          "yes").getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(max);
      params.addParameter(threshold);
      params.addParameter(filterFlags);

      return params;

    } catch (ParameterException e) {
      getLogger().error("Error while creating parameters: " + e);
    }

    return null;
  }

  /**
   * Test the quality of the bioassay.
   * @param bioassay BioAssay to test
   * @param arrayList The array list
   * @param parameters parameters of the test
   * @return A QualityObjectResultTest Object
   * @throws PlatformException if an error occurs while executing the test.
   */
  public QualityUnitTestResult test(final BioAssay bioassay,
      final BioAssay arrayList, final Parameters parameters)
      throws PlatformException {

    QualityUnitTestResult result = null;

    try {

      final boolean[] results = new boolean[bioassay.size()];
      final int[] flags = bioassay.getFlags();
      final int[] f635SD = bioassay.getDataFieldInt("F635 SD");
      final int[] f532SD = bioassay.getDataFieldInt("F532 SD");

      final double threshold = parameters.getParameter("threshold")
          .getDoubleValue();
      final double max = parameters.getParameter("max").getIntValue();

      final boolean filterFlags = parameters.getParameter("filterFlags")
          .getBooleanValue();

      int countOut532 = 0;
      int countOut635 = 0;
      int countRealSpot = 0;

      // Data data = new Data();
      ArrayDoubleList adl532 = new ArrayDoubleList();
      ArrayDoubleList adl635 = new ArrayDoubleList();

      for (int i = 0; i < flags.length; i++) {
        if (flags[i] == BioAssay.FLAG_ABSCENT)
          continue;

        // data.add(f532SD[i], 1);
        adl532.add(f532SD[i]);
        adl635.add(f635SD[i]);

        boolean flag = false;

        if (f532SD[i] >= max) {
          countOut532++;
          flag = true;
        }

        if (f635SD[i] >= max) {
          countOut635++;
          flag = true;
        }

        if (!flag)
          results[i] = true;

        countRealSpot++;
      }

      final double ratio532 = ((double) countOut532) / ((double) countRealSpot)
          * 100;
      final double ratio635 = ((double) countOut635) / ((double) countRealSpot)
          * 100;

      int realThreshold = (int) (threshold / 100 * countRealSpot);

      result = new QualityUnitTestResult(bioassay, this);
      result.setMessage("Heterogeneous features in 532: " + countOut532 + "/"
          + countRealSpot + " features (threshold: " + realThreshold
          + " features)<br>" + "Heterogeneous features in 635 : " + countOut635
          + "/" + countRealSpot + " features (threshold: " + realThreshold
          + " features)"

      );

      result.setNewFlags(results);
      result.setFilterFlags(filterFlags);
      result.setGlobalResultType(false);

      SummaryResult r532 = result.getResultChannel532();
      SummaryResult r635 = result.getResultChannel635();

      r532.setPercent(true);
      r532.setThresholdEqualityType("<=");
      r532.setUnit("%");
      r532.setThreshold(threshold);
      r532.setValue(ratio532);
      r532.setPass(ratio532 <= threshold);

      r635.setPercent(true);
      r635.setThresholdEqualityType("<=");
      r635.setUnit("%");
      r635.setThreshold(threshold);
      r635.setValue(ratio635);
      r635.setPass(ratio635 <= threshold);

      DoelanChart gu532 = new DoelanChart();
      gu532.setData(adl532.toArray());
      gu532.setTitle("532 foreground standard deviation");
      gu532.setXAxisLegend("Standard deviation");
      gu532.setYAxisLegend("#Spots");
      gu532.setHistCaption("Spots");
      gu532.setThreshold(max);
      gu532.setHeight(DoelanRegistery.getDoelanChartHeigth());
      gu532.setWidth(DoelanRegistery.getDoelanChartWidth());
      gu532.setBins(DEFAULT_BINS);
      gu532.setYLogAxis(true);

      DoelanChart gu635 = new DoelanChart();
      gu635.setData(adl635.toArray());
      gu635.setTitle("635 foreground standard deviation");
      gu635.setXAxisLegend("Standard deviation");
      gu635.setYAxisLegend("#Spots");
      gu635.setHistCaption("Spots");
      gu635.setThreshold(max);
      gu635.setHeight(DoelanRegistery.getDoelanChartHeigth());
      gu635.setWidth(DoelanRegistery.getDoelanChartWidth());
      gu635.setBins(DEFAULT_BINS);
      gu635.setYLogAxis(true);

      result.setImage(GraphicsUtil.mergeImages(gu532.getImage(), gu635
          .getImage()));

    } catch (ParameterException e) {
      throw new PlatformException("Error while creating parameters ("
          + this.getClass().getName() + "): " + e.getMessage());
    }

    return result;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *           <b>null </b>.
   */
  public HeterogeneousFeatureTest() throws PlatformException {
    // MUST BE EMPTY
  }
}