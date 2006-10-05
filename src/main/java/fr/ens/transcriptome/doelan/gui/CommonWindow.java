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

import java.applet.Applet;
import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.doelan.Core;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.nividic.util.NividicUtils;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * Common GUI for applet and normal application.
 * @author Laurent jourdren TODO modify the about information to use Globals
 *               TODO update directory and file management
 */
public class CommonWindow {

  private static final String WINDOWS_PLAF = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
  //private static final String METAL_PLAF =
  // "javax.swing.plaf.metal.MetalLookAndFeel";
  private static final String GTK_PLAF = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

  //For log system
  private static Logger log = Logger.getLogger(CommonWindow.class);

  private static CommonWindow cw;

  private Container container;
  private Core core = Core.getCore();

  private JLabel statusBar;

  private MainTabWidget mainTab;
  private TestSuiteTabWidget testSuiteTab;
  private ReportTabWidget reportTab;

  private javax.swing.JTabbedPane tabbedPane;

  //
  // Getters
  //

  /**
   * Get the container
   * @return The container
   */
  private Container getContainer() {
    return container;
  }

  /**
   * Set the unique command window object.
   * @return the unique command window
   */
  private static CommonWindow getCommonWindow() {
    return cw;
  }

  //
  // Setters
  //

  /**
   * Set the Graphical Container.
   * @param container The container to set
   */
  public void setContainer(final Container container) {
    this.container = container;
  }

  /**
   * Set the unique command window object.
   */
  private void setCommonWindow(final CommonWindow cw) {
    CommonWindow.cw = cw;
  }

  //
  // Other methods
  //

  /**
   * Find the best look and feel for the application.
   */
  private void setUILookAndFeel() {

    if (SystemUtils.isWindowsSystem())
      setLookAndFeel(WINDOWS_PLAF);
    else if (SystemUtils.isMacOsX())
      NividicUtils.nop();
    else if (System.getProperty("os.name").startsWith("Linux")
        && System.getProperty("java.version").startsWith("1.5."))
      setLookAndFeel(GTK_PLAF);
    /*
     * else {
     * //setLookAndFeel("com.jgoodies.plaf.plastic.Plastic3DLookAndFeel"); //
     * jgoodies /* Plastic3DLookAndFeel laf = new Plastic3DLookAndFeel();
     * Plastic3DLookAndFeel
     * .setTabStyle(Plastic3DLookAndFeel.TAB_STYLE_METAL_VALUE);
     * Plastic3DLookAndFeel.setHighContrastFocusColorsEnabled(true);
     * Plastic3DLookAndFeel .setMyCurrentTheme(new
     * com.jgoodies.plaf.plastic.theme.Silver()); setLookAndFeel(laf); }
     */

    SwingUtilities.updateComponentTreeUI(getContainer());
  }

  /**
   * Set a look and feel for the application.
   * @param uiClassName Class name of the look and feel
   */
  private static void setLookAndFeel(final String uiClassName) {

    if (uiClassName == null)
      return;

    if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
      try {
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
          public void run() {
            setLookAndFeel(uiClassName);
          }
        });
      } catch (InterruptedException e) {
        log.error("Error when trying to use the Event dispatch thread.");
      } catch (InvocationTargetException e) {
        log.error("Error when trying to use the Event dispatch thread.");
      }

    } else {

      try {

        //UIManager.setLookAndFeel(uiClassName);

        Class c = CommonWindow.class.getClassLoader().loadClass(uiClassName);
        LookAndFeel laf = (LookAndFeel) c.newInstance();

        UIManager.setLookAndFeel(laf);

      } catch (ClassNotFoundException e) {
        log.error("PLAF error, class not found: " + uiClassName);
        return;
      } catch (InstantiationException e) {
        log.error("PLAF error, instantiation exception: " + uiClassName);
        return;
      } catch (IllegalAccessException e) {
        log.error("PLAF error, illegal access: " + uiClassName);
        return;
      } catch (UnsupportedLookAndFeelException e) {
        log.error("PLAF error, unssopported look and feel: " + uiClassName);
        return;
      }

    }

  }

  /**
   * Set a look and feel for the application.
   * @param lookAndFeel The look and feel
   */
  /*
   * private void setLookAndFeel(final LookAndFeel lookAndFeel) { if
   * (lookAndFeel == null) return; try { UIManager.setLookAndFeel(lookAndFeel); }
   * catch (UnsupportedLookAndFeelException e) { final String defaultPLAF =
   * UIManager.getSystemLookAndFeelClassName(); if
   * (lookAndFeel.equals(defaultPLAF)) return; log.error("Invalid look and feel: " +
   * lookAndFeel); setLookAndFeel(defaultPLAF); }
   * SwingUtilities.updateComponentTreeUI(getContainer()); }
   */

  /**
   * Show a message to the user.
   * @param message Message to show
   * @param error true if message is an error
   */
  public static void showMessage(final String message, final boolean error) {

    if (DoelanRegistery.isAppletMode()) {
      if (error)
        getCommonWindow().statusBar.setForeground(Color.RED);
      else
        getCommonWindow().statusBar.setForeground(Color.BLACK);
      getCommonWindow().statusBar.setText(message);
      Toolkit.getDefaultToolkit().beep();
    } else
      JOptionPane.showMessageDialog(getCommonWindow().getContainer(), message,
          error ? "Error" : "Message", error ? JOptionPane.ERROR_MESSAGE
              : JOptionPane.WARNING_MESSAGE);
  }

  /**
   * Set the message for the status bar.
   * @param message Message to set
   */
  public static void showStatusBarMessage(final String message) {
    getCommonWindow().statusBar.setText(message);
  }

  /**
   * Initiatilze the application.
   */
  public void init() {

    setUILookAndFeel();

    statusBar = new JLabel();
    tabbedPane = new javax.swing.JTabbedPane();

    showStatusBarMessage("No test suite loaded.");
    getContainer().add(statusBar, java.awt.BorderLayout.SOUTH);

    mainTab = new MainTabWidget();
    testSuiteTab = new TestSuiteTabWidget();
    reportTab = new ReportTabWidget(statusBar);

    tabbedPane.addTab("Main", mainTab);
    tabbedPane.addTab("TestSuite", testSuiteTab);
    tabbedPane.addTab("Report", reportTab);

    getContainer().add(tabbedPane, java.awt.BorderLayout.CENTER);
    this.core.setMainTab(mainTab);
    this.core.setTable(testSuiteTab.getTable());
    this.core.setReport(reportTab);
    this.core.setStatusBar(statusBar);

    mainTab.setWorking(false);

    //setUILookAndFeel();
  }

  //
  // Constructors
  //

  private CommonWindow() {

    setCommonWindow(this);
    container = null;
  }

  /**
   * Public constructor for applet mode.
   * @param applet The main applet
   * @param testSuiteListURL URL of the testsuite list
   */
  public CommonWindow(final Applet applet, final URL testSuiteListURL) {
    this(((JApplet) applet).getContentPane());

    Core.getCore().setApplet(applet);
    Core.getCore().setTestSuiteListURL(testSuiteListURL);
  }

  /**
   * Public constructor for standalone mode.
   * @param frame The main frame
   * @param testSuiteListURL URL of the testsuite list
   */
  public CommonWindow(final JFrame frame, final URL testSuiteListURL) {
    this(frame.getContentPane());

    DoelanRegistery.setAppletMode(false);
    Core.getCore().setTestSuiteListURL(testSuiteListURL);
  }

  private CommonWindow(final Container container) {
    this();

    this.container = container;

    if (DoelanRegistery.isAppletMode()) {
      log.info("Mode: Applet");
      ToolTipManager.sharedInstance().setEnabled(false);
    } else
      log.info("Mode: Standalone application");

  }

}