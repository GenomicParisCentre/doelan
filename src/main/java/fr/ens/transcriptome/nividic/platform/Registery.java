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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * This class implements a registery for the configuration of the application.
 * @author Laurent Jourdren
 */
public final class Registery {

  //For logging system
  private static Logger log = Logger.getLogger(Registery.class);

  private static Properties registery = new Properties();

  /**
   * Get the registery object.
   * @return An object Property with system properties
   */
  public static Properties getRegistery() {
    return registery;
  }

  /**
   * Load registery data.
   * @param filename Filename to load
   */
  public static void loadRegistery(final String filename) {

    if (filename == null)
      return;

    loadRegistery(new File(filename));
  }

  /**
   * Load registery data.
   * @param file File to load
   */
  public static void loadRegistery(final File file) {

    if (file == null)
      return;

    try {
      FileInputStream fis = new FileInputStream(file);
      registery.load(fis);
      fis.close();
    } catch (FileNotFoundException e) {
      log.error("File not found : " + file.getAbsolutePath());
    } catch (IOException e) {
      log.error("Error while loading file : " + file.getAbsolutePath());
    }
  }

  /**
   * Load registery data.
   * @param is Stream to load
   */
  public static void loadRegistery(final InputStream is) {

    if (is == null)
      return;

    try {
      registery.load(is);
      is.close();
    } catch (IOException e) {
      log.error("Error while loading input stream : ");
    }
  }

  /**
   * Load registery data.
   * @param url URL to load
   */
  public static void loadRegistery(final URL url) {

    if (url == null)
      return;

    try {
      registery.load(url.openStream());
    } catch (IOException e) {
      log.error("Error while loading url : " + url.toString());
    }
  }

  /**
   * Add properties to the registery.
   * @param properties to add
   */
  public static void addPropertiesToRegistery(final Properties properties) {

    if (properties == null)
      return;

    Iterator it = properties.keySet().iterator();

    while (it.hasNext()) {
      String element = (String) it.next();
      registery.setProperty(element, properties.getProperty(element));
    }
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private Registery() {
  }

}