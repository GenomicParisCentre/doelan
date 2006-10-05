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
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResultData;
import fr.ens.transcriptome.doelan.data.TestSuiteResult;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.platform.workflow.SimpleAlgorithmEvent;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class define a quality unit test.
 * @author Laurent Jourdren
 */
public abstract class QualityUnitTest extends QualityTest {

  protected void doIt(final Container c, final Parameters parameters)
      throws PlatformException {

    BioAssay bioassay = DoelanDataUtils.getBioAssay(c);
    BioAssay arraylist = DoelanDataUtils.getArrayList(c);
    TestSuiteResult tsr = DoelanDataUtils.getTestSuiteResult(c);

    QualityUnitTestResult result = test(bioassay, arraylist, parameters);

    tsr.addTestId(result.getTestId());

    if (result == null)
      throw new PlatformException("The result of " + getId() + " ("
          + aboutModule().getName() + ") is null");

    c.add(new QualityUnitTestResultData(result));

    sendEvent(new SimpleAlgorithmEvent(this, RESULT_EVENT, result,
        "result data"));
  }

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
    return false;
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

  //
  // Abstract methods
  //

  /**
   * Test the quality of the bioassay.
   * @param bioassay BioAssay to test
   * @param parameters Parameters of the test
   * @param arrayList The array list
   * @return A QualityObjectResultTest Object
   * @throws PlatformException if an error occurs while executing the test.
   */
  protected abstract QualityUnitTestResult test(final BioAssay bioassay,
      final BioAssay arrayList, final Parameters parameters)
      throws PlatformException;

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *                 <b>null </b>.
   */
  public QualityUnitTest() throws PlatformException {
    // MUST BE EMPTY
  }
}