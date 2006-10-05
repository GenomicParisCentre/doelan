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

package fr.ens.transcriptome.doelan.gui;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.doelan.Core;
import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.DoelanException;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteList;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteURL;
import fr.ens.transcriptome.doelan.io.QualityTestIOFactory;
import fr.ens.transcriptome.doelan.io.QualityTestSuiteListIO;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowEvent;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowListener;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * Main Panel.
 * @author Laurent Jourdren
 */
public class MainTabWidget extends JPanel implements WorkflowListener {

  // For log system
  private static Logger log = Logger.getLogger(MainTabWidget.class);

  private SuiteChooserWidget chooser;
  private FileChooserWidget gprFileChooser;
  private FileChooserWidget galFileChooser;
  private DescriptionWidget descriptionWidget;

  // private URL testSuiteListURL;

  private javax.swing.JPanel commandPanel;
  private javax.swing.JButton startButton;
  private javax.swing.JButton stopButton;

  private StatusWidget status;
  private QualityTestSuiteURL qtsURL;
  private boolean working;

  //
  // Getters
  //

  /**
   * Test if the application is working.
   * @return true if the application is working
   */
  private boolean isWorking() {
    return working;
  }

  //
  // Setters
  //

  /**
   * Set the state of the application.
   * @param working The state of the application
   */
  public void setWorking(final boolean working) {
    this.working = working;
    workingButtons();
  }

  //
  // Other methods
  //

  /**
   * Show the enable/disable state of the start/stop buttons.
   */
  private void workingButtons() {

    if (isWorking()) {
      startButton.setEnabled(false);
      if (Core.getCore().getTable() != null)
        Core.getCore().getTable().setStartButtonEnable(false);
      stopButton.setEnabled(true);
    } else {
      startButton.setEnabled(true);
      if (Core.getCore().getTable() != null)
        Core.getCore().getTable().setStartButtonEnable(true);
      stopButton.setEnabled(false);
    }

    startButton.repaint();
    stopButton.repaint();
  }

  private void init() {

    // startPanel = new javax.swing.JPanel();

    commandPanel = new javax.swing.JPanel();
    startButton = new javax.swing.JButton();
    stopButton = new javax.swing.JButton();

    setLayout(new java.awt.BorderLayout());

    add(new AboutWidget(), java.awt.BorderLayout.NORTH);

    startButton.setText("Start");
    startButton.setToolTipText("Press this button to launch the test suite");
    startButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        start();
      }
    });
    commandPanel.add(startButton);

    stopButton.setText("Stop");
    stopButton
        .setToolTipText("Press this button to stop the running test suite");
    stopButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        stop();
      }
    });
    commandPanel.add(stopButton);

    if (!SystemUtils.isMacOsX()) {
      startButton.setMnemonic(KeyEvent.VK_S);
      stopButton.setMnemonic(KeyEvent.VK_P);
    }

    workingButtons();

    add(commandPanel, java.awt.BorderLayout.SOUTH);

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new java.awt.GridBagLayout());

    gprFileChooser = new FileChooserWidget(!DoelanRegistery.isAppletMode(),
        "Select the GenePix file to analyzed:", KeyEvent.VK_G,
        "Choose Result File", centerPanel, 0);

    gprFileChooser.addFiletype("Imagene Output File", ".txt", true);
    gprFileChooser.addFiletype("Genepix Result", ".gpr", false);

    gprFileChooser.init();

    // Genepix Result :
    galFileChooser = new FileChooserWidget(!DoelanRegistery.isAppletMode(),
        "Load the Gene Array List (optional):", KeyEvent.VK_L,
        "Choose Array List File", centerPanel, 2);

    galFileChooser.addFiletype("Imagene Gene ID File", "", false);
    galFileChooser.addFiletype("Genepix Array List", ".gal", false);

    galFileChooser.init();

    // Array List :

    // ligne vide
    GridBagConstraints gridBagConstraints;
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.ipady = 40;
    centerPanel.add(new JLabel(""), gridBagConstraints);

    // Description
    descriptionWidget = new DescriptionWidget("Array description (optional):",
        KeyEvent.VK_D, centerPanel, 3);

    // ligne vide
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.ipady = 40;
    centerPanel.add(new JLabel(""), gridBagConstraints);

    chooser = new SuiteChooserWidget(this, centerPanel, 6);

    // startPanel.add(chooser, java.awt.BorderLayout.CENTER);
    add(centerPanel, java.awt.BorderLayout.CENTER);

    // ligne vide
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.ipady = 40;
    centerPanel.add(new JLabel(""), gridBagConstraints);

    // Description
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.ipady = 40;

    status = new StatusWidget(centerPanel, 10);

    Core.getCore().setStatus(status);

    loadTestSuiteList();
  }

  /**
   * Load the list of testsuites
   */
  private void loadTestSuiteList() {

    String file = DoelanRegistery.getTestSuiteListFilename();

    log.info("Load test suite list file: " + file);

    // Read default test suite list file
    QualityTestSuiteListIO qtslio = new QualityTestIOFactory()
        .createQualityTestSuiteListIO(file);

    QualityTestSuiteList tsl = null;
    boolean oneOrMoreTestSuite;

    try {
      tsl = qtslio.read();
      chooser.setTestSuiteList(tsl);
      oneOrMoreTestSuite = tsl.isATestSuite();
    } catch (DoelanException e) {
      oneOrMoreTestSuite = false;
      chooser.setTestSuiteList(new QualityTestSuiteList());
      if (DoelanRegistery.isAppletMode())
        CommonWindow.showMessage(
            "Unable to read the list os test suites. Start "
                + Defaults.APP_NAME
                + " in standalone mode to create test suite lists.", true);
    }

    if (!oneOrMoreTestSuite) {

      if (DoelanRegistery.isAppletMode())
        CommonWindow.showMessage("No test suite found. Start "
            + Defaults.APP_NAME
            + " in standalone mode to create test suite lists.", true);

      else
        CommonWindow
            .showMessage("No test suite found.\n\n"
                + "Create a TestSuite and add your own test parameters, then\n"
                + "select your GPR and GAL files and press on Start button.",
                false);
    }
  }

  /**
   * Load the test suite.
   */
  void load() {

    if (Defaults.DEBUG) {
      try {

        Core.getCore().createDemoWorkflow();
        this.qtsURL = new QualityTestSuiteURL();
        this.qtsURL.setName("debug build-in test suite");
      } catch (DoelanException e) {
        CommonWindow.showMessage(e.getMessage(), true);
      }
    } else {
      // Load testsuite

      QualityTestSuiteURL url = chooser.getQualityTestSuiteSelected();

      if (url == null) {
        CommonWindow.showMessage("No test suite selected", false);
        setWorking(false);
        return;
      }

      if (!(this.qtsURL == url))
        try {
          Core.getCore().loadNewWorkflow(url);
        } catch (DoelanException e) {
          CommonWindow.showMessage(e.getMessage(), true);
        }

      this.qtsURL = url;
      this.status.setTestSuiteName(this.qtsURL.getName());
      CommonWindow
          .showStatusBarMessage(this.qtsURL.getName() + " suite loaded");

      final String chipType = this.chooser.getCurrentChipTypeName();
      final String testSuite = this.chooser.getCurrentTestSuiteName();

      TestSuiteTabWidget.getTestSuiteTabWidget().setChipType(chipType);
      TestSuiteTabWidget.getTestSuiteTabWidget().setTestSuite(testSuite);
    }

  }

  /**
   * Execute the test suite.
   */
  private void execute() {

    final Core core = Core.getCore();
    final StatusWidget status = StatusWidget.getStatusWidget();

    if (!core.isSuite()) {
      // showMessage("No suite loaded.", false);
      return;
    }

    status.setStatus(StatusWidget.STATUS_STARTED);

    final File[] gpr;
    final File gal;

    if (Defaults.DEBUG) {
      gpr = new File[1];
      gpr[0] = new File(
          "/home/jourdren/data/2003-11-25-lame20 ARNu12-11_0635.gpr");
      gal = new File("/home/jourdren/data/2003-11-25-lame20 ARNu12-11_0635.gpr");
    } else {
      gpr = gprFileChooser.getFiles();
      gal = galFileChooser.getFile();
    }

    log.info("gpr:"+gpr);
    
    if (gpr == null && !DoelanRegistery.isAppletMode()) {
      status.setStatus(StatusWidget.STATUS_NO_DATA_TO_TEST);

      if (!Core.getCore().isAppletMode())
        JOptionPane.showMessageDialog(this, "You must enter a Genepix file",
            "Message", JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    try {

      // this.chooser.getTestSuiteList().

      final String chipType = this.chooser.getCurrentChipTypeName();
      final String testSuite = this.chooser.getCurrentTestSuiteName();
      final String description = this.descriptionWidget.getDescription();

      // this.suitePanel.setChipType(chipType);
      // this.suitePanel.setTestSuite(testSuite);

      String[] gprFilenames = new String[gpr.length];
      for (int i = 0; i < gpr.length; i++)
        gprFilenames[i] = gpr[i].getAbsolutePath();

      core.startWorkflow(chipType, testSuite, gpr == null ? new String[] {""}
          : gprFilenames, gal == null ? "" : gal.getAbsolutePath(),
          description,
          DoelanRegistery.isAppletMode() ? core.getApplet() : null, this);

    } catch (DoelanException e) {

      log.error(e.getMessage());
      CommonWindow.showMessage(e.getMessage(), true);
      StatusWidget.getStatusWidget().setStatus(StatusWidget.STATUS_ERROR);
    }

  }

  /**
   * Start analysis.
   */
  public void start() {
    load();
    execute();
  }

  private void stop() {

    try {
      Core.getCore().stopWorkflow();
    } catch (PlatformException e) {
      CommonWindow.showMessage(e.getMessage(), true);
    }

  }

  //
  // WorkflowExceptionListener
  //

  /**
   * Invoked when the target of the listener has changed its state.
   * @param event a WorkflowEvent object
   */
  public void workflowStateChanged(final WorkflowEvent event) {

    if (event == null)
      return;

    switch (event.getId()) {
    case WorkflowEvent.START_EVENT:
      setWorking(true);
      break;

    case WorkflowEvent.END_EVENT:
      setWorking(false);
      break;

    default:
      break;
    }

  }

  void showStartButton(final boolean show) {
    this.startButton.setEnabled(show);
  }

  /**
   * Throws an execption to a listener.
   * @param e Exception to throw.
   */
  public void workflowNewException(final PlatformException e) {

    log.error(e.getMessage());
    CommonWindow.showMessage(e.getMessage(), true);
    StatusWidget.getStatusWidget().setStatus(StatusWidget.STATUS_ERROR);
  }

  //
  // constructor
  //

  /**
   * Public constructor.
   */
  public MainTabWidget() {
    init();
  }

}