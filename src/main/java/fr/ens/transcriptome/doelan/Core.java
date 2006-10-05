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

package fr.ens.transcriptome.doelan;

import java.applet.Applet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.doelan.algorithms.DoelanConfigure;
import fr.ens.transcriptome.doelan.algorithms.DoelanExecuteGlobalTests;
import fr.ens.transcriptome.doelan.algorithms.DoelanGenerateReport;
import fr.ens.transcriptome.doelan.algorithms.DoelanLoadGenepixData;
import fr.ens.transcriptome.doelan.algorithms.DoelanShowReport;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteURL;
import fr.ens.transcriptome.doelan.gui.MainTabWidget;
import fr.ens.transcriptome.doelan.gui.ReportTabWidget;
import fr.ens.transcriptome.doelan.gui.StatusWidget;
import fr.ens.transcriptome.doelan.gui.TestSuitePanel;
import fr.ens.transcriptome.nividic.platform.Platform;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.PlatformRegistery;
import fr.ens.transcriptome.nividic.platform.workflow.SimpleWorkflowBuilder;
import fr.ens.transcriptome.nividic.platform.workflow.Workflow;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowElement;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowEvent;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowGraph;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowListener;
import fr.ens.transcriptome.nividic.platform.workflow.io.WorkflowIO;
import fr.ens.transcriptome.nividic.platform.workflow.io.WorkflowXMLIO;
import fr.ens.transcriptome.nividic.util.StringUtils;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;

/**
 * Manage the Workflows
 * @author Laurent Jourdren
 */
public final class Core implements WorkflowListener {

  // For log system
  private static Logger log = Logger.getLogger(Core.class);

  /** Singleton. */
  private static Core core;

  /** Identifier of the load algorithm. */
  public static final String LOAD_ALGORITHM = "DoelanLoadData";
  /** Identifier of the algorithm for executing global test. */
  public static final String EXECUTE_GLOBAL_TESTS_ALGORITHM = "DoelanExecuteGlobalTests";
  /** Identifier of the generate report algorithm. */
  public static final String GENERATE_REPORT_ALGORITHM = "DoelanGenerateReport";
  /** Identifier of the show report algorithm. */
  public static final String SHOW_REPORT_ALGORITHM = "DoelanShowReport";
  /** Identifier of the show report algorithm. */
  public static final String CONFIGURE_ALGORITHM = "DoelanConfigure";

  private static final int NUMBER_OF_DOELAN_ALGORITHM = 4;

  private String testSuiteName;
  private int countAlgorithm;
  private Workflow workflow;
  private MainTabWidget mainTab;
  private TestSuitePanel table;
  private ReportTabWidget report;
  private StatusWidget status;
  private Applet applet;
  private JLabel statusBar;
  private URL testSuiteListURL;
  private String lastFileChooserDir;

  //
  // Getters
  //

  /**
   * Get the last directory choose by the user.
   * @return a string with the last directory choose by the user
   */
  public String getLastFileChooserDir() {
    return lastFileChooserDir;
  }

  /**
   * Get the table.
   * @return Returns the table
   */
  public TestSuitePanel getTable() {
    return table;
  }

  /**
   * Get the workflow
   * @return Returns the workflow
   */
  private Workflow getWorkflow() {
    return workflow;
  }

  /**
   * Test if a suite is load in memory.
   * @return true if a suite is load in memory
   */
  public boolean isSuite() {
    return this.workflow != null;
  }

  /**
   * Get the report widget
   * @return Returns the editor
   */
  public ReportTabWidget getReport() {
    return report;
  }

  /**
   * Set the report widget.
   * @param report The repory to set
   */
  public void setReport(final ReportTabWidget report) {
    if (report != null)
      this.report = report;
  }

  /**
   * Test if the applet is enable.
   * @return Returns the appletMode
   */
  public boolean isAppletMode() {
    return this.applet != null;
  }

  /**
   * Get the applet.
   * @return Returns the applet
   */
  public Applet getApplet() {
    return applet;
  }

  /**
   * Get the status widget.
   * @return Returns the status
   */
  public StatusWidget getStatus() {
    return status;
  }

  /**
   * Get the test suite name.
   * @return Returns the testSuiteName
   */
  private String getTestSuiteName() {
    return testSuiteName;
  }

  /**
   * Get the status bar object.
   * @return Returns the statusBar
   */
  public JLabel getStatusBar() {
    return statusBar;
  }

  /**
   * Get the URL of the test suite list.
   * @return Returns the testSuiteListURL
   */
  public URL getTestSuiteListURL() {
    return testSuiteListURL;
  }

  /**
   * Get the main tab widget.
   * @return Returns the mainTab
   */
  public MainTabWidget getMainTab() {
    return mainTab;
  }

  //
  // Setters
  //

  /**
   * Set the table.
   * @param table The table to set
   */
  public void setTable(final TestSuitePanel table) {
    this.table = table;
  }

  /**
   * Set the workflow
   * @param workflow The workflow to set
   */
  private void setWorkflow(final Workflow workflow) {
    this.workflow = workflow;
  }

  /**
   * Set the applet.
   * @param applet The applet to set
   */
  public void setApplet(final Applet applet) {
    this.applet = applet;
  }

  /**
   * Set the status widget.
   * @param status The status to set
   */
  public void setStatus(final StatusWidget status) {
    this.status = status;
  }

  /**
   * Set the test suite name.
   * @param testSuiteName The testSuiteName to set
   */
  private void setTestSuiteName(final String testSuiteName) {
    this.testSuiteName = testSuiteName;
  }

  /**
   * Set the status bar object.
   * @param statusBar The statusBar to set
   */
  public void setStatusBar(final JLabel statusBar) {
    this.statusBar = statusBar;
  }

  /**
   * Set the URL of the test suite list.
   * @param testSuiteListURL The testSuiteListURL to set
   */
  public void setTestSuiteListURL(final URL testSuiteListURL) {
    this.testSuiteListURL = testSuiteListURL;
  }

  /**
   * Set the main tab widget
   * @param mainTab The mainTab to set
   */
  public void setMainTab(final MainTabWidget mainTab) {
    this.mainTab = mainTab;
  }

  /**
   * Set the last directory choose by the user.
   * @parm lastFileChooserDir with the last directory choose by the user
   */
  public void setLastFileChooserDir(final String lastFileChooserDir) {
    this.lastFileChooserDir = lastFileChooserDir;
  }

  //
  // WorkflowException Listener
  //

  //
  // Other methods
  //

  /**
   * Load an new workflow
   * @param url url of the testsuite to load
   * @throws DoelanException if error occurs while the initialization of the
   *           test suite.
   */
  public void loadNewWorkflow(final QualityTestSuiteURL url)
      throws DoelanException {

    if (url == null)
      throw new DoelanException("Url is null");

    log.info("Load workflow : " + url.getURL());

    if (url.getURL() == null) {
      createEmptyWorkflow();
      saveWorkflow(url);
    } else
      loadWorkflow(url);
    getTable().setUrl(url);
    activateWorflow();

  }

  /**
   * Save a workflow.
   * @param url of the testsuite to save
   * @throws DoelanException if error occurs while saving workflow
   */
  public void saveWorkflow(final QualityTestSuiteURL url)
      throws DoelanException {

    if (url == null)
      return;

    if (url.getURL() == null) {
      File f = new File(PlatformRegistery.getConfDirectory() + File.separator
          + System.currentTimeMillis() + ".rwf");
      try {
        url.setURL(f.toURL());
      } catch (MalformedURLException e) {
        log.error("invalid URL :" + e.getMessage());
      }
    }

    try {
      OutputStream os = new FileOutputStream(url.getURL().getFile());
      WorkflowIO wfio = new WorkflowXMLIO(os);
      wfio.write(getWorkflow());
      log.info("save workflow url=" + url.getURL().getFile());
    } catch (FileNotFoundException e) {
      throw new DoelanException("Error while writing the file : "
          + e.getMessage());
    } catch (PlatformException e) {
      throw new DoelanException("Error while writing the file : "
          + e.getMessage());
    }

  }

  /**
   * Create and activate the demo test suite.
   * @throws DoelanException if an error occurs while the initialization of the
   *           test suite.
   */
  public void createDemoWorkflow() throws DoelanException {

    SimpleWorkflowBuilder swb = new SimpleWorkflowBuilder();
    Workflow w = swb.addElement("DoelanLoadData", LOAD_ALGORITHM).addElement(
        "EmptySpotTest", "algo1").addElement("SpotDiameterTest", "algo2")
        .addElement(EXECUTE_GLOBAL_TESTS_ALGORITHM,
            EXECUTE_GLOBAL_TESTS_ALGORITHM).addElement(
            GENERATE_REPORT_ALGORITHM, GENERATE_REPORT_ALGORITHM).addElement(
            SHOW_REPORT_ALGORITHM, SHOW_REPORT_ALGORITHM).getWorkflow();

    try {
      w.getElement("algo1").getParameters().setParameter("threshold", "10");

      w.getElement("algo1").getParameters().setParameter("quorum", "10");

      w.getElement("algo2").getParameters().setParameter("min", "10");
      w.getElement("algo2").getParameters().setParameter("max", "50");
      w.getElement("algo2").getParameters().setParameter("quorum", "10");

    } catch (ParameterException e) {
      throw new DoelanException("Unable to set parameters");
    }

    setWorkflow(w);
  }

  /**
   * Create and activate the empty test suite.
   * @throws DoelanException if an error occurs while the initialization of the
   *           test suite.
   */
  public void createEmptyWorkflow() throws DoelanException {

    SimpleWorkflowBuilder swb = new SimpleWorkflowBuilder();
    Workflow w = swb.addElement(LOAD_ALGORITHM, LOAD_ALGORITHM).addElement(
        CONFIGURE_ALGORITHM, CONFIGURE_ALGORITHM).addElement(
        EXECUTE_GLOBAL_TESTS_ALGORITHM, EXECUTE_GLOBAL_TESTS_ALGORITHM)
        .addElement(GENERATE_REPORT_ALGORITHM, GENERATE_REPORT_ALGORITHM)
        .addElement(SHOW_REPORT_ALGORITHM, SHOW_REPORT_ALGORITHM).getWorkflow();

    w.setType(Defaults.DOELAN_WORKFLOW_TYPE);
    w.setGenerator(DoelanRegistery.getAppName() + " "
        + DoelanRegistery.getAppVersion());

    // w.setName(getTestSuiteName());
    w.getAnnotations().setProperty(
        Defaults.DOELAN_WORKFLOW_VERSION_ANNOTATION_KEY,
        Defaults.DOELAN_WORKFLOW_VERSION);

    /*
     * w.getAnnotations().setProperty(Defaults.CHIP_TYPE_ANNOTATION_KEY,
     * "Mouse");
     * w.getAnnotations().setProperty(Defaults.TEST_SUITE_NAME_ANNOTATION_KEY,
     * "22k");
     */

    setWorkflow(w);
  }

  /**
   * Load a workflow
   * @param url Location of the test suite
   * @throws DoelanException if an error occurs while reading the test suite
   */
  private void loadWorkflow(final QualityTestSuiteURL url)
      throws DoelanException {

    if (url == null || url.getURL() == null)
      throw new DoelanException("Url is null");

    try {
      WorkflowIO wfio = new WorkflowXMLIO(url.getURL().openStream());

      Workflow w = wfio.read();
      setWorkflow(w);
      setTestSuiteName(url.getName());

    } catch (FileNotFoundException e) {
      throw new DoelanException("Test suite file not found");
    } catch (IOException e) {
      throw new DoelanException("IO error while reading the test suite");
    } catch (PlatformException e) {
      throw new DoelanException("IO error while reading the test suite");
    }
  }

  /**
   * Test if a workflow is a valid testsuite.
   * @param workflow Workflow to test
   * @return true if the workflow is a valid workflow
   */
  private boolean isValidTestSuite(final Workflow workflow) {

    if (workflow == null)
      return false;

    if (workflow.getType() == null
        || !workflow.getType().equals(Defaults.DOELAN_WORKFLOW_TYPE)) {
      log.error("Invalid test suite: Not a doelan workflow type");
      return false;
    }

    WorkflowElement wfeLoad = workflow.getRootElement();

    // Test if load data algorithm is the first
    if (wfeLoad == null || wfeLoad.getAlgorithm() == null
        || !(wfeLoad.getAlgorithm() instanceof DoelanLoadGenepixData)
        || !wfeLoad.getId().equals(LOAD_ALGORITHM)) {
      log
          .error("Invalid test suite: load data algorithmn isn't the first element");
      return false;
    }

    WorkflowElement wfeConfigure = wfeLoad.getNextElements()[0];
    if (!(wfeConfigure.getAlgorithm() instanceof DoelanConfigure)
        || !wfeConfigure.getId().equals(CONFIGURE_ALGORITHM)) {
      log
          .error("Invalid test suite: configure algorithm isn't the sedond element");
      return false;
    }

    // Test if report algorithm is the last
    WorkflowElement[] endElements = new WorkflowGraph(workflow)
        .getEndElementsOfTheGraph();

    if (endElements == null || endElements.length != 1) {
      log.error("Invalid test suite: no end element");
      return false;
    }

    WorkflowElement wfeShowReport = endElements[0];
    if (!(wfeShowReport.getAlgorithm() instanceof DoelanShowReport)
        || !wfeShowReport.getId().equals(SHOW_REPORT_ALGORITHM)) {
      log.error("Invalid test suite: show report isn't the last element");
      return false;
    }

    WorkflowElement wfeGenerateReport = wfeShowReport.getPreviousElements()[0];
    if (!(wfeGenerateReport.getAlgorithm() instanceof DoelanGenerateReport)
        || !wfeGenerateReport.getId().equals(GENERATE_REPORT_ALGORITHM)) {
      log.error("Invalid test suite: generate report isn't the n-1 element");
      return false;
    }

    WorkflowElement wfeExecuteGlobalTests = wfeGenerateReport
        .getPreviousElements()[0];
    if (!(wfeExecuteGlobalTests.getAlgorithm() instanceof DoelanExecuteGlobalTests)
        || !wfeExecuteGlobalTests.getId()
            .equals(EXECUTE_GLOBAL_TESTS_ALGORITHM)) {
      log
          .error("Invalid test suite: execute global tests isn't the n-2 element");
      return false;
    }

    return true;
  }

  /**
   * Activate the test suite.
   * @throws DoelanException if an error occurs while activate the workflow
   */
  private void activateWorflow() throws DoelanException {

    final Workflow w = getWorkflow();

    if (w == null)
      return;

    try {
      // activate the workflow
      w.activate();

      if (!isValidTestSuite(w))
        throw new DoelanException("invalid test suite");

      WorkflowElement wfeLoad = workflow.getRootElement();
      WorkflowElement wfeShowReport = new WorkflowGraph(workflow)
          .getEndElementsOfTheGraph()[0];

      // Active the applet mode
      if (isAppletMode()) {
        DoelanLoadGenepixData mlgd = (DoelanLoadGenepixData) wfeLoad
            .getAlgorithm();
        mlgd.setApplet(getApplet());
      }

      // Link the report tab with the workflow
      DoelanShowReport mr = (DoelanShowReport) wfeShowReport.getAlgorithm();
      mr.setReport(getReport());
      mr.setStatus(getStatus());

      // Link the workflow with the table
      getTable().getModel().setWorkflow(w);

    } catch (PlatformException e) {
      throw new DoelanException("Error while activate the test suite: "
          + e.getMessage());
    }

  }

  /**
   * Start the workflow.
   * @param chipTypeName Name of the selected chipType
   * @param testSuiteName Name of the selected testSuite
   * @param gprFilename Filename of the GPR data
   * @param galFilename Filename of the GAL data
   * @param description Description of the file
   * @param applet applet used to load data
   * @param listener workflow listener
   * @throws DoelanException if an error occurs during the workflow
   */
  public void startWorkflow(final String chipTypeName,
      final String testSuiteName, final String[] gprFilename,
      final String galFilename, final String description, final Applet applet,
      final WorkflowListener listener) throws DoelanException {

    System.gc();

    // start the workflow
    if (getWorkflow() != null) {

      if (getWorkflow().size() < NUMBER_OF_DOELAN_ALGORITHM) {
        throw new DoelanException("Warning: Your test suite contains no test.");
      }

      // TODO here set the parameters of the workflow
      WorkflowElement wfe = getWorkflow().getRootElement();

      try {

        wfe.getParameters().setParameter("chipTypeName", chipTypeName);
        wfe.getParameters().setParameter("testSuiteName", testSuiteName);
        wfe.getParameters().setParameter(
            "gprFilenames",
            gprFilename == null ? "" : StringUtils
                .arrayStringtoString(gprFilename));
        wfe.getParameters().setParameter("galFilename",
            galFilename == null ? "" : galFilename);
        wfe.getParameters().setParameter("description", description);
        wfe.getParameters().setParameter("loadFromApplet",
            applet == null ? "false" : "true");

      } catch (ParameterException e) {
        throw new DoelanException("Invalid parameters ; " + e.getMessage());
      }

      // clear testsuite tab and report tab
      getReport().clear();
      getTable().clearResults();

      // for progress info
      this.countAlgorithm = 0;

      getWorkflow().addListener(listener);
      getWorkflow().addListener(this);
      setTestSuiteName(testSuiteName);
      showProgressWorkflow();

      new Thread(getWorkflow()).start();

    }
  }

  /**
   * Stop the workflow.
   * @throws PlatformException if the workflow is not running
   */
  public void stopWorkflow() throws PlatformException {
    getWorkflow().stop();
  }

  //
  // Algorithm Listener
  //

  /**
   * Throws an execption to a listener.
   * @param e Exception to throw.
   */
  public void workflowNewException(final PlatformException e) {
  }

  /**
   * Invoked when the target of the listener has changed its state.
   * @param event a WorkflowEvent object
   */
  public void workflowStateChanged(final WorkflowEvent event) {

    if (event == null)
      return;

    if (event.getId() == WorkflowEvent.END_ALGORITHM_EVENT)
      showProgressWorkflow();

  }

  /**
   * Show the progression of the workflow in the statusbar.
   */
  private void showProgressWorkflow() {

    if (getStatusBar() == null)
      return;

    String msg = getTestSuiteName();
    /*
     * + " executed with "; if (DoelanRegistery.isAppletMode()) msg = msg +
     * "Genepix data."; else msg = msg + getGprFilename();
     */

    if (countAlgorithm == getWorkflow().size()) {
      msg = msg + " - done";
      this.countAlgorithm = 0;
    } else {

      int percent = (int) ((double) countAlgorithm
          / (double) getWorkflow().size() * 100);
      msg = msg + " - " + percent + "% done";
      this.countAlgorithm++;
    }

    getStatusBar().setText(msg);
  }

  /**
   * Return the unique instance of the Core object
   * @return The instance of the core object
   */
  public static Core getCore() {

    if (core == null)
      core = new Core();
    return core;
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private Core() {

    // Start the platform
    Platform.start(Defaults.INTERNALS_MODULES, new String[] {PlatformRegistery
        .getPluginsDirectory()}); // ,
    // !DoelanRegistery.isAppletMode());
    setLastFileChooserDir(DoelanRegistery.getDoelanDataDirectory());
  }

}
