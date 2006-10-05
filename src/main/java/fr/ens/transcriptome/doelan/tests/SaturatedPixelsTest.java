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

import fr.ens.transcriptome.doelan.Defaults;
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
import fr.ens.transcriptome.nividic.util.parameter.FixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterBuilder;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class define a test for saturated pixel of features from a bioassay.
 * @author Laurent Jourdren
 */
public class SaturatedPixelsTest extends QualityUnitTest implements Module {

  private static AboutModule aboutModule;

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    if (aboutModule == null) {

      ModuleDescription md = null;
      try {
        md = new ModuleDescription("SaturatedPixelsTest",
            "Test saturated pixels of spots");
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

      final Parameter saturatedPixels = new ParameterBuilder().withName(
          "saturated pixels threshold").withLongName(
          "Threshold maximum saturated pixels").withType(
          Parameter.DATATYPE_INTEGER).withDescription(
          "Threshold of saturated pixels (in percent) to reject a spot")
          .withGreaterThanValue(0).withLowerThanValue(100)
          .withDefaultValue("1").withUnit("%").getParameter();

      final Parameter filterFlags = new ParameterBuilder().withName(
          "filterFlags")
          .withLongName("Remove bad spots from output array list").withType(
              Parameter.DATATYPE_YESNO).withDescription(
              "Remove invalid spots in output arraylist file.")
          .withDefaultValue("yes").getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(threshold);
      params.addParameter(saturatedPixels);
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
      final int[] sat635 = bioassay.getDataFieldInt("F635 % Sat.");
      final int[] sat532 = bioassay.getDataFieldInt("F532 % Sat.");
      final boolean filterFlags = parameters.getParameter("filterFlags")
          .getBooleanValue();

      final double threshold = parameters.getParameter("threshold")
          .getDoubleValue();
      final double saturatedPixels = parameters.getParameter(
          "saturated pixels threshold").getIntValue();

      int countSat532 = 0;
      int countSat635 = 0;
      int countRealSpot = 0;
      for (int i = 0; i < flags.length; i++) {
        results[i] = true;
        if (flags[i] == BioAssay.FLAG_ABSCENT)
          continue;

        if (sat532[i] >= saturatedPixels) {
          countSat532++;
          results[i] = false;
        }

        if (sat635[i] >= saturatedPixels) {
          countSat635++;
          results[i] = false;
        }

        countRealSpot++;
      }

      final double ratio532 = ((double) countSat532) / ((double) countRealSpot)
          * 100;
      final double ratio635 = ((double) countSat635) / ((double) countRealSpot)
          * 100;

      result = new QualityUnitTestResult(bioassay, this);

      final long max = (long) (countRealSpot * threshold / 100);
      result.setMessage("Features with invalid saturated pixels in 532 : "
          + countSat532 + "/" + countRealSpot + " features (threshold: " + max
          + " features)<br>"
          + "Features with invalid saturated pixels in 635: " + countSat635
          + "/" + countRealSpot + " features (threshold: " + max + " features)"

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
  public SaturatedPixelsTest() throws PlatformException {
    // MUST BE EMPTY
  }
}