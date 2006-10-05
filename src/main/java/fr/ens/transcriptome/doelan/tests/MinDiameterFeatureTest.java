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

import java.util.Arrays;

import org.apache.commons.collections.primitives.ArrayDoubleList;

import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.DoelanChart;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.doelan.algorithms.QualityUnitTest;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult.SummaryResult;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.BioAssayUtils;
import fr.ens.transcriptome.nividic.om.SpotIterator;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.util.NividicUtils;
import fr.ens.transcriptome.nividic.util.SystemUtils;
import fr.ens.transcriptome.nividic.util.parameter.FixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterBuilder;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class defines a quality test based on the minimal spot diameter.
 * @author Laurent Jourdren
 */
public class MinDiameterFeatureTest extends QualityUnitTest implements Module {

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
        md = new ModuleDescription("MinDiameterFeatureTest",
            "Test minimal diameter of features");
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
              "Threshold for the test").withGreaterThanValue(0)
          .withLowerThanValue(1).withDefaultValue("10").withUnit("%")
          .getParameter();

      final Parameter min = new ParameterBuilder().withName("min")
          .withLongName("Minimal diameter value").withType(
              Parameter.DATATYPE_DOUBLE).withDescription(
              "Minimal value of the diameter for a spot (included)")
          .withGreaterThanValue(0).withDefaultValue("80").withUnit("µm")
          .getParameter();

      final Parameter filterFlags = new ParameterBuilder().withType(
          Parameter.DATATYPE_YESNO).withName("filterFlags").withLongName(
          "Remove bad spots from output array list").withDescription(
          "Remove invalid spots in output arraylist file.").withDefaultValue(
          "yes").getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(min);
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
      final double min = parameters.getParameter("min").getDoubleValue();

      final double threshold = parameters.getParameter("threshold")
          .getDoubleValue();

      final boolean filterFlags = parameters.getParameter("filterFlags")
          .getBooleanValue();

      final boolean[] results = new boolean[bioassay.size()];

      final double[] dia;

      // Find the name of the diameter field
      switch (BioAssayUtils.getBioAssayType(bioassay)) {

      case BioAssayUtils.IMAGENE_RESULTS_BIOASSAY_TYPE:
        dia = bioassay.getDataFieldDouble("Diameter");
        break;

      case BioAssayUtils.GPR_BIOASSAY_TYPE:
      default:
        dia = NividicUtils.arrayIntToArrayDouble(bioassay
            .getDataFieldInt("Dia."));
        break;
      }

      if (dia == null)
        throw new PlatformException("Diameter field doesn't exits");

      int countBadDiameter = 0;
      int countRealSpot = 0;
      ArrayDoubleList adl = new ArrayDoubleList();

      SpotIterator si = bioassay.iterator();
      int i = 0;

      while (si.hasNext()) {
        si.next();
        if (si.isEmpty() || si.isFlagAbscent()) {
          i++;
          continue;
        }

        adl.add(dia[i]);
        if (dia[i] >= min)
          results[i] = true;
        else {
          results[i] = false;
          countBadDiameter++;
        }

        countRealSpot++;
        i++;
      }

      final double ratio = ((double) countBadDiameter)
          / ((double) countRealSpot) * 100;

      result = new QualityUnitTestResult(bioassay, this);

      long maxThreshold = (long) (countRealSpot * threshold / 100.0);

      result.setMessage("Bad diameter features: " + countBadDiameter + "/"
          + countRealSpot + " features (threshold: " + maxThreshold
          + " features)");

      DoelanChart gu = new DoelanChart();
      gu.setData(adl.toArray());
      gu.setTitle("Diameter distribution");
      gu.setXAxisLegend("Spot diameter (pixels)");
      gu.setYAxisLegend("#Spots");
      gu.setHistCaption("Spots");
      gu.setThreshold(min);
      gu.setWidth(DoelanRegistery.getDoelanChartWidth());
      gu.setHeight(DoelanRegistery.getDoelanChartHeigth());
      gu.setBins(DEFAULT_BINS);

      result.setImage(gu.getImage());
      result.setGlobalResultType(true);
      result.setNewFlags(results);
      result.setFilterFlags(filterFlags);
      SummaryResult rac = result.getResultAllChannels();
      rac.setPercent(true);
      rac.setThresholdEqualityType("<=");
      rac.setUnit("%");
      rac.setThreshold(threshold);
      rac.setValue(ratio);
      rac.setPass(ratio <= threshold);

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
  public MinDiameterFeatureTest() throws PlatformException {
    // MUST BE EMPTY
  }

}