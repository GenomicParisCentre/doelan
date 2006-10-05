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
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fr.ens.transcriptome.doelan.DoelanException;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteList;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteURL;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * @author Laurent Jourdren
 */
public class RenameTestSuiteWidget extends JDialog {

  private JPanel jPanel;

  private JButton okButton;
  private JButton cancelButton;
  private JPanel jPanel3;
  private JLabel newNameLegendLabel;
  private JTextField newNameTextField = new JTextField();;

  private JRadioButton testSuiteRadioButton;
  private JRadioButton chipTypeRadioButton;

  private JDialog dialog = this;

  private QualityTestSuiteList list;
  private String chipName;
  private QualityTestSuiteURL suite;
  private boolean chipTypeMode;

  //
  // Getters
  //

  /**
   * Get the test suite.
   * @return Returns the suite
   */
  private QualityTestSuiteURL getSuite() {
    return suite;
  }

  /**
   * Get the list.
   * @return Returns the list
   */
  private QualityTestSuiteList getList() {
    return list;
  }

  /**
   * Test if is in chip type mode.
   * @return Returns the chipTypeMode
   */
  public boolean isChipTypeMode() {
    return chipTypeMode;
  }

  //
  // Setters
  //

  /**
   * Set the test suite.
   * @param suite The suite to set
   */
  private void setSuite(final QualityTestSuiteURL suite) {
    this.suite = suite;
  }

  /**
   * Set the list.
   * @param list The list to set
   */
  private void setList(final QualityTestSuiteList list) {
    this.list = list;
  }

  /**
   * Set if the chip type mode is set.
   * @param chipTypeMode The chipTypeMode to set
   */
  public void setChipTypeMode(final boolean chipTypeMode) {
    this.chipTypeMode = chipTypeMode;
  }

  //
  // Other methods
  //

  //
  // Other methods
  //
  /**
   * This method initializes the dialog
   */
  private void initialize() {
    this.setTitle("Rename");
    this.setSize(300, 160);
    // this.setContentPane(getJContentPane());

    GridLayout gridLayout4 = new GridLayout(3, 1);

    getContentPane().setLayout(new BorderLayout());

    JPanel panel = new JPanel();

    panel.setLayout(gridLayout4);
    // gridLayout4.setRows(3);
    // gridLayout4.setColumns(1);
    // gridLayout4.setHgap(1);

    panel.add(getChipTypeRadioButton());

    panel.add(getTestSuiteRadioButton());
    // panel.add(new JLabel(" "));
    panel.add(getJPanel3());

    ButtonGroup group = new ButtonGroup();
    group.add(getChipTypeRadioButton());
    group.add(getTestSuiteRadioButton());

    if (getSuite() == null)
      setChipTypeMode(true);

    if (isChipTypeMode())
      getChipTypeRadioButton().setSelected(true);
    else
      getTestSuiteRadioButton().setSelected(true);

    updateEditedNames();

    getContentPane().add(panel, BorderLayout.CENTER);
    getContentPane().add(getJPanel(), BorderLayout.SOUTH);

    this.setResizable(false);
    this.setModal(true);

    setVisible(true);
    this.newNameTextField.requestFocus();

  }

  /**
   * Set the focus on the textField
   */
  public void requestFocus() {
    this.newNameTextField.requestFocus();
  }

  private void updateEditedNames() {

    if (isChipTypeMode()) {

      final String oldName;

      if (getName() == null)
        oldName = this.chipName;
      else
        oldName = getList().getChipType(getSuite());

      this.newNameTextField.setText(oldName);
    } else if (getSuite() != null)
      this.newNameTextField.setText(getSuite().getName());

  }

  /**
   * This method initializes jPanel
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel() {
    if (jPanel == null) {
      jPanel = new JPanel();
      jPanel.add(getOkButton(), null);
      jPanel.add(getCancelButton(), null);
    }
    return jPanel;
  }

  /**
   * This method initializes jButton
   * @return javax.swing.JButton
   */
  private JButton getOkButton() {
    if (okButton == null) {
      okButton = new JButton();
      okButton.setText("OK");
      if (!SystemUtils.isMacOsX())
        okButton.setMnemonic(KeyEvent.VK_O);
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(final java.awt.event.ActionEvent e) {

          if (isChipTypeMode()) {

            try {
              getList().renameChipType(RenameTestSuiteWidget.this.chipName,
                  newNameTextField.getText());
            } catch (DoelanException e1) {
              JOptionPane.showMessageDialog(getJPanel(), e1.getMessage());
            }

          } else {
            // getSuite().setName(newNameTextField.getText());

            try {
              getList().renameTestSuite(chipName, getSuite().getName(),
                  newNameTextField.getText());
            } catch (DoelanException e1) {
              JOptionPane.showMessageDialog(getJPanel(), e1.getMessage());
            }

          }

          hide();
        }
      });

    }

    return okButton;
  }

  /**
   * This method initializes jButton1
   * @return javax.swing.JButton
   */
  private JButton getCancelButton() {
    if (cancelButton == null) {
      cancelButton = new JButton();
      cancelButton.setText("Cancel");
      if (!SystemUtils.isMacOsX())
        cancelButton.setMnemonic(KeyEvent.VK_C);
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(final java.awt.event.ActionEvent e) {
          dialog.hide();
        }
      });
    }
    return cancelButton;
  }

  /**
   * This method initializes jPanel2
   * @return javax.swing.JPanel
   */

  /*
   * private JPanel getJPanel2() { if (jPanel2 == null) { oldNameLegendLabel =
   * new JLabel(); oldNameLabel = new JLabel(); FlowLayout flowLayout5 = new
   * FlowLayout(); jPanel2 = new JPanel(); jPanel2.setLayout(flowLayout5);
   * oldNameLegendLabel.setText("Old name:");
   * flowLayout5.setAlignment(java.awt.FlowLayout.LEFT);
   * jPanel2.add(oldNameLegendLabel, null); jPanel2.add(oldNameLabel, null); }
   * return jPanel2; }
   */

  /**
   * This method initializes jPanel3
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel3() {
    if (jPanel3 == null) {
      newNameLegendLabel = new JLabel();
      // FlowLayout flowLayout6 = new FlowLayout();

      jPanel3 = new JPanel();
      // SpringLayout layout = new SpringLayout();
      BorderLayout layout = new BorderLayout();
      jPanel3.setLayout(layout);

      newNameLegendLabel.setText("New name: ");
      newNameLegendLabel.setDisplayedMnemonic(KeyEvent.VK_N);
      newNameLegendLabel.setLabelFor(this.newNameTextField);

      jPanel3.add(newNameLegendLabel, BorderLayout.WEST);
      jPanel3.add(this.newNameTextField, BorderLayout.CENTER);

    }
    return jPanel3;
  }

  /**
   * This method initializes jRadioButton
   * @return javax.swing.JRadioButton
   */
  private JRadioButton getTestSuiteRadioButton() {
    if (testSuiteRadioButton == null) {
      testSuiteRadioButton = new JRadioButton();
      if (!SystemUtils.isMacOsX())
        testSuiteRadioButton.setMnemonic(KeyEvent.VK_T);
      if (getSuite() == null) {
        testSuiteRadioButton.setText("Test suite: ");
        testSuiteRadioButton.setEnabled(false);
      } else
        testSuiteRadioButton.setText("Test suite: " + getSuite().getName());

      testSuiteRadioButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent e) {
              setChipTypeMode(false);
              updateEditedNames();
            }
          });

    }
    return testSuiteRadioButton;
  }

  /**
   * This method initializes jRadioButton1
   * @return javax.swing.JRadioButton
   */
  private JRadioButton getChipTypeRadioButton() {
    if (chipTypeRadioButton == null) {
      chipTypeRadioButton = new JRadioButton();
      if (!SystemUtils.isMacOsX())
        chipTypeRadioButton.setMnemonic(KeyEvent.VK_H);

      chipTypeRadioButton.setText("Chip type: " + this.chipName);

      chipTypeRadioButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent e) {
              setChipTypeMode(true);
              updateEditedNames();
            }
          });

    }
    return chipTypeRadioButton;
  }

  /**
   * @param testSuiteRadioButton The testSuiteRadioButton to set
   */
  public void setTestSuiteRadioButton(final JRadioButton testSuiteRadioButton) {
    this.testSuiteRadioButton = testSuiteRadioButton;
  }

  //
  // Constructor
  //

  /**
   * This is the default constructor
   * @param list List of quality tests
   * @param chipName The name of the chip
   * @param suiteURL The URL of the suite
   */
  public RenameTestSuiteWidget(final QualityTestSuiteList list,
      final String chipName, final QualityTestSuiteURL suiteURL) {
    super();
    setList(list);
    setSuite(suiteURL);
    this.chipName = chipName;
    initialize();
  }

}