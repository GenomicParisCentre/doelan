/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.nividic.platform;

import java.io.File;

import fr.ens.transcriptome.nividic.Globals;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * This class extends Registery for nividic configuration.
 * @author Laurent Jourdren
 */
public final class PlatformRegistery {

  private static final String WINDOWS_APPLICATION_DATA_DIR = "Application Data";

  /**
   * Get the absolute path of the base directory of the application.
   * @return The path of the base directory
   */
  public static String getBaseDirectory() {

    if ("true".equals(Registery.getRegistery().getProperty("nividic.debug")))
      return Registery.getRegistery().getProperty(
          "nividic.base.dir",
          SystemUtils.isUnixSystem() ? Registery.getRegistery().getProperty(
              "nividic.unix.base.dir.debug", System.getProperty("user.home"))
              : Registery.getRegistery().getProperty(
                  "nividic.windows.base.dir.debug",
                  System.getProperty("user.home")));

    return Registery.getRegistery().getProperty(
        "nividic.base.dir",
        SystemUtils.getClassSource(PlatformRegistery.class).getParentFile()
            .getParentFile().getAbsolutePath());
  }

  /**
   * Get the absolute path of the directory for configuration files.
   * @return The path of the directory for configuration
   */
  public static String getConfDirectory() {

    if (!Registery.getRegistery().containsKey("nividic.conf.dir")) {

      if (SystemUtils.isWindowsSystem()) {

        final String userConfDir = System.getProperty("user.home")
            + File.separator + WINDOWS_APPLICATION_DATA_DIR + File.separator
            + getAppName();

        File userConfDirFile = new File(userConfDir);
        if (!userConfDirFile.exists())
          userConfDirFile.mkdirs();

        return userConfDir;
      }

      if (SystemUtils.isMacOsX() || SystemUtils.isUnixSystem()) {

        final String userConfDir = System.getProperty("user.home")
            + File.separator + WINDOWS_APPLICATION_DATA_DIR + File.separator
            + "." + getAppName().toLowerCase();

        File userConfDirFile = new File(userConfDir);
        if (!userConfDirFile.exists())
          userConfDirFile.mkdirs();

        return userConfDir;
      }

    }

    return Registery.getRegistery().getProperty("nividic.conf.dir",
        getBaseDirectory() + File.separator + Defaults.SUBDIR_CONF);
  }

  /**
   * Get the absolute path of the directory for plugins.
   * @return The path of the directory for plugins
   */
  public static String getPluginsDirectory() {

    return Registery.getRegistery().getProperty("nividic.plugins.dir",
        getBaseDirectory() + File.separator + Defaults.SUBDIR_PLUGINS);
  }

  /**
   * Get the path of the temporay directory.
   * @return the temporary directory
   */
  public static String getDefaultTemporaryDirectory() {

    return Registery.getRegistery().getProperty("nividic.tmp.dir",
        System.getProperty("java.io.tmpdir"));
  }

  /**
   * Get the path of the logFile.
   * @return The path of the log file
   */
  public static String getLogFile() {

    final String propertyLogFile = Registery.getRegistery().getProperty(
        "nividic.log.file");

    if (propertyLogFile != null) {

      File dir = new File(propertyLogFile).getParentFile();
      if (!dir.exists())
        if (!dir.mkdirs())
          return null;

      return propertyLogFile;
    }

    String defaultLogDir = getBaseDirectory() + File.separator
        + Defaults.SUBDIR_LOGS;

    File dir = new File(defaultLogDir);
    if (!dir.exists())
      if (!dir.mkdirs())
        return null;

    return defaultLogDir
        + File.separator
        + Registery.getRegistery().getProperty("nividic.log.file.shortname",
            Globals.LOG_FILE);
  }

  /**
   * Test if the log is enabled
   * @return true if the log is enabled
   */
  public static boolean isLog() {
    return "true".equals(Registery.getRegistery().getProperty("nividic.log"));
  }

  /**
   * Test if the log is enabled
   * @return true if the log is enabled
   */
  public static boolean isConsoleLog() {
    return "true".equals(Registery.getRegistery().getProperty(
        "nividic.log.console"));
  }

  /**
   * Get the log debug level
   * @return Get the log debug level
   */
  public static String getLogLevel() {
    return Registery.getRegistery().getProperty("nividic.log.level",
        Globals.LOG_LEVEL);
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

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private PlatformRegistery() {
  }

}