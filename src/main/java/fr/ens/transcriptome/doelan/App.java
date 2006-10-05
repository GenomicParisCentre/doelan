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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.ens.transcriptome.doelan.gui.CommonWindow;
import fr.ens.transcriptome.nividic.platform.Registery;

/**
 * A Class to test the application with standalone application.
 * @author Laurent Jourdren
 */
public final class App extends JFrame {

  //
  // Get Genepix data
  //

  /**
   * Create the thread for the GUI
   */
  private static void mainInit() {

    JFrame.setDefaultLookAndFeelDecorated(true);
    Toolkit.getDefaultToolkit().setDynamicLayout(true);

    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {

        App app = new App();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.graphicalInit();
        app.pack();
        app.setResizable(true);
        app.setVisible(true);
      }
    });
  }

  /**
   * Create the GUI and show it. For thread safety, this method should be
   * invoked from the event-dispatching thread.
   */
  private void graphicalInit() {

    this.setName(Defaults.APP_NAME);
    this.setTitle(Defaults.APP_NAME);
    URL url = CommonWindow.class.getResource("/files/doelan-16.png");
    ImageIcon ii = new ImageIcon(url);
    setIconImage(ii.getImage());

    CommonWindow window = new CommonWindow(this, null);
    window.init();

  }

  /**
   * Get the minimun size of the main window.
   * @return a dimension object
   */
  public Dimension getMinimumSize() {
    return new Dimension(Defaults.WINDOW_WIDTH, Defaults.WINDOW_HEIGHT);
  }

  /**
   * Get the minimun size of the main window.
   * @return a dimension object
   */
  public Dimension getPreferredSize() {
    return new Dimension(Defaults.WINDOW_WIDTH, Defaults.WINDOW_HEIGHT);
  }

  /*
   * public Dimension getMaximumSize() { return new Dimension(666, 666); }
   */

  /**
   * Create the Options object for the command line.
   * @return The Option object
   */
  private static Options makeOptions() {

    Option help = new Option("help", "show this message");
    Option licence = new Option("about", "show information this software");
    Option suitelist = OptionBuilder.withArgName("suitelist").withDescription(
        "testsuite list to open").create("suitelist");
    Option suite = OptionBuilder.withArgName("suite").withDescription(
        "testsuite open").create("suite");
    Option conf = OptionBuilder.withArgName("conf").withDescription(
        "configuration file").create("conf");

    Options options = new Options();
    options.addOption(help);
    options.addOption(licence);
    options.addOption(suitelist);
    options.addOption(suite);
    options.addOption(conf);

    return options;
  }

  /**
   * Show information about this application.
   */
  private static void about() {

    System.out.println(DoelanRegistery.licence());
    System.exit(0);
  }

  /**
   * Show licence information about this application.
   */
  private static void licence() {

    System.out.println(DoelanRegistery.about());
    System.exit(0);
  }

  /**
   * Main method.
   * @param args Command line arguments
   */
  public static void main(final String[] args) {

    System.out.println("hello from main(args[])");

    // Initialize default values
    DoelanRegistery.init();

    commandLineMain(args);
  }

  private static void commandLineMain(final String[] args) {

    Options options = makeOptions();
    CommandLineParser parser = new GnuParser();

    try {
      // parse the command line arguments
      CommandLine line = parser.parse(options, args);

      if (line.hasOption("help")) {

        // Show help message
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("aligneSondes", options);

        System.exit(0);
      }

      if (line.hasOption("about"))
        about();

      if (line.hasOption("licence"))
        about();

      if (line.hasOption("conf"))
        Registery.loadRegistery(line.getOptionValue("conf"));

    } catch (ParseException exp) {
      System.err.println("Error analysing command line");
      System.exit(1);
    }

    mainInit();
  }

  /**
   * Main method.
   * @param is Input stream for configuration file
   */
  public static void main(final InputStream is) {

    // Initialize default values
    DoelanRegistery.init();

    if (is != null)
      Registery.loadRegistery(is);

    mainInit();
  }

  /**
   * Main method.
   * @param properties for configuration
   * @param args command line arguments
   */
  public static void main(final Properties properties, final String[] args) {

    // Initialize default values
    DoelanRegistery.init();

    if (properties != null)
      Registery.addPropertiesToRegistery(properties);

    commandLineMain(args);
  }

}