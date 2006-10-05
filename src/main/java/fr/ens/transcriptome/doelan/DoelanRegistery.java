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

package fr.ens.transcriptome.doelan;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.ens.transcriptome.nividic.platform.Registery;
import fr.ens.transcriptome.nividic.platform.PlatformRegistery;
import fr.ens.transcriptome.nividic.util.WebBrowser;

/**
 * Globals setting for the application
 * @author Laurent Jourdren
 */
public final class DoelanRegistery {

  /**
   * Initialize default values.
   */
  public static void init() {

    URL url = DoelanRegistery.class.getResource(Defaults.CONFIG_FILE);
    Registery.loadRegistery(url);
    Registery.getRegistery().setProperty("debug", "" + Defaults.DEBUG);
    Registery.getRegistery().setProperty("project.name", Defaults.APP_NAME);
    Registery.getRegistery().setProperty("project.version", Defaults.VERSION);
  }

  /**
   * Get the absolute path of the test suite list file.
   * @return The path of this file
   */
  public static String getTestSuiteListFilename() {
    return Registery.getRegistery().getProperty(
        "doelan.suiteslist.file",
        PlatformRegistery.getConfDirectory() + File.separator
            + Defaults.TEST_SUITE_LIST_FILE);
  }

  /**
   * Get the absolute path of the directory for plugins.
   * @return The path of the directory for plugins
   */
  public static String getDoelanDataDirectory() {

    return Registery.getRegistery().getProperty(
        "doelan.data.dir",
        PlatformRegistery.getBaseDirectory() + File.separator
            + Defaults.SUBDIR_DATA);
  }

  /**
   * Get the absolute path of the directory for plugins.
   * @return The path of the directory for plugins
   */
  public static String getDoelanReportDirectory() {

    return Registery.getRegistery().getProperty(
        "doelan.report.dir",
        PlatformRegistery.getBaseDirectory() + File.separator
            + Defaults.SUBDIR_REPORT);
  }

  /**
   * Get the name of the application.
   * @return The name of the application
   */
  public static String getAppName() {
    return Registery.getRegistery().getProperty("project.name",
        Defaults.APP_NAME);
  }

  /**
   * Get the version of the application.
   * @return the name of the application
   */
  public static String getAppVersion() {
    return Registery.getRegistery().getProperty("project.version",
        Defaults.VERSION);
  }

  /**
   * Get the description of the application.
   * @return the description of the application
   */
  public static String getAppDescription() {
    return Registery.getRegistery().getProperty("project.description",
        Defaults.APP_DESCRITPION);
  }

  /**
   * Get the date of the compilation.
   * @return The date of the compilation
   */
  public static Date getAppCompileDate() {

    String d = Registery.getRegistery().getProperty("project.compile.date");

    if (d == null)
      return null;

    DateFormat df = new SimpleDateFormat("dd-MM-yy-H:mm:ss");
    try {
      return df.parse(d);
    } catch (ParseException e) {
      return null;
    }

  }

  /**
   * Get the URL of the project.
   * @return Get the URL of the project
   */
  public static String getAppURL() {
    return Registery.getRegistery()
        .getProperty("project.url", Defaults.WEBSITE);
  }

  /**
   * Get the name of the organization of the project.
   * @return The name of the organization of the project
   */
  public static String getOrganizationName() {
    return Registery.getRegistery().getProperty("project.organization.name",
        Defaults.MANUFACTURER);
  }

  /**
   * Get the url of the organization
   * @return The url of the organization of the project
   */
  public static String getOrganizationURL() {
    return Registery.getRegistery().getProperty("project.organization.url",
        Defaults.MANUFACTURER_WEBSITE);
  }

  /**
   * Get the date of the copyright.
   * @return The date of the copyright
   */
  public static String getCopyrightDate() {
    return Registery.getRegistery().getProperty("project.copyright.date",
        Defaults.COPYRIGHT_DATE);
  }

  /**
   * Get the copyright.
   * @return The copyright of the appplication.
   */
  public static String getAppCopyright() {
    return "Copyright " + getCopyrightDate() + " " + getOrganizationName();
  }

  /**
   * Get information about the project.
   * @return a String with information about the project
   */
  public static String about() {
    StringBuffer sb = new StringBuffer();

    sb.append("\n");
    sb.append(DoelanRegistery.getAppName() + " version "
        + DoelanRegistery.getAppVersion() + ": "
        + DoelanRegistery.getAppDescription() + "\n");
    sb.append("Compiled on ");

    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
        DateFormat.MEDIUM, Locale.UK);

    Date compileDate = DoelanRegistery.getAppCompileDate();

    if (compileDate != null)
      sb.append(df.format(compileDate));
    sb.append("\n\n");
    sb.append(DoelanRegistery.getAppCopyright() + "\n");
    sb.append("This software is published under the " + Defaults.LICENCE
        + ".\n\n");

    sb.append("Website: " + DoelanRegistery.getAppURL() + "\n");

    return sb.toString();
  }

  /**
   * Get information about the project.
   * @return a String with information about the project
   */
  public static String licence() {
    StringBuffer sb = new StringBuffer();

    sb.append(DoelanRegistery.getAppCopyright() + "\n");
    sb.append("This software is published under the " + Defaults.LICENCE
        + ".\n\n");

    sb.append("Website: " + DoelanRegistery.getAppURL() + "\n");

    return sb.toString();
  }

  /**
   * Get the path of the alterantive web browser.
   * @return The path of the alternative path
   */
  public static String getAlternativeBrowserPath() {

    return Registery.getRegistery().getProperty(
        "doelan.alternative.browser.path");
  }

  /**
   * Get the browser path.
   * @return The browser path
   */
  public static String getBrowserPath() {

    return WebBrowser.getBrowserPath(getAlternativeBrowserPath());
  }

  /**
   * Test if applet mode is set.
   * @return true if applet mode is set
   */
  public static boolean isAppletMode() {
    return "true".equals(Registery.getRegistery().getProperty(
        "doelan.applet.mode"));
  }

  /**
   * Set the applet mode.
   * @param appletMode The applet mode
   */
  public static void setAppletMode(final boolean appletMode) {
    Registery.getRegistery().setProperty("doelan.applet.mode", "" + appletMode);
  }

  /**
   * Get the width of the doelan charts.
   * @return The width in pixel of doelan charts
   */
  public static int getDoelanChartWidth() {
    return Integer.parseInt(Registery.getRegistery().getProperty(
        "doelan.chart.witdh", "" + Defaults.DOLEAN_CHART_WIDTH));
  }

  /**
   * Get the height of the doelan charts.
   * @return The width in pixel of doelan charts
   */
  public static int getDoelanChartHeigth() {
    return Integer.parseInt(Registery.getRegistery().getProperty(
        "doelan.chart.height", "" + Defaults.DOELAN_CHART_HEIGHT));
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private DoelanRegistery() {
  }

}