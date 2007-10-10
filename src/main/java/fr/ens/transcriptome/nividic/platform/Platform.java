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

import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fr.ens.transcriptome.nividic.Globals;
import fr.ens.transcriptome.nividic.platform.module.ModuleManager;

/**
 * System class for nividic platform environement
 * @author Laurent jourdren
 */
public final class Platform {

  // Log system
  private static Logger log;

  /**
   * start the system.
   */
  public static void start() {
    start(null, null);
  }

  /**
   * Start the system.
   * @param internalsModules Internals modules
   * @param modulesDirectories Modules directories
   */
  public static void start(final Class[] internalsModules,
      final String[] modulesDirectories) {
    start(internalsModules, modulesDirectories, PlatformRegistery.isLog());
  }

  /**
   * Start the system.
   * @param internalsModules Internals modules
   * @param modulesDirectories Modules directories
   * @param isLog Active logFile
   */
  public static void start(final Class[] internalsModules,
      final String[] modulesDirectories, final boolean isLog) {

    //  Log system configuration

    Properties conf = new Properties();

    if (isLog) {

      String logFile = PlatformRegistery.getLogFile();
      if (logFile != null && !PlatformRegistery.isConsoleLog()) {

        conf.put("log4j.rootLogger", Registery.getRegistery().getProperty(
            "log.level", PlatformRegistery.getLogLevel())
            + ", R");
        conf.put("log4j.appender.R", "org.apache.log4j.RollingFileAppender");
        conf.put("log4j.appender.R.File", logFile);
        conf.put("log4j.appender.R.MaxFileSize", "1000KB");
        conf.put("log4j.appender.R.MaxBackupIndex", "1");
        conf.put("log4j.appender.R.layout", "org.apache.log4j.PatternLayout");
        conf.put("log4j.appender.R.layout.ConversionPattern",
            "%d %-5p [%t] (%F:%L) - %m%n");
      } else {
        conf.put("log4j.rootLogger", Registery.getRegistery().getProperty(
            "log.level", PlatformRegistery.getLogLevel())
            + ", A1");
        conf.put("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");

        conf.put("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
        conf.put("log4j.appender.A1.layout.ConversionPattern",
            "%r %-5p (%F:%L) - %m%n");

      }
    } else {
      conf.put("log4j.rootLogger", Registery.getRegistery().getProperty(
          "log.level", PlatformRegistery.getLogLevel())
          + ", R");
      conf.put("log4j.appender.R", "org.apache.log4j.varia.NullAppender");
    }

    PropertyConfigurator.configure(conf);
    log = Logger.getLogger(Platform.class);

    log.info("Nividic Platform started.");

    // Start the module manager
    ModuleManager mm = ModuleManager.getManager();
    mm.addInternalModules(Globals.INTERNALS_MODULES);
    if (internalsModules != null)
      mm.addInternalModules(internalsModules);
    mm.addPath(Globals.MODULES_GLOBAL_DIRECTORY);
    if (modulesDirectories != null)
      mm.addPaths(modulesDirectories);
    mm.findModules();
  }

  /**
   * Private constructor.
   */
  private Platform() {
  }

}