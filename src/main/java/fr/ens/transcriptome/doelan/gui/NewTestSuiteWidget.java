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
import java.awt.FlowLayout;
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

import org.apache.log4j.Logger;

import fr.ens.transcriptome.doelan.Core;
import fr.ens.transcriptome.doelan.DoelanException;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteList;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteURL;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * Widget to create a new test suite list.
 * @author Laurent Jourdren
 */
public class NewTestSuiteWidget extends JDialog {

  private javax.swing.JPanel jContentPane;

  private JPanel jPanel;
  private JButton okButton;
  private JButton cancelButton;
  private JPanel jPanel1;
  private JPanel jPanel2;
  private JPanel jPanel3;
  private JRadioButton chipTypeRadioButton;
  private JRadioButton testSuiteRadioButton;
  private JPanel jPanel4;
  private JLabel jLabel;
  private JTextField jTextField;

  private QualityTestSuiteList list;
  private String chipType;

  // private boolean chipTypeMode;

  //
  // Getters
  //

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
  private boolean isChipTypeMode() {
    return getChipTypeRadioButton().isSelected();
  }

  /**
   * Get the chip type.
   * @return Returns the chipType
   */
  private String getChipType() {
    return chipType;
  }

  //
  // Setters
  //

  /**
   * Set the list.
   * @param list The list to set
   */
  private void setList(final QualityTestSuiteList list) {
    this.list = list;
  }

  /**
   * Set the chip type.
   * @param chipType The chipType to set
   */
  private void setChipType(final String chipType) {
    this.chipType = chipType;
  }

  //
  // Other methods
  //

  /**
   * This method initializes this
   * @return void
   */
  private void initialize() {
    this.setResizable(false);
    this.setModal(true);
    this.setBounds(0, 0, 300, 150);
    this.setTitle("Add");
    this.setContentPane(getJContentPane());

    ButtonGroup group = new ButtonGroup();
    group.add(getChipTypeRadioButton());
    group.add(getTestSuiteRadioButton());

    if (getList() == null || getList().getChipTypes() == null
        || getList().getChipTypes().length == 0) {
      getTestSuiteRadioButton().setEnabled(false);
      getChipTypeRadioButton().setSelected(true);
    } else if (isChipTypeMode())
      getChipTypeRadioButton().setSelected(true);
    else
      getTestSuiteRadioButton().setSelected(true);

  }

  /**
   * This method initializes jContentPane
   * @return javax.swing.JPanel
   */
  private javax.swing.JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new javax.swing.JPanel();
      jContentPane.setLayout(new java.awt.BorderLayout());
      jContentPane.add(getJPanel(), java.awt.BorderLayout.SOUTH);
      jContentPane.add(getJPanel1(), java.awt.BorderLayout.CENTER);
    }
    return jContentPane;
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
              getList().addChipType(getJTextField().getText());
            } catch (DoelanException e1) {
              JOptionPane.showMessageDialog(jPanel, e1.getMessage());
            }

            /*
             * if (getList() == null) setList(new QualityTestSuiteList());
             * boolean result =
             * getList().addChipType(getJTextField().getText()); if (!result)
             * JOptionPane.showMessageDialog(jPanel, "Chip type already
             * exists");
             */

          } else {

            QualityTestSuiteURL qtsURL = new QualityTestSuiteURL();
            qtsURL.setName(getJTextField().getText());

            try {
              getList().addTestSuite(getChipType(), qtsURL);

              Core.getCore().createEmptyWorkflow();
              Core.getCore().saveWorkflow(qtsURL);
            } catch (DoelanException e1) {
              JOptionPane.showMessageDialog(getJContentPane(), e1.getMessage());
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
          hide();
        }
      });

    }
    return cancelButton;
  }

  /**
   * This method initializes jPanel1
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel1() {
    if (jPanel1 == null) {
      GridLayout gridLayout6 = new GridLayout();
      jPanel1 = new JPanel();
      jPanel1.setLayout(gridLayout6);
      jPanel1.setPreferredSize(new java.awt.Dimension(124, 42));
      jPanel1.add(getJPanel2(), null);
      jPanel1.add(getJPanel3(), null);
      gridLayout6.setRows(3);
      gridLayout6.setColumns(1);
      jPanel1.add(getJPanel4(), null);
    }
    return jPanel1;
  }

  /**
   * This method initializes jPanel2
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel2() {
    if (jPanel2 == null) {
      jPanel2 = new JPanel();
      FlowLayout flowLayout2 = new FlowLayout();
      jPanel2.setLayout(flowLayout2);

      flowLayout2.setAlignment(java.awt.FlowLayout.LEFT);
      jPanel2.add(getChipTypeRadioButton(), null);
    }
    return jPanel2;
  }

  /**
   * This method initializes jPanel3
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel3() {
    if (jPanel3 == null) {
      jPanel3 = new JPanel();
      FlowLayout flowLayout1 = new FlowLayout();
      jPanel3.setLayout(flowLayout1);

      flowLayout1.setAlignment(java.awt.FlowLayout.LEFT);
      jPanel3.add(getTestSuiteRadioButton(), null);
    }
    return jPanel3;
  }

  /**
   * This method initializes jRadioButton1
   * @return javax.swing.JRadioButton
   */
  private JRadioButton getChipTypeRadioButton() {

    if (chipTypeRadioButton == null) {
      chipTypeRadioButton = new JRadioButton();
      chipTypeRadioButton.setText("Chip type");
      if (!SystemUtils.isMacOsX())
        chipTypeRadioButton.setMnemonic(KeyEvent.VK_H);
    }
    return chipTypeRadioButton;
  }

  /**
   * This method initializes jRadioButton
   * @return javax.swing.JRadioButton
   */
  private JRadioButton getTestSuiteRadioButton() {

    if (testSuiteRadioButton == null) {
      testSuiteRadioButton = new JRadioButton();
      testSuiteRadioButton.setText("Test suite");
      if (!SystemUtils.isMacOsX())
        testSuiteRadioButton.setMnemonic(KeyEvent.VK_T);
    }
    return testSuiteRadioButton;
  }

  /**
   * This method initializes jPanel4
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel4() {
    if (jPanel4 == null) {
      jLabel = new JLabel();
      // FlowLayout flowLayout3 = new FlowLayout();
      BorderLayout flowLayout3 = new BorderLayout();
      jPanel4 = new JPanel();
      jPanel4.setLayout(flowLayout3);
      jLabel.setText("Name: ");
      jLabel.setDisplayedMnemonic(KeyEvent.VK_N);
      jLabel.setLabelFor(getJTextField());
      jLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      jLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
      // flowLayout3.setAlignment(java.awt.FlowLayout.LEFT);
      jPanel4.add(jLabel, BorderLayout.WEST);
      jPanel4.add(getJTextField(), BorderLayout.CENTER);
    }
    return jPanel4;
  }

  /**
   * This method initializes jTextField
   * @return javax.swing.JTextField
   */
  private JTextField getJTextField() {
    if (jTextField == null) {
      jTextField = new JTextField();
      jTextField.setName("Name");
    }
    return jTextField;
  }

  //
  // Constructor
  //

  /**
   * This is the default constructor.
   * @param list Testsuite list
   * @param chipType Chip type
   */
  public NewTestSuiteWidget(final QualityTestSuiteList list,
      final String chipType) {
    super();
    setList(list);
    setChipType(chipType);
    initialize();
    getChipTypeRadioButton().setSelected(false);
    jTextField.requestFocusInWindow();
  }

}