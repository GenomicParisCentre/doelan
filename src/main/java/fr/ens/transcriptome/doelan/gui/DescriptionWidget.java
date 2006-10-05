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

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This class implements a file chooser.
 * @author Laurent Jourdren
 */
public class DescriptionWidget {

  private String label;
  private JPanel jPanel;
  private int position;
  private int mnemonicKey;

  private JTextField descriptionField = new JTextField();

  //
  // Getters
  //

  /**
   * Get the text of the label.
   * @return Returns the labelText
   */
  private String getLabel() {
    return label;
  }

  /**
   * Get the panel.
   * @return Returns the Panel
   */
  private JPanel getPanel() {
    return jPanel;
  }

  /**
   * Get the position in the panel.
   * @return Returns the position
   */
  private int getPosition() {
    return position;
  }

  /**
   * Get the mnemonic key.
   * @return Returns the mnemonicKey
   */
  public int getMnemonicKey() {
    return mnemonicKey;
  }

  /**
   * Get the text of the description.
   * @return the text of the description
   */
  public String getDescription() {
    return this.descriptionField.getText();
  }

  //
  // Setters
  //

  /**
   * Set the label of the text.
   * @param labelText The labelText to set
   */
  private void setLabel(final String labelText) {
    this.label = labelText;
  }

  /**
   * Set the panel.
   * @param panel The Panel to set
   */
  private void setPanel(final JPanel panel) {
    jPanel = panel;
  }

  /**
   * Set the postion in the panel.
   * @param position The position to set
   */
  private void setPosition(final int position) {
    this.position = position;
  }

  /**
   * set the mnemonic key.
   * @param mnemonicKey The mnemonicKey to set
   */
  public void setMnemonicKey(final int mnemonicKey) {
    this.mnemonicKey = mnemonicKey;
  }

  //
  // Other methods
  //

  private void init() {

    GridBagConstraints gridBagConstraints;
    // JPanel selectorPanel = new JPanel();
    // selectorPanel.setLayout(new java.awt.GridBagLayout());
    final JPanel selectorPanel = getPanel();

    // File label
    JLabel jLabel1 = new JLabel(getLabel());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = getPosition();
    gridBagConstraints.ipadx = 30;
    selectorPanel.add(jLabel1, gridBagConstraints);
    jLabel1.setDisplayedMnemonic(getMnemonicKey());
    jLabel1.setLabelFor(descriptionField);

    // Name of file selected
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.gridy = getPosition();
    selectorPanel.add(descriptionField, gridBagConstraints);

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param label the Label of the widget
   * @param panel Calling panel
   * @param position Position of the chooser
   */
  public DescriptionWidget(final String label, final int mnemonicKey,
      final JPanel panel, final int position) {

    setLabel(label);
    setMnemonicKey(mnemonicKey);
    setPanel(panel);
    setPosition(position);
    init();
  }

}