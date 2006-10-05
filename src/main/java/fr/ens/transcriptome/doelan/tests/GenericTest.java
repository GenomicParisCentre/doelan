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
import fr.ens.transcriptome.nividic.om.GenepixResults;
import fr.ens.transcriptome.nividic.om.SpotIterator;
import fr.ens.transcriptome.nividic.om.io.GPRConverterFieldNames;
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
 * This class define a generic unit test
 * @author Laurent Jourdren
 */
public class GenericTest extends QualityUnitTest implements Module {

  private static final int LESSER_THAN_OR_EQUALS_TO = 1;
  private static final int LESSER_THAN = 2;
  private static final int EQUALS = 3;
  private static final int NOT_EQUALS = 4;
  private static final int GREATER_THAN_OR_EQUALS_TO = 5;
  private static final int GREATER_THAN = 6;
  private static AboutModule aboutModule;

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    if (aboutModule == null) {

      ModuleDescription md = null;
      try {

        md = new ModuleDescription("GenericTest",
            "Generic test for a column of GPR");
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

      final Parameter columnName = new ParameterBuilder()
          .withName("columnName").withLongName("Column name").withType(
              Parameter.DATATYPE_STRING).withDescription(
              "Name of the column to test").getParameter();

      final Parameter testToApply = new ParameterBuilder().withName(
          "testToApply").withLongName("Test").withType(
          Parameter.DATATYPE_STRING).withDescription("Test to apply")
          .withChoices(
              new String[] {"<", "<=", "equals", "not equals", ">=", ">"})
          .withDefaultValue("<").getParameter();

      final Parameter testValue = new ParameterBuilder().withName("testValue")
          .withLongName("Test value").withType(Parameter.DATATYPE_STRING)
          .withDescription("Test value").getParameter();

      final Parameter threshold = new ParameterBuilder().withName("threshold")
          .withLongName("Maximal threshold of bad spots").withType(
              Parameter.DATATYPE_DOUBLE).withDescription(
              "Threshold of invalid spots to reject the test")
          .withGreaterThanValue(0).withDefaultValue("10").withUnit("%")
          .getParameter();

      final Parameter filterFlags = new ParameterBuilder().withType(
          Parameter.DATATYPE_YESNO).withName("filterFlags").withLongName(
          "Remove bad spots from output array list").withDescription(
          "Remove invalid spots in output arraylist file.").withDefaultValue(
          "yes").getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(columnName);
      params.addParameter(testToApply);
      params.addParameter(testValue);
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

      String columnName = parameters.getParameter("columnName")
          .getStringValue();
      final String testToApply = parameters.getParameter("testToApply")
          .getStringValue();

      final double threshold = parameters.getParameter("threshold")
          .getDoubleValue();

      final String testValue = parameters.getParameter("testValue")
          .getStringValue();

      final boolean filterFlags = parameters.getParameter("filterFlags")
          .getBooleanValue();

      int intThreshold = 0;
      double doubleThreshold = 0;
      String stringThreshold = testValue.trim();

      int fieldType = -1;
      int testType = getTestType(testToApply);

      boolean somethingToTest = false;
      if (columnName != null) {
        columnName = columnName.trim();

        if (new GenepixResults(bioassay).isGPRData())
          columnName = new GPRConverterFieldNames()
              .getBioAssayFieldName(columnName);

        if (bioassay.isField(columnName)) {
          somethingToTest = true;

          fieldType = bioassay.getFieldType(columnName);

          switch (fieldType) {

          case BioAssay.DATATYPE_DOUBLE:

            try {
              doubleThreshold = Double.parseDouble(stringThreshold);
            } catch (NumberFormatException e) {
              somethingToTest = false;
            }
            break;

          case BioAssay.DATATYPE_INTEGER:

            try {
              intThreshold = Integer.parseInt(stringThreshold);
            } catch (NumberFormatException e) {
              somethingToTest = false;
            }
            break;

          default:
            break;
          }

        }
      }

      int countBad = 0;
      int countRealSpot = 0;

      SpotIterator si = bioassay.iterator();

      while (si.hasNext()) {
        si.next();

        if (si.isEmpty() || si.isFlagAbscent())
          continue;

        if (somethingToTest) {

          boolean bad = true;

          switch (fieldType) {

          case BioAssay.DATATYPE_DOUBLE:

            double dv = si.getDataFieldDouble(columnName);

            switch (testType) {
            case LESSER_THAN_OR_EQUALS_TO:

              if (dv <= doubleThreshold)
                bad = false;
              break;

            case LESSER_THAN:

              if (dv < doubleThreshold)
                bad = false;
              break;

            case EQUALS:

              if (dv == doubleThreshold)
                bad = false;
              break;

            case NOT_EQUALS:

              if (dv != doubleThreshold)
                bad = false;
              break;

            case GREATER_THAN_OR_EQUALS_TO:

              if (dv >= doubleThreshold)
                bad = false;
              break;

            case GREATER_THAN:

              if (dv > doubleThreshold)
                bad = false;
              break;

            default:
              break;
            }

            break;

          case BioAssay.DATATYPE_INTEGER:
            int iv = si.getDataFieldInt(columnName);

            switch (testType) {
            case LESSER_THAN_OR_EQUALS_TO:

              if (iv <= intThreshold)
                bad = false;
              break;

            case LESSER_THAN:

              if (iv < intThreshold)
                bad = false;
              break;

            case EQUALS:

              if (iv == intThreshold)
                bad = false;
              break;

            case NOT_EQUALS:

              if (iv != intThreshold)
                bad = false;
              break;

            case GREATER_THAN_OR_EQUALS_TO:

              if (iv >= intThreshold)
                bad = false;
              break;

            case GREATER_THAN:

              if (iv > intThreshold)
                bad = false;
              break;

            default:
              break;
            }
            break;

          case BioAssay.DATATYPE_STRING:
            String sv = si.getDataFieldString(columnName);

            switch (testType) {
            case LESSER_THAN_OR_EQUALS_TO:

              if (stringThreshold.compareTo(sv) >= 0)
                bad = false;
              break;

            case LESSER_THAN:

              if (stringThreshold.compareTo(sv) > 0)
                bad = false;
              break;

            case EQUALS:

              if (stringThreshold.compareTo(sv) == 0)
                bad = false;
              break;

            case NOT_EQUALS:

              if (stringThreshold.compareTo(sv) != 0)
                bad = false;
              break;

            case GREATER_THAN_OR_EQUALS_TO:

              if (stringThreshold.compareTo(sv) <= 0)
                bad = false;
              break;

            case GREATER_THAN:

              if (stringThreshold.compareTo(sv) < 0)
                bad = false;
              break;

            default:
              break;
            }
            break;

          default:
            break;
          }

          if (bad)
            countBad++;
          else
            results[si.getIndex()] = true;
        } else
          results[si.getIndex()] = true;

        countRealSpot++;
      }

      final double ratio = ((double) countBad) / ((double) countRealSpot) * 100;

      result = new QualityUnitTestResult(bioassay, this);
      final long max = (long) (countRealSpot * threshold / 100);

      if (somethingToTest)

        result.setMessage("Bad features: " + countBad + "/" + countRealSpot
            + " features (threshold: " + max + " features)");

      else
        result.setMessage("Invalid field or invalid test value.");

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

  private int getTestType(final String test) {

    if (test == null)
      return -1;

    String t = test.trim();

    if ("<=".equals(t))
      return LESSER_THAN_OR_EQUALS_TO;
    else if ("<".equals(t))
      return LESSER_THAN;
    else if ("equals".equals(t))
      return EQUALS;
    else if ("not equals".equals(t))
      return NOT_EQUALS;
    else if (">=".equals(t))
      return GREATER_THAN_OR_EQUALS_TO;
    else if (">".equals(t))
      return GREATER_THAN;

    return -1;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *           <b>null </b>.
   */
  public GenericTest() throws PlatformException {
    // MUST BE EMPTY
  }

}
