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
 * Copyright (c) 2004-2005 ENS
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the Doelan project and its aims,
 * or to join the Doelan mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/doelan
 *
 */

import fr.ens.transcriptome.doelan.algorithms.QualityUnitTest;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult.SummaryResult;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.SpotIterator;
import fr.ens.transcriptome.nividic.util.parameter.FixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterBuilder;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * Plugin Sample Test.
 * This class define a test based on not found spot flags.
 * @author Laurent Jourdren
 */
public class PluginSampleTest extends QualityUnitTest implements Module {

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("PluginSampleTest",
          "Plugin sample Test : Test not found spot flag for BioAssay");
    } catch (PlatformException e) {
      getLogger().error("Unable to create the module description");
    }
    return md;
  }

  /**
   * Set the parameters of the element.
   * @return The defaults parameters to set.
   */
  protected Parameters defineParameters() {

    try {

      final Parameter threshold = new ParameterBuilder().withName("Threshold")
          .withType(Parameter.DATATYPE_DOUBLE).withDescription(
              "Threshold for the test").withGreaterThanValue(0).withDefaultValue(
              "0.10").getParameter();
      final Parameter filterFlags = new ParameterBuilder().withName(
              "Filter flags").withType(Parameter.DATATYPE_BOOLEAN).withDescription(
              "Filter invalid features in output arraylist file").withDefaultValue(
              "false").getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(threshold);
      params.addParameter(filterFlags);

      return params;

    } catch (ParameterException e) {
      getLogger().error("Error while creating parameters : " + e);
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

      final double threshold = parameters.getParameter("Threshold").getDoubleValue();
      final boolean filterFlags = parameters.getParameter("Filter flags").getBooleanValue();

      int countNotFound = 0;
      int countRealSpot = 0;

      SpotIterator si = bioassay.iterator();

      while (si.hasNext()) {
        si.next();

        if (si.isEmpty() || si.isFlagAbscent())
          continue;

        if (si.getFlag() == BioAssay.FLAG_NOT_FOUND)
          countNotFound++;
        else
          results[si.getIndex()] = true;

        countRealSpot++;
      }


      final double ratio = ((double) countNotFound) / ((double) countRealSpot);

      result = new QualityUnitTestResult(bioassay, this);
      result.setMessage("Not found flag features : " + countNotFound + " / "
          + countRealSpot + " max : " + (countRealSpot * threshold));

      result.setGlobalResultType(true);
      if (filterFlags) result.setNewFlags(results);

      SummaryResult rac = result.getResultAllChannels();
      rac.setPercent(true);
      rac.setThresholdEqualityType("<=");
      rac.setUnit("%");
      rac.setThreshold(threshold);
      rac.setValue(ratio);
      rac.setPass(ratio <= threshold);

    } catch (ParameterException e) {
      throw new PlatformException("Error while creating parameters ("
          + this.getClass().getName() + ") : " + e.getMessage());
    }

    return result;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *                 <b>null </b>.
   */
  public PluginSampleTest() throws PlatformException {
    // MUST BE EMPTY
  }
}
