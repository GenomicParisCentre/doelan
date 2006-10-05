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

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.ens.transcriptome.doelan.Core;

/**
 * Status widget.
 * @author Laurent Jourdren
 */
public class StatusWidget {

  /** Testsuite not started. */
  public static final int STATUS_NOT_STARTED = 0;
  /** Testsuite started. */
  public static final int STATUS_STARTED = 1;
  /** Testsuite success. */
  public static final int STATUS_PASS = 2;
  /** Testsuite failled. */
  public static final int STATUS_FAILLED = 3;
  /** Testsuite no data to test. */
  public static final int STATUS_NO_DATA_TO_TEST = 4;
  /** Testsuite error. */
  public static final int STATUS_ERROR = 5;

  /** Variable for static accessor. */
  private static StatusWidget statusWidget;

  private JLabel statusLabel = new JLabel();
  private JLabel testSuiteLabel = new JLabel();
  private int status;

  private JPanel panel;
  private int yPosition;

  //
  // Getters
  //

  /**
   * Get the status of the testsuite.
   * @return The status of the testsuite
   */
  public int getStatus() {
    return status;
  }

  /**
   * Get the panel.
   * @return Returns the panel
   */
  public JPanel getPanel() {
    return panel;
  }

  /**
   * Get the y position of the panel.
   * @return Returns the yPosition
   */
  public int getYPosition() {
    return yPosition;
  }

  //
  // Setters
  //

  /**
   * Set the status of the testsuite.
   * @param status The status of the testsuite
   */
  public void setStatus(final int status) {

    this.status = status;
    statusLabel.setForeground(Color.black);

    switch (status) {
    case STATUS_NOT_STARTED:
      statusLabel.setForeground(Color.black);
      statusLabel.setText("Not started");

      break;
    case STATUS_STARTED:
      statusLabel.setForeground(Color.black);
      statusLabel.setText("Started");

      break;
    case STATUS_PASS:
      statusLabel.setForeground(Color.green);
      statusLabel.setText("This scan pass successfully all the tests");

      break;
    case STATUS_FAILLED:
      statusLabel.setForeground(Color.red);
      statusLabel.setText("This scan fail to pass one or more test");
      break;

    case STATUS_NO_DATA_TO_TEST:
      statusLabel.setForeground(Color.red);
      statusLabel.setText("You must enter a Genepix file");
      break;

    case STATUS_ERROR:
      statusLabel.setForeground(Color.black);
      statusLabel.setText("Error !!!");

      break;

    default:
      break;
    }

  }

  /**
   * Set the y position of the widget.
   * @param position The yPosition to set
   */
  public void setYPosition(final int position) {
    yPosition = position;
  }

  /**
   * Set the panel.
   * @param panel The panel to set
   */
  public void setPanel(final JPanel panel) {
    this.panel = panel;
  }

  /**
   * Set the test suite name.
   * @param testSuiteName The testSuiteName to set
   */
  public void setTestSuiteName(final String testSuiteName) {

    if (testSuiteName == null) {
      testSuiteLabel.setText("no test suite selected");
      testSuiteLabel.setEnabled(false);
    } else {
      testSuiteLabel.setText(testSuiteName);
      testSuiteLabel.setEnabled(true);
    }

  }

  //
  // Other methods
  //

  private void init() {

    GridBagConstraints gridBagConstraints;

    JLabel jLabel1 = new JLabel("Test suite loaded:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = getYPosition();
    getPanel().add(jLabel1, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = getYPosition();
    gridBagConstraints.gridwidth = 5;
    getPanel().add(testSuiteLabel, gridBagConstraints);

    JLabel jLabel2 = new JLabel("Test suite result:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = getYPosition() + 1;
    getPanel().add(jLabel2, gridBagConstraints);

    setStatus(STATUS_NOT_STARTED);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = getYPosition() + 1;
    gridBagConstraints.gridwidth = 5;
    getPanel().add(statusLabel, gridBagConstraints);

  }

  //
  // Static method
  //

  /**
   * Get the status Widget.
   */
  public static StatusWidget getStatusWidget() {
    return statusWidget;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param panel Panel of the widget
   * @param position of the widget in the panel
   */
  public StatusWidget(final JPanel panel, final int position) {

    statusWidget = this;
    setPanel(panel);
    setYPosition(position);
    setTestSuiteName(null);
    init();
  }

}