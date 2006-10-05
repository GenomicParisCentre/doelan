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

package fr.ens.transcriptome.doelan.data;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.algorithms.QualityGlobalTest;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class contains the report information of a testsuite.
 * @author Laurent Jourdren
 */
public class TestSuiteResult {

  private String htmlReport;
  private String chipTypeName;
  private String testSuiteName;
  private String description;
  private Image arrayPlot;
  private Set globalTest = new HashSet();
  private BioAssay newArrayList;
  private boolean result;
  private ArrayList testIds = new ArrayList();
  private String spotRejectedId = Defaults.REJECTED_SPOT_IDENTIFIER;
  private String[] emptySpotIds;

  /**
   * This class defines a object which contains a global test and its parameters
   * @author Laurent Jourdren
   */
  public class GlobalTest {

    private QualityGlobalTest test;
    private Parameters parameters;

    //
    // Getters
    //

    /**
     * Get the parameters.
     * @return Returns the parameters
     */
    public Parameters getParameters() {
      return parameters;
    }

    /**
     * Get the test.
     * @return Returns the test
     */
    public QualityGlobalTest getTest() {
      return test;
    }

    //
    // Setters
    //

    /**
     * Set the parameters
     * @param parameters The parameters to set
     */
    public void setParameters(final Parameters parameters) {
      this.parameters = parameters;
    }

    /**
     * Set the test.
     * @param test The test to set
     */
    public void setTest(final QualityGlobalTest test) {
      this.test = test;
    }

    //
    // Other methods
    //

    /**
     * Excecute the test with its parameters
     * @param container Container to put in the test
     * @throws PlatformException if no test exists
     */
    public void executeTest(final Container container) throws PlatformException {
      if (getTest() == null)
        throw new PlatformException("No test to run");
      getTest().doTest(container, getParameters());
    }

    //
    // Construtor
    //

    /**
     * Public constructor.
     * @param test Test to set
     * @param parameters Parameters to set
     */
    public GlobalTest(final QualityGlobalTest test, final Parameters parameters) {
      setTest(test);
      setParameters(parameters);

    }
  }

  //
  // Getters
  //

  /**
   * Get the name of the type of the chip.
   * @return Returns the chipTypeName
   */
  public String getChipTypeName() {
    return chipTypeName;
  }

  /**
   * Get the html report.
   * @return Returns the htmlReport
   */
  public String getHtmlReport() {
    return htmlReport;
  }

  /**
   * Get the test suite name.
   * @return Returns the testSuiteName
   */
  public String getTestSuiteName() {
    return testSuiteName;
  }

  /**
   * Get the arrayplot image.
   * @return Returns the arrayPlot
   */
  public Image getArrayPlot() {
    return arrayPlot;
  }

  /**
   * Get the new array list.
   * @return Returns the newArrayList
   */
  public BioAssay getNewArrayList() {
    return newArrayList;
  }

  /**
   * Set the new array list.
   * @param newArrayList The newArrayList to set
   */
  public void setNewArrayList(final BioAssay newArrayList) {
    this.newArrayList = newArrayList;
  }

  /**
   * Get the result of the test suite.
   * @return Returns the result
   */
  public boolean getResult() {
    return result;
  }

  /**
   * Get the identifier of teh rejected spots.
   * @return the identifier of teh rejected spots
   */
  public String getSpotRejectedId() {
    return spotRejectedId;
  }

  /**
   * Get the data description
   * @return the description of the testsuite
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get the identifiers of the empty spots.
   * @return The identifiers of the empty spots
   */
  public String[] getEmptySpotIds() {
    return emptySpotIds;
  }

  //
  // Setters
  //

  /**
   * Set the identifiers of the empty spots.
   * @param emptySpotIds The identifiers of the empty spots
   */
  public void setEmptySpotIds(final String[] emptySpotIds) {
    this.emptySpotIds = emptySpotIds;
  }

  /**
   * Set the name of the type of the chip.
   * @param chipTypeName The chipTypeName to set
   */
  public void setChipTypeName(final String chipTypeName) {
    this.chipTypeName = chipTypeName;
  }

  /**
   * Set the code of the html report.
   * @param htmlReport The htmlReport to set
   */
  public void setHtmlReport(final String htmlReport) {
    this.htmlReport = htmlReport;
  }

  /**
   * Set the test suite name.
   * @param testSuiteName The name of the testsuite
   */
  public void setTestSuiteName(final String testSuiteName) {
    this.testSuiteName = testSuiteName;
  }

  /**
   * Set the arrayplot image.
   * @param arrayPlot The arrayPlot to set
   */
  public void setArrayPlot(final Image arrayPlot) {
    this.arrayPlot = arrayPlot;
  }

  /**
   * Set the result of the test suite.
   * @param result The result to set
   */
  public void setResult(final boolean result) {
    this.result = result;
  }

  /**
   * Set the identifier for the rejected spots.
   * @param spotRejectedId The identifier of the rejected spots
   */
  public void setSpotRejectedId(final String spotRejectedId) {
    this.spotRejectedId = spotRejectedId;
  }

  /**
   * Set the data description
   * @param description Data description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  //
  // Other methods
  //

  /**
   * Add a global test.
   * @param test Test to add
   * @param parameters Parameters of the global test
   */
  public void addGlobalTest(final QualityGlobalTest test,
      final Parameters parameters) {

    GlobalTest gt = new GlobalTest(test, parameters);

    this.globalTest.add(gt);
  }

  /**
   * Add a global test.
   * @param test Test to add
   */
  public void addGlobalTest(final GlobalTest test) {
    this.globalTest.add(test);
  }

  /**
   * Get the global tests
   * @return The global tests
   */
  public Set getGlobalTests() {
    return this.globalTest;
  }

  //
  // Other methods
  //

  /**
   * Add a test id for the test order in the report.
   * @param testId Test id to add
   */
  public void addTestId(final String testId) {
    this.testIds.add(testId);
  }

  /**
   * Get the order of the tests.
   * @return The order of the test
   */
  public String[] getTestIdsOrder() {

    String[] result = new String[this.testIds.size()];
    this.testIds.toArray(result);

    return result;
  }

  //
  // Constructor
  //
  /**
   * Public Constructor.
   * @param chipTypeName The chipTypeName to set
   * @param testSuiteName The name of the testsuite
   * @param description Data description
   */
  public TestSuiteResult(final String chipTypeName, final String testSuiteName,
      final String description) {
    setChipTypeName(chipTypeName);
    setTestSuiteName(testSuiteName);
    setDescription(description);
  }

}