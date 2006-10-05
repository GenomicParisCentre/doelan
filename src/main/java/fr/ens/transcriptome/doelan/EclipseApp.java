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

import java.util.Properties;

/**
 * This class allow to launch Doelan within Eclipse
 * @author Laurent Jourdren
 */
public final class EclipseApp {

  /**
   * Main method.
   * @param args arguments of the main method
   */
  public static void main(final String[] args) {

    String name = EclipseApp.class.getName().substring(
        EclipseApp.class.getPackage().getName().length() + 1);

    System.out.println(name);

    Properties properties = new Properties();
    properties.setProperty("nividic.conf.dir", System
        .getProperty("java.io.tmpdir"));
    properties.setProperty("nividic.log", "true");
    properties.setProperty("nividic.log.console", "true");
    properties.setProperty("nividic.log.level", "debug");

    App.main(properties, args);
  }

  //
  // Constructor
  //

  private EclipseApp() {
  }

}
