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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import fr.ens.transcriptome.doelan.DoelanException;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteList;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteURL;
import fr.ens.transcriptome.doelan.io.QualityTestIOFactory;
import fr.ens.transcriptome.doelan.io.QualityTestSuiteListIO;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * Suite chooser widget.
 * @author Laurent Jourdren
 */
public class SuiteChooserWidget {

  private JComboBox chipTypeList = new JComboBox();
  private JComboBox testSuiteList = new JComboBox();
  private MainTabWidget mainTabWidget;
  private JPanel panel;
  private int yPosition;

  private String currentChipTypeName;
  private String currentTestSuiteName;
  private boolean advancedButtons;

  private JButton advancedButton;
  private JButton loadButton;
  private JButton newButton;
  private JButton renameButton;
  private JButton deleteButton;
  //private JButton saveButton;

  private QualityTestSuiteList data;

  //
  // Getters
  //

  /**
   * Get the test suite list.
   * @return The test suite list
   */
  public QualityTestSuiteList getTestSuiteList() {
    return data;
  }

  public String getCurrentChipTypeName() {
    return currentChipTypeName;
  }

  public String getCurrentTestSuiteName() {
    return currentTestSuiteName;
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

  /**
   * Get the mainTabWidget.
   * @return Returns the mainTabWidget
   */
  public MainTabWidget getMainTabWidget() {
    return mainTabWidget;
  }

  //
  // Setters
  //

  /**
   * Set the test suite list.
   * @param list The suite list
   */
  public void setTestSuiteList(final QualityTestSuiteList list) {
    data = list;
    updateChipList();
  }

  private void setCurrentChipTypeName(final String name) {
    currentChipTypeName = name;
  }

  private void setCurrentTestSuiteName(final String name) {
    currentTestSuiteName = name;
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
   * Set the common window.
   * @param mainTabWidget The mainTabWidget to set
   */
  public void setMainTabWidget(final MainTabWidget commonWindow) {
    this.mainTabWidget = commonWindow;
  }

  //
  // Other methods
  //

  /**
   * Return the quality test suite url selected.
   * @return the quality test suite url object selected
   */
  public QualityTestSuiteURL getQualityTestSuiteSelected() {

    if (this.data == null)
      return null;

    QualityTestSuiteURL[] suites = this.data
        .getTestSuiteURLs(getCurrentChipTypeName());

    if (suites == null)
      return null;
    final String testSuiteName = getCurrentTestSuiteName();
    for (int i = 0; i < suites.length; i++) {
      if (suites[i].getName().equals(testSuiteName))
        return suites[i];
    }

    return null;
  }

  private void showAdvancedButtons(final boolean show) {

    this.newButton.setVisible(show);
    this.deleteButton.setVisible(show);
    this.renameButton.setVisible(show);
    //this.saveButton.setVisible(show);
    this.loadButton.setVisible(show);

    if (show == false) {
      this.advancedButton.setText("Show advanced options");
      this.advancedButton
          .setToolTipText("Press this button to show advanced options");
    } else {
      this.advancedButton.setText("Hide advanced options");
      this.advancedButton
          .setToolTipText("Press this button to hide advanced options");
    }

    this.advancedButtons = show;

  }

  private void save() {

    String[] chipTypes = getTestSuiteList().getChipTypes();
    if (chipTypes == null) {
      JOptionPane.showMessageDialog(panel, "Nothing to save");
      return;
    }

    for (int i = 0; i < chipTypes.length; i++) {
      QualityTestSuiteURL[] urls = getTestSuiteList().getTestSuiteURLs(
          chipTypes[i]);
      for (int j = 0; j < urls.length; j++)
        if (urls[j].getURL() == null) {
          JOptionPane.showMessageDialog(panel, "Test suite not save");
          return;
        }
    }

    QualityTestSuiteListIO qtslio = new QualityTestIOFactory()
        .createQualityTestSuiteListIO(DoelanRegistery
            .getTestSuiteListFilename());

    try {
      qtslio.write(data);
      //JOptionPane.showMessageDialog(panel, "Test suite directory saved.");
    } catch (DoelanException err) {
      JOptionPane.showMessageDialog(panel, err.getMessage());
    }

  }

  private void init() {

    chipTypeList.setToolTipText("Click to select a chip type");
    testSuiteList.setToolTipText("Click to select a test suite");

    final JPanel selectorPanel = getPanel();

    GridBagConstraints gridBagConstraints;

    JLabel jLabel3 = new JLabel("Select the related microarray type:");
    jLabel3.setDisplayedMnemonic(KeyEvent.VK_M);
    jLabel3.setLabelFor(chipTypeList);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = getYPosition();

    selectorPanel.add(jLabel3, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = getYPosition();
    gridBagConstraints.gridwidth = 4;
    selectorPanel.add(chipTypeList, gridBagConstraints);

    JLabel jLabel4 = new JLabel("Load the test suite you want to apply:");
    jLabel4.setDisplayedMnemonic(KeyEvent.VK_T);
    jLabel4.setDisplayedMnemonicIndex(9);
    jLabel4.setLabelFor(testSuiteList);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = getYPosition() + 1;
    selectorPanel.add(jLabel4, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = getYPosition() + 1;
    gridBagConstraints.gridwidth = 4;
    selectorPanel.add(testSuiteList, gridBagConstraints);

    // Test suite manager

    if (!DoelanRegistery.isAppletMode()) {

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = getYPosition() + 2;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
      loadButton = new JButton("Load test");
      loadButton.setToolTipText("Press this button to load the test suite");

      selectorPanel.add(loadButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = getYPosition() + 2;
      gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 3);
      newButton = new JButton("New");
      newButton
          .setToolTipText("Press this button to create a new test suite or a new chip type");

      selectorPanel.add(newButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = getYPosition() + 2;
      gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 3);
      renameButton = new JButton("Rename");
      renameButton
          .setToolTipText("Press this button to rename a test suite or a chip type");

      selectorPanel.add(renameButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = getYPosition() + 2;
      gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
      deleteButton = new JButton("Delete");
      deleteButton
          .setToolTipText("Press this button to delete a test suite or a chip type");

      selectorPanel.add(deleteButton, gridBagConstraints);

      /*
       * gridBagConstraints = new java.awt.GridBagConstraints();
       * gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
       * gridBagConstraints.gridx = 5; gridBagConstraints.gridy = getYPosition() +
       * 3; gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
       * saveButton = new JButton("Save"); selectorPanel.add(saveButton,
       * gridBagConstraints);
       */

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridx = 4;
      gridBagConstraints.gridy = getYPosition() + 3;
      gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
      this.advancedButton = new JButton();

      selectorPanel.add(advancedButton, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = getYPosition() + 3;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.ipadx = 300;
      gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
      final JLabel label = new JLabel();
      selectorPanel.add(label, gridBagConstraints);

      showAdvancedButtons(false);

      if (!SystemUtils.isMacOsX()) {
        loadButton.setMnemonic(KeyEvent.VK_L);
        newButton.setMnemonic(KeyEvent.VK_N);
        renameButton.setMnemonic(KeyEvent.VK_R);
        deleteButton.setMnemonic(KeyEvent.VK_D);
        advancedButton.setMnemonic(KeyEvent.VK_A);
      }

      advancedButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {

          if (SuiteChooserWidget.this.advancedButtons)
            SuiteChooserWidget.this.advancedButtons = false;
          else
            SuiteChooserWidget.this.advancedButtons = true;

          SuiteChooserWidget.this
              .showAdvancedButtons(SuiteChooserWidget.this.advancedButtons);

        }
      });

      loadButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          if (getCurrentTestSuiteName() == null)
            JOptionPane.showMessageDialog(panel, "No test suite selected");
          else
            getMainTabWidget().load();
        }
      });

      newButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {

          NewTestSuiteWidget ntsw = new NewTestSuiteWidget(data,
              getCurrentChipTypeName());

          ntsw.setLocation(newButton.getLocationOnScreen());
          ntsw.show();

          updateChipList();
          updateSuiteList(getCurrentChipTypeName());
          save();
        }
      });

      renameButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          RenameTestSuiteWidget rtsw = new RenameTestSuiteWidget(data,
              getCurrentChipTypeName(), getQualityTestSuiteSelected());

          rtsw.setLocation(renameButton.getLocationOnScreen());
          //rtsw.show();

          updateChipList();
          updateSuiteList(getCurrentChipTypeName());
          save();
        }
      });

      deleteButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {

          if (getCurrentTestSuiteName() == null) {
            int response = JOptionPane.showConfirmDialog(panel,
                new String[] {"Remove \"" + getCurrentChipTypeName()
                    + "\" chip type ?"}, "Remove chip type",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            switch (response) {
            case JOptionPane.YES_OPTION:
              /*
               * if (!data.removeChipType(getCurrentChipTypeName()))
               * JOptionPane.showMessageDialog(panel, "Unable to remove this
               * chip type");
               */

              try {
                data.removeChipType(getCurrentChipTypeName());
              } catch (DoelanException e1) {
                JOptionPane.showMessageDialog(panel, e1.getMessage());
              }

              break;

            default:
              break;
            }
          } else {
            int response = JOptionPane.showConfirmDialog(panel,
                new String[] {"Remove \"" + getCurrentTestSuiteName()
                    + "\" test suite ?"}, "Remove test suite",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            switch (response) {
            case JOptionPane.YES_OPTION:

              try {
                /*
                 * if (!data.removeTestSuite(getQualityTestSuiteSelected()))
                 * JOptionPane.showMessageDialog(panel, "Unable to remove this
                 * test suite");
                 */
                data.removeTestSuite(getQualityTestSuiteSelected());
              } catch (DoelanException e1) {
                JOptionPane.showMessageDialog(panel, e1.getMessage());
              }

              break;

            default:
              break;
            }
          }
          updateChipList();
          updateSuiteList(getCurrentChipTypeName());
          save();
        }
      });

      /*
       * saveButton.addActionListener(new ActionListener() { public void
       * actionPerformed(final ActionEvent e) { String[] chipTypes =
       * getTestSuiteList().getChipTypes(); if (chipTypes == null) {
       * JOptionPane.showMessageDialog(panel, "Nothing to save"); return; } for
       * (int i = 0; i < chipTypes.length; i++) { QualityTestSuiteURL[] urls =
       * getTestSuiteList().getTestSuiteURLs( chipTypes[i]); for (int j = 0; j <
       * urls.length; j++) if (urls[j].getURL() == null) {
       * JOptionPane.showMessageDialog(panel, "Test suite not save"); return; } }
       * QualityTestSuiteListIO qtslio = new QualityTestIOFactory()
       * .createQualityTestSuiteListIO(DoelanRegistery
       * .getTestSuiteListFilename()); try { qtslio.write(data);
       * JOptionPane.showMessageDialog(panel, "Test suite directory saved."); }
       * catch (DoelanException err) { JOptionPane.showMessageDialog(panel,
       * err.getMessage()); } } });
       */
    }

    // Update Suite list when a chip type is selected
    this.chipTypeList.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JComboBox combo = (JComboBox) e.getSource();

        setCurrentChipTypeName((String) combo.getSelectedItem());
        updateSuiteList(getCurrentChipTypeName());
      }
    });

    //  Update Suite list when a chip type is selected
    this.testSuiteList.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JComboBox combo = (JComboBox) e.getSource();
        setCurrentTestSuiteName((String) combo.getSelectedItem());
      }
    });

  }

  //
  // Constructor
  //

  private void updateChipList() {

    this.chipTypeList.removeAllItems();
    String[] elements = data.getChipTypes();

    Arrays.sort(elements);
    for (int i = 0; i < elements.length; i++) {
      this.chipTypeList.addItem(elements[i]);
    }
    if (elements.length != 0) {
      updateSuiteList(elements[0]);
      this.chipTypeList.setEnabled(true);
      if (!DoelanRegistery.isAppletMode()) {
        this.deleteButton.setEnabled(true);
        this.renameButton.setEnabled(true);
      }

    } else {
      updateSuiteList(null);
      this.chipTypeList.setEnabled(false);
      if (!DoelanRegistery.isAppletMode()) {
        this.deleteButton.setEnabled(false);
        this.renameButton.setEnabled(false);
      }
      if (!DoelanRegistery.isAppletMode()) showAdvancedButtons(true);

    }

  }

  private void updateSuiteList(final String chip) {

    if (chip == null) {
      this.mainTabWidget.showStartButton(false);
      if (!DoelanRegistery.isAppletMode()) {
        this.testSuiteList.setEnabled(false);
        this.loadButton.setEnabled(false);
      }
      return;
    }

    this.testSuiteList.removeAllItems();

    QualityTestSuiteURL[] suites = data.getTestSuiteURLs(chip);

    if (suites.length == 0) {
      //setCurrentChipTypeName(null);
      setCurrentTestSuiteName(null);
      this.testSuiteList.setEnabled(false);
      if (!DoelanRegistery.isAppletMode())
        this.loadButton.setEnabled(false);
      this.mainTabWidget.showStartButton(false);
      return;
    }
    this.testSuiteList.setEnabled(true);
    if (!DoelanRegistery.isAppletMode())
      this.loadButton.setEnabled(true);
    this.mainTabWidget.showStartButton(true);

    String[] names = new String[suites.length];

    for (int i = 0; i < suites.length; i++)
      names[i] = suites[i].getName();
    Arrays.sort(names);

    for (int i = 0; i < names.length; i++)
      testSuiteList.addItem(names[i]);

    if (names.length == 0)
      setCurrentTestSuiteName(null);
    else
      setCurrentTestSuiteName(names[0]);
  }

  private static void setMiddleLocation(Container object) {

    Container c = object;

    while (c.getParent() != null)
      c = c.getParent();

    int posY = (c.getHeight() / 2) - (object.getHeight() / 2);
    int posX = (c.getWidth() / 2) - (object.getWidth() / 2);

    Point rp = c.getLocationOnScreen();
    Point p = c.getLocation();

    object.setLocation((int) (rp.getX() - p.getX() + posX), (int) (rp.getY()
        - p.getY() + posY));

  }

  /**
   * Public constructor.
   * @param panel Panel of the widget
   * @param position of the widget in the panel
   */
  public SuiteChooserWidget(final MainTabWidget cw, final JPanel panel,
      final int position) {

    setPanel(panel);
    setYPosition(position);
    setMainTabWidget(cw);
    init();
  }

}