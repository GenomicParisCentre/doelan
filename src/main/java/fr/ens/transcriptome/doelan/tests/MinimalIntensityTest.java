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
import fr.ens.transcriptome.nividic.om.SpotIterator;
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
 * Test there are real spots in "abscent" spot.
 * @author Laurent Jourdren
 */
public class MinimalIntensityTest extends QualityUnitTest implements Module {

  private static final int DEFAULT_BINS = 16;
  private static final String CHANNEL_CY3 = "Cy3 (green)";
  private static final String CHANNEL_CY5 = "Cy5 (red)";
  private static final String CHANNEL_CY3CY5 = "Both";

  private static AboutModule aboutModule;

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    if (aboutModule == null) {

      ModuleDescription md = null;
      try {
        md =
            new ModuleDescription("MinimalIntensityTest",
                "Test if the spots have the required minimal intensity");
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
   * Define the parameters of the algorithm.
   */
  protected Parameters defineParameters() {

    try {

      final Parameter minimalIntensity =
          new ParameterBuilder().withName("minimalIntensity").withLongName(
              "Minimal spot intensity").withType(Parameter.DATATYPE_INTEGER)
              .withDescription("Minimal intensity of a spot")
              .withGreaterThanValue(0).withDefaultValue("50").getParameter();

      final Parameter channel =
          new ParameterBuilder().withName("channel").withLongName("Channel(s)")
              .withType(Parameter.DATATYPE_STRING).withDescription(
                  "Channel(s) to test").withChoices(
                  new String[] {CHANNEL_CY5, CHANNEL_CY3, CHANNEL_CY3CY5})
              .withDefaultValue(CHANNEL_CY3CY5).getParameter();

      final Parameter threshold =
          new ParameterBuilder().withName("threshold").withLongName(
              "Maximal threshold of bad spots").withType(
              Parameter.DATATYPE_DOUBLE).withDescription(
              "Threshold of invalid spots to reject the chip")
              .withGreaterThanValue(0).withDefaultValue("10").withUnit("%")
              .getParameter();

      final Parameter filterFlags =
          new ParameterBuilder().withName("filterFlags").withType(
              Parameter.DATATYPE_YESNO).withLongName(
              "Remove bad spots from output array list").withDescription(
              "Remove invalid spots in output arraylist file.")
              .withDefaultValue("yes").getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(minimalIntensity);
      params.addParameter(channel);
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

    if (bioassay == null)
      return null;

    QualityUnitTestResult result = null;

    try {

      final int minimalIntensity =
          getParameters().getParameter("minimalIntensity").getIntValue();
      final double threshold =
          getParameters().getParameter("threshold").getDoubleValue();
      final boolean filterFlags =
          parameters.getParameter("filterFlags").getBooleanValue();
      final String channel =
          parameters.getParameter("channel").getStringValue();

      boolean testGreen = true;
      boolean testRed = true;

      if (CHANNEL_CY3.equals(channel))
        testRed = false;
      else if (CHANNEL_CY5.equals(channel))
        testGreen = false;

      final boolean[] results = new boolean[bioassay.size()];

      SpotIterator si = bioassay.iterator();
      ArrayDoubleList dataGreen = new ArrayDoubleList();
      ArrayDoubleList dataRed = new ArrayDoubleList();

      int count = 0;
      int countRealSpot = 0;

      while (si.hasNext()) {

        si.next();
        results[si.getIndex()] = true;

        if (!si.isEmpty() && !si.isFlagAbscent()) {

          boolean notPassRed = si.getRed() < minimalIntensity;
          boolean notPassGreen = si.getGreen() < minimalIntensity;

          if (notPassRed && testRed || notPassGreen && testGreen) {
            count++;
            results[si.getIndex()] = false;
          }

        }

        dataGreen.add(si.getGreen());
        dataRed.add(si.getRed());

        countRealSpot++;
      }

      final double ratio = ((double) count) / ((double) countRealSpot) * 100;
      result = new QualityUnitTestResult(bioassay, this);

      final long max = (long) (countRealSpot * threshold / 100);
      result.setMessage("Features with invalid invalid intensity : "
          + count + "/" + countRealSpot + " features (threshold: " + max
          + " features)<br>");

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

      DoelanChart guGreen = null;

      if (testGreen) {

        guGreen = new DoelanChart();
        guGreen.setData(dataGreen.toArray());
        guGreen.setTitle("Green Median Intensity distribution");
        guGreen.setXAxisLegend("Intensity");
        guGreen.setYAxisLegend("#Spots");
        guGreen.setHistCaption("Spots");
        guGreen.setThreshold(minimalIntensity);
        guGreen.setWidth(DoelanRegistery.getDoelanChartWidth());
        guGreen.setHeight(DoelanRegistery.getDoelanChartHeigth());
        guGreen.setBins(DEFAULT_BINS);
        guGreen.setYLogAxis(true);
      }

      DoelanChart guRed = null;

      if (testRed) {

        guRed = new DoelanChart();
        guRed.setData(dataRed.toArray());
        guRed.setTitle("Red Median Intensity distribution");
        guRed.setXAxisLegend("Intensity");
        guRed.setYAxisLegend("#Spots");
        guRed.setHistCaption("Spots");
        guRed.setThreshold(minimalIntensity);
        guRed.setWidth(DoelanRegistery.getDoelanChartWidth());
        guRed.setHeight(DoelanRegistery.getDoelanChartHeigth());
        guRed.setBins(DEFAULT_BINS);
        guRed.setYLogAxis(true);
      }

      if (testGreen && testRed)
        result.setImage(GraphicsUtil.mergeImages(guGreen.getImage(), guRed
            .getImage()));
      else if (testRed)
        result.setImage(guRed.getImage());
      else
        result.setImage(guGreen.getImage());

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
   *             <b>null </b>.
   */
  public MinimalIntensityTest() throws PlatformException {
    // MUST BE EMPTY
  }

}