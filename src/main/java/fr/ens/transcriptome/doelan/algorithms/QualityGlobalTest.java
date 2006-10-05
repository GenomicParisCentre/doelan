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

package fr.ens.transcriptome.doelan.algorithms;

import fr.ens.transcriptome.doelan.data.DoelanDataUtils;
import fr.ens.transcriptome.doelan.data.TestSuiteResult;
import fr.ens.transcriptome.doelan.data.QualityGlobalTestResult;
import fr.ens.transcriptome.doelan.data.QualityGlobalTestResultData;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.platform.workflow.SimpleAlgorithmEvent;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class define a abstract global test. Tha aim of the a global test is
 * only to set global parameters for the DoelanGenerateReport algorithm.
 * @author Laurent Jourdren
 */
public abstract class QualityGlobalTest extends QualityTest {

  /**
   * Test if the test is deletable().
   * @return true if the test is deletable
   */
  public boolean isDeletable() {
    return true;
  }

  /**
   * Test if only one instance of the test could be created.
   * @return true if only one instance of the test could be created
   */
  public boolean isUniqueInstance() {
    return true;
  }

  /**
   * Test if the test is modifiable.
   * @return true if the test is modifiable
   */
  public boolean isModifiable() {
    return true;
  }

  /**
   * Test if the test could be showed.
   * @return true if the test could be showed
   */
  public boolean isShowable() {
    return true;
  }

  /**
   * Test if the test could be diplayed in the list of tests to add.
   * @return true if the test could be showed
   */
  public boolean isAddable() {
    return true;
  }

  protected void doIt(final Container c, final Parameters parameters)
      throws PlatformException {

    // Get test suite result
    TestSuiteResult testSuiteResult = DoelanDataUtils.getTestSuiteResult(c);
    testSuiteResult.addGlobalTest(this, parameters);

    sendEvent(new SimpleAlgorithmEvent(this, GLOBAL_TEST_INIT_EVENT, this
        .getId(), "set global parameter"));
  }

  /**
   * This method is called once all the unit tests was executed.
   * @param c Container
   * @param parameters Parameters of the test
   * @throws PlatformException if an error occurs during the test
   */
  public void doTest(final Container c, final Parameters parameters)
      throws PlatformException {

    BioAssay bioassay = DoelanDataUtils.getBioAssay(c);
    BioAssay arraylist = DoelanDataUtils.getArrayList(c);
    QualityUnitTestResult[] unitTestResults = DoelanDataUtils
        .getQualityUnitTestResults(c);

    TestSuiteResult tsr = DoelanDataUtils.getTestSuiteResult(c);
    tsr.addTestId(this.getId());

    QualityGlobalTestResult globalResult = test(bioassay, arraylist,
        parameters, unitTestResults);

    if (globalResult == null)
      throw new PlatformException("The result of " + getId() + " ("
          + aboutModule().getName() + ") is null");

    globalResult.setTestDescription(this.aboutModule().getShortDescription());
    QualityGlobalTestResultData data = new QualityGlobalTestResultData(
        globalResult);

    c.add(data);

    sendEvent(new SimpleAlgorithmEvent(this, RESULT_EVENT, globalResult,
        "result data"));
  }

  //
  // Abstract methods
  //

  /**
   * Do the test.
   * @param parameters Parameters of the test
   * @param bioassay BioAssay to test
   * @param arrayList The array list
   * @param unitResults results of the units tests
   * @throws PlatformException if an error occurs while executing the test.
   */
  protected abstract QualityGlobalTestResult test(final BioAssay bioassay,
      final BioAssay arrayList, final Parameters parameters,
      final QualityUnitTestResult[] unitResults) throws PlatformException;

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *                 <b>null </b>.
   */
  public QualityGlobalTest() throws PlatformException {
    // MUST BE EMPTY
  }

}
