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
import fr.ens.transcriptome.doelan.algorithms.QualityGlobalTest;
import fr.ens.transcriptome.doelan.data.QualityGlobalTestResult;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
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
 * This class defines an algorithm to count the maximal bad spots in a test
 * suite.
 * @author Laurent Jourdren
 */
public class GlobalMaxBadFeaturesTest extends QualityGlobalTest implements
    Module {

  private static AboutModule aboutModule;

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    if (aboutModule == null) {

      ModuleDescription md = null;
      try {
        md = new ModuleDescription("GlobalMaxBadFeaturesTest",
            "Count the maximal bad spots in a test suite");
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

      final FixedParameters params = new FixedParameters();
      params.addParameter(threshold);

      return params;

    } catch (ParameterException e) {
      getLogger().error("Error while creating parameters: " + e);
    }

    return null;
  }

  /**
   * Do the test.
   * @param parameters Parameters of the test
   * @param unitResults results of the units tests
   * @throws PlatformException if an error occurs while executing the test.
   */
  protected QualityGlobalTestResult test(final BioAssay bioassay,
      final BioAssay arrayList, final Parameters parameters,
      final QualityUnitTestResult[] unitResults) throws PlatformException {

    if (parameters == null || unitResults == null)
      return null;

    try {
      final double threshold = parameters.getParameter("threshold")
          .getDoubleValue();

      int countArrays = 0;

      for (int i = 0; i < unitResults.length; i++) {
        if (unitResults[i].getNewFlags() != null)
          countArrays++;
      }

      boolean[][] badspots = new boolean[countArrays][];
      countArrays = 0;

      for (int i = 0; i < unitResults.length; i++) {
        boolean[] nf = unitResults[i].getNewFlags();
        if (nf != null)
          badspots[countArrays++] = nf;
      }

      int countRealSpot = 0;
      int countBad = 0;

      SpotIterator si = bioassay.iterator();
      int i = 0;

      while (si.hasNext()) {
        si.next();
        if (si.isEmpty() || si.isFlagAbscent()) {
          i++;
          continue;
        }

        boolean result = true;
        for (int j = 0; j < badspots.length; j++)
          result = result && badspots[j][i];

        if (!result)
          countBad++;
        countRealSpot++;
        i++;
      }

      final double ratio = ((double) countBad) / ((double) countRealSpot) * 100;
      long maxThreshold = (long) (countRealSpot * threshold / 100.0);

      final String message = "Bad diameter features: " + countBad + "/"
          + countRealSpot + " features (threshold: " + maxThreshold
          + " features)";

      QualityGlobalTestResult testResult = new QualityGlobalTestResult(
          bioassay, this);
      //testResult.setResult(false);
      testResult.setPercent(true);
      testResult.setThresholdEqualityType("<=");
      testResult.setUnit("%");
      testResult.setThreshold(threshold);
      testResult.setValue(ratio);
      testResult.setResult(ratio <= threshold);
      testResult.setMessage(message);

      return testResult;

    } catch (ParameterException e) {

      getLogger().error(
          "Error while creating parameters (" + this.getClass().getName()
              + "): " + e.getMessage());
    }

    return null;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *                 <b>null </b>.
   */
  public GlobalMaxBadFeaturesTest() throws PlatformException {
    // MUST BE EMPTY
  }

}
