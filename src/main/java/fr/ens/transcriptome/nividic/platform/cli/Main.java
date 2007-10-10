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

package fr.ens.transcriptome.nividic.platform.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.ens.transcriptome.nividic.Globals;
import fr.ens.transcriptome.nividic.platform.Platform;
import fr.ens.transcriptome.nividic.platform.module.ModuleManager;

/**
 * This class is the main class for command line interface (CLI)
 *
 * @author Laurent Jourdren
 */
public final class Main {

  /**
   * Create the Options object for the command line.
   * @return The Option object
   */
  private static Options makeOptions() {

    Option help = new Option("help", "show this message");
    Option licence = new Option("licence", "show information about software licence");
    Option about = new Option("about", "show information this software");
    Option listmodule = new Option("listmodules", "list modules available");
    Option showmodule =
      OptionBuilder.withArgName("showmodule").withDescription(
        "show information about a module").create(
        "module");

    Options options = new Options();
    options.addOption(help);
    options.addOption(licence);
    options.addOption(about);
    options.addOption(listmodule);
    options.addOption(showmodule);

    return options;
  }

  /**
   * Show information about this application.
   */
  private static void about() {
    System.out.println(
      Globals.APP_NAME + " version " + Globals.VERSION + " : " + Globals.APP_DESCRITPION);
    System.exit(0);
  }

  /**
   * Show information about the software licence.
   */
  private static void licence() {
    System.out.println("This software is developed under " + Globals.LICENCE);

    BufferedReader br =
      new BufferedReader(
        new InputStreamReader(
          Main.class.getClassLoader().getResourceAsStream("META-INF/LICENCE.txt")));
    String line = null;
    try {
      while ((line = br.readLine()) != null)
        System.out.println(line);
      
      br.close();
    } catch (IOException e) {
      System.err.println("Error while reading the README.txt resource");
    }
    
    System.out.println();
    System.exit(0);
  }

  /**
   * Start the nividic platform.
   */
  public static void start() {

    Platform.start();
    ModuleManager mm = ModuleManager.getManager();
    mm.addInternalModules(Globals.INTERNALS_MODULES);
    mm.addPath(Globals.MODULES_USER_DIRECTORY);
    mm.findModules();
    /*
    Module m1 = m.getModule("TranslateGPRToBioAssay");
    System.out.println(m1.aboutModule().getName());
    Module m2 = m.getModule("TestElement");
    System.out.println(m2.aboutModule().getName());
    */
  }

  //
  // Main method.
  //

  /**
   * Main method.
   * @param args Command line arguments
   */
  public static void main(final String[] args) {

    Options options = makeOptions();
    CommandLineParser parser = new GnuParser();
    try {
      // parse the command line arguments
      CommandLine line = parser.parse(options, args);
      if (line.hasOption("help")) { // Show help message
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("aligneSondes", options);
        System.exit(0);
      }

      if (line.hasOption("about"))
        about();
      if (line.hasOption("licence"))
        licence();
      if (line.hasOption("listmodules")) {
        start();
        ModuleCLI.listModules();
      }

      if (line.hasOption("showmodule")) {
        start();
        ModuleCLI.showModule(line.getOptionValue("module"));
      } else {
        System.out.println("Missing arguments (' --help ' for help)");
        System.exit(0);
      }

    } catch (ParseException exp) {
      System.err.println("Error analysing command line");
      System.exit(1);
    }

  }

  /**
   * Private constructor.
   */
  private Main() {
  }

}
