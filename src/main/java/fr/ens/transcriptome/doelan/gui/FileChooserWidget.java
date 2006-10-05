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
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import fr.ens.transcriptome.doelan.Core;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * This class implements a file chooser.
 * @author Laurent Jourdren
 */
public class FileChooserWidget {

  private String label;
  // private String extension;
  // private String extensionDescription;
  private JPanel jPanel;
  private int position;
  private int mnemonicKey;
  private String buttonText;

  private File file;
  private File[] files;
  private boolean fileChooser;
  private JLabel filenameLabel = new JLabel();
  private boolean firstInit = true;

  private ArrayList types = new ArrayList();

  private class FileType {

    public String description;
    public String extension;
    public boolean multipleFiles;

  }

  //
  // Getters
  //

  /**
   * Get if the file choose must be displayed.
   * @return true if the file choose must be displayed
   */
  private boolean isFileChooser() {

    return fileChooser;
  }

  /**
   * Get the data file.
   * @return The data file
   */
  public File getFile() {
    return file;
  }

  /**
   * Get the data files.
   * @return The data files
   */
  public File[] getFiles() {
    return files;
  }

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
   * Get the text of the button
   * @return Returns the buttonText
   */
  public String getButtonText() {
    return buttonText;
  }

  //
  // Setters
  //

  /**
   * Set the data file.
   * @param file The data file
   */
  private void setFile(final File file) {
    this.file = file;
  }

  /**
   * Set the data files.
   * @param files The data files
   */
  private void setFiles(final File[] files) {
    this.files = files;
  }

  /**
   * Set if the file chooser must be visible.
   * @param visible
   */
  private void setFileChooser(final boolean visible) {
    fileChooser = visible;
  }

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

  /**
   * Set the text of the button
   * @param buttonText The buttonText to set
   */
  public void setButtonText(final String buttonText) {
    this.buttonText = buttonText;
  }

  /**
   * Add a file type to the Widget.
   * @param description Description of the filetype
   * @param extension Extension of the filetype
   * @param multipleFiles If there are multiple files
   */
  public void addFiletype(final String description, final String extension,
      final boolean multipleFiles) {

    if (extension == null)
      return;
    FileType ft = new FileType();
    ft.description = description;
    ft.extension = extension;
    ft.multipleFiles = multipleFiles;

    this.types.add(ft);
  }

  //
  // Other methods
  //

  public void init() {

    if (this.firstInit) {
      this.firstInit = false;
    } else
      return;

    GridBagConstraints gridBagConstraints;
    // JPanel selectorPanel = new JPanel();
    // selectorPanel.setLayout(new java.awt.GridBagLayout());
    final JPanel selectorPanel = getPanel();

    JButton chooseFileButton = new JButton();
    chooseFileButton.setToolTipText("Press this button to select a file");

    // File label
    JLabel jLabel1 = new JLabel(getLabel());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = getPosition();
    gridBagConstraints.ipadx = 30;
    selectorPanel.add(jLabel1, gridBagConstraints);

    // Name of file selected
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.gridy = getPosition();
    selectorPanel.add(filenameLabel, gridBagConstraints);

    if (isFileChooser()) {

      filenameLabel.setText("no file seleted");
      filenameLabel.setEnabled(false);
      chooseFileButton.setText(getButtonText());

      if (!SystemUtils.isMacOsX())
        chooseFileButton.setMnemonic(getMnemonicKey());

      // Choose file button
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = getPosition();
      gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 0);
      selectorPanel.add(chooseFileButton, gridBagConstraints);

    } else {
      filenameLabel.setText("Genepix Pro data");
    }

    // File chooser
    chooseFileButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc
            .setCurrentDirectory(new File(Core.getCore()
                .getLastFileChooserDir()));
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        for (int i = 0; i < types.size(); i++) {

          final FileType ft = (FileType) types.get(i);
          if (ft.multipleFiles == true && !jfc.isMultiSelectionEnabled())
            jfc.setMultiSelectionEnabled(true);

          jfc.addChoosableFileFilter(new FileFilter() {
            public boolean accept(final File f) {

              if (f.isDirectory())
                return true;
              if (f.getName().length() < 4)
                return false;
              String end = f.getName().substring(f.getName().length() - 4);

              return end.toLowerCase().endsWith(ft.extension);
            }

            public String getDescription() {
              return ft.description;
            }
          });
        }

        int result = jfc.showOpenDialog(jPanel);
        if (result == JFileChooser.APPROVE_OPTION) {
          setFile(jfc.getSelectedFile());
          setFiles(jfc.getSelectedFiles());
          filenameLabel.setText(getFile().getName());
          filenameLabel.setEnabled(true);
        }

      }
    });

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param fileChooserVisible Set if the fileChooser must be visible
   * @param label the Label of the widget
   * @param mnemonicKey mnemonic key
   * @param buttonText button text
   * @param panel Calling panel
   * @param position Position of the chooser
   */
  public FileChooserWidget(final boolean fileChooserVisible,
      final String label, final int mnemonicKey, final String buttonText,
      final JPanel panel, final int position) {

    setLabel(label);
    setMnemonicKey(mnemonicKey);
    setButtonText(buttonText);
    setFileChooser(fileChooserVisible);
    setPanel(panel);
    setPosition(position);
  }

}