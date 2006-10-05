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

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The testsuite panel
 * @author Laurent Jourdren
 */
public class TestSuiteTabWidget extends JPanel {

  private static TestSuiteTabWidget widget;
  private JLabel chipTypeLabel = new JLabel();
  private JLabel testSuiteLabel = new JLabel();
  private JPanel labelPane = new JPanel();
  private TestSuitePanel table = new TestSuitePanel();

  /**
   * Set the chip type to display.
   * @param chipType The name of the chip type to display
   */
  public void setChipType(final String chipType) {

    String msg = "Chip type: ";
    if (chipType == null)
      msg += "no chip type selected";
    else
      msg += chipType;

    this.chipTypeLabel.setText(msg);
  }

  /**
   * Set the test suite to display.
   * @param testSuite The name of the test suite to display
   */
  public void setTestSuite(final String testSuite) {

    String msg = "Test suite: ";
    if (testSuite == null)
      msg += "no testsuite selected";
    else
      msg += testSuite;

    this.testSuiteLabel.setText(msg);
  }

  /**
   * Get the table widget.
   * @return The table widget
   */
  public TestSuitePanel getTable() {
    return this.table;
  }

  private void init() {

    chipTypeLabel.setHorizontalAlignment(JLabel.CENTER);
    testSuiteLabel.setHorizontalAlignment(JLabel.CENTER);

    labelPane.setLayout(new GridLayout(1, 2));
    labelPane.add(chipTypeLabel);
    labelPane.add(testSuiteLabel);

    setLayout(new BorderLayout());
    add(labelPane, BorderLayout.NORTH);
    add(table, BorderLayout.CENTER);
  }

  //
  // Static method
  //

  public static TestSuiteTabWidget getTestSuiteTabWidget() {
    return widget;
  }

  //
  // Constructor
  //

  /**
   * Public constructor
   */
  public TestSuiteTabWidget() {
    widget = this;
    init();
    setChipType(null);
    setTestSuite(null);
  }

}