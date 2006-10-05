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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleLocation;
import fr.ens.transcriptome.nividic.platform.module.ModuleManager;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * Widget for creating new WorkflowElement.
 * @author Laurent Jourdren
 */
public class NewWorkflowElementWidget extends JDialog {

  private javax.swing.JPanel jContentPane;

  private JLabel algoLabel;
  private JComboBox algoComboBox;
  private Map mapIdModuleLocation = new HashMap();
  private ModuleLocation[] moduleLocations;
  private boolean ok;

  private JPanel jPanel;
  private JPanel jPanel1;
  private JButton cancelButton;
  private JButton okButton;
  private JTextField idTextField;

  private JLabel idLabel;

  /**
   * This is the default constructor
   * @param moduleLocations Module location of the algorithms
   */
  public NewWorkflowElementWidget(final ModuleLocation[] moduleLocations) {
    super();
    this.moduleLocations = moduleLocations;
    initialize();
  }

  /**
   * This method initializes this
   */
  private void initialize() {
    //this.setSize(300, 150);
    this.setContentPane(getJContentPane());
    this.setSize(this.algoLabel.getPreferredSize().width
        + this.algoComboBox.getPreferredSize().width, 150);

    this.setResizable(false);

  }

  /**
   * This method initializes jContentPane
   * @return javax.swing.JPanel
   */
  private javax.swing.JPanel getJContentPane() {
    if (jContentPane == null) {
      BorderLayout gridLayout22 = new BorderLayout();
      algoLabel = new JLabel();
      jContentPane = new javax.swing.JPanel();
      jContentPane.setLayout(gridLayout22);
      algoLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
      algoLabel.setText("Algorithm");
      algoLabel.setDisplayedMnemonic(KeyEvent.VK_A);
      algoLabel.setLabelFor(getAlgoComboBox());
      jContentPane.add(getJPanel(), BorderLayout.CENTER);
      jContentPane.add(getJPanel1(), BorderLayout.SOUTH);

    }
    return jContentPane;
  }

  /**
   * This method initializes jComboBox
   * @return javax.swing.JComboBox
   */
  private JComboBox getAlgoComboBox() {
    if (algoComboBox == null) {
      algoComboBox = new JComboBox();

      if (moduleLocations != null)
        for (int i = 0; i < moduleLocations.length; i++) {
          final String key = moduleLocationNameShow(moduleLocations[i]);
          getAlgoComboBox().addItem(key);
          this.mapIdModuleLocation.put(key, moduleLocations[i]);
        }

    }
    return algoComboBox;
  }

  private static String moduleLocationNameShow(
      final ModuleLocation moduleLocation) {
    if (moduleLocation == null)
      return null;

    Module m = ModuleManager.getManager().loadModule(moduleLocation);

    return m.aboutModule().getShortDescription();
  }

  /**
   * Get the module location of the selected algorithm.
   * @return The module location of the selected algorithm
   */
  public ModuleLocation getModuleLocationSelected() {
    return (ModuleLocation) this.mapIdModuleLocation.get(getAlgoComboBox()
        .getSelectedItem());
  }

  /**
   * Get the identifier of the new algorithm.
   * @return The identifier of the algorithm
   */
  public String getIdentifier() {
    return getIdTextField().getText();
  }

  /**
   * This method initializes jPanel
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel() {
    if (jPanel == null) {
      idLabel = new JLabel();
      GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
      GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
      jPanel = new JPanel();
      jPanel.setLayout(new GridBagLayout());
      gridBagConstraints16.gridx = 1;
      gridBagConstraints16.gridy = 2;
      gridBagConstraints16.insets = new java.awt.Insets(5, 0, 5, 0);
      gridBagConstraints17.gridx = 2;
      gridBagConstraints17.gridy = 2;
      gridBagConstraints17.weightx = 1.0;
      gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints17.ipadx = 186;
      gridBagConstraints17.insets = new java.awt.Insets(1, 0, 0, 0);
      gridBagConstraints21.gridx = 2;
      gridBagConstraints21.gridy = 1;
      gridBagConstraints21.weightx = 1.0;
      gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints23.gridx = 1;
      gridBagConstraints23.gridy = 1;
      idLabel.setText("Identifer");
      idLabel.setDisplayedMnemonic(KeyEvent.VK_I);
      idLabel.setLabelFor(getIdTextField());
      idLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
      jPanel.add(algoLabel, gridBagConstraints16);
      jPanel.add(getAlgoComboBox(), gridBagConstraints17);
      jPanel.add(getIdTextField(), gridBagConstraints21);
      jPanel.add(idLabel, gridBagConstraints23);
    }
    return jPanel;
  }

  /**
   * This method initializes jPanel1
   * @return javax.swing.JPanel
   */
  private JPanel getJPanel1() {
    if (jPanel1 == null) {
      jPanel1 = new JPanel();
      jPanel1.add(getOkButton(), null);
      jPanel1.add(getCancelButton(), null);
    }
    return jPanel1;
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
   * This method initializes jButton2
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
          ok = true;
          hide();
        }
      });
    }
    return okButton;
  }

  /**
   * This method initializes jTextField
   * @return javax.swing.JTextField
   */
  private JTextField getIdTextField() {
    if (idTextField == null) {
      idTextField = new JTextField();
    }
    return idTextField;
  }

  /**
   * Hide the widget.
   */
  public void close() {
    this.hide();
  }

  /**
   * Test if ok.
   * @return Returns the ok
   */
  public boolean isOk() {
    return ok;
  }
}