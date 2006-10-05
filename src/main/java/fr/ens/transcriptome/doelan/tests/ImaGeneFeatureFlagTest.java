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
import fr.ens.transcriptome.nividic.om.SpotIterator;
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
 * This class defines a test for not found spot flag.
 * @author Laurent Jourdren
 */
public class ImaGeneFeatureFlagTest extends QualityUnitTest implements Module {

  /** Default value of the threshold. */
  private static final double DEFAULT_THRESHOLD = 5.0;
  private static AboutModule aboutModule;

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    if (aboutModule == null) {

      ModuleDescription md = null;
      try {
        md = new ModuleDescription("ImaGeneFeatureFlagTest",
            "Test feature flag");
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
              "Threshold of invalid spots to reject the test")
          .withGreaterThanValue(0).withDefaultValue("" + DEFAULT_THRESHOLD)
          .withUnit("%").getParameter();

      final Parameter filterFlags = new ParameterBuilder().withName(
          "filterFlags").withType(Parameter.DATATYPE_YESNO).withLongName(
          "Remove bad spots from output array list").withDescription(
          "Remove invalid spots in output arraylist file.").withDefaultValue(
          "yes").getParameter();

      final Parameter flagValue = new ParameterBuilder().withName("flagValue")
          .withType(Parameter.DATATYPE_INTEGER).withLongName(
              "Flag value to test").withDescription("Flag Value")
          .withDefaultValue("yes").getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(threshold);
      params.addParameter(flagValue);
      params.addParameter(filterFlags);

      return params;

    } catch (ParameterException e) {
      getLogger().error("Error while creating parameters: " + e);
    }

    return null;
  }

  // protected abstract String getFlagFilerType();

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
      // final int[] flags = bioassay.getFlags();

      final double threshold = parameters.getParameter("threshold")
          .getDoubleValue();

      final boolean filterFlags = parameters.getParameter("filterFlags")
          .getBooleanValue();

      final int flagValue = parameters.getParameter("flagValue").getIntValue();

      int countBad = 0;
      int countRealSpot = 0;

      SpotIterator si = bioassay.iterator();

      while (si.hasNext()) {
        si.next();

        /*
         * if (si.isEmpty() || (si.isFlagAbscent() && flagValue !=
         * BioAssay.FLAG_ABSCENT)) continue;
         */

        if (si.getFlag() == flagValue)
          countBad++;
        else
          results[si.getIndex()] = true;

        countRealSpot++;
      }

      final double ratio = ((double) countBad) / ((double) countRealSpot) * 100;

      result = new QualityUnitTestResult(bioassay, this);
      final long max = (long) (countRealSpot * threshold / 100);

      result.setMessage("Filtered features: " + countBad + "/" + countRealSpot
          + " features (threshold: " + max + " features)");

      result.setGlobalResultType(true);
      result.setFilterFlags(filterFlags);
      result.setNewFlags(results);
      SummaryResult rac = result.getResultAllChannels();
      rac.setPercent(true);
      rac.setThresholdEqualityType("<=");
      rac.setThreshold(threshold);
      rac.setUnit("%");
      rac.setValue(ratio);
      rac.setPercent(false);
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
  public ImaGeneFeatureFlagTest() throws PlatformException {
    // MUST BE EMPTY
  }
}