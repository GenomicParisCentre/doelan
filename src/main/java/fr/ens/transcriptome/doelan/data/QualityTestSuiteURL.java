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


package fr.ens.transcriptome.doelan.data;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.platform.PlatformRegistery;

/**
 * This class define a location to a qualtity test suite.
 * @author Laurent Jourdren
 */
public class QualityTestSuiteURL {

  //For log system
  private static Logger log = Logger.getLogger(QualityTestSuiteURL.class);

  private String name;

  private URL url;

  //
  // Getters
  //

  /**
   * Get the name of the TestSuiteURL
   * @return The name of the TestSuiteURL
   */
  public String getName() {
    return name;
  }

  /**
   * Get the URL of the test TestSuiteURL
   * @return the URL of the test suite
   */
  public URL getURL() {
    return url;
  }

  //
  // Setters
  //

  /**
   * Set the name of the TestSuiteURL
   * @param name Name to set
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Set the url of the TestSuite
   * @param url URL to set
   */
  public void setURL(final URL url) {
    this.url = url;
  }

  /**
   * Set the url of the TestSuite
   * @param url URL to set
   */
  public void setUrl(final String url) {

    if (url == null)
      return;

    try {
      if (url.startsWith("file:") || url.startsWith("ftp:")
          || url.startsWith("http:"))
        this.url = new URL(url);
      else {
        File f = new File(url);
        if (!f.isAbsolute())
          f = new File(PlatformRegistery.getConfDirectory() + File.separator + url);
        if (f.exists() && !f.isDirectory()) {
          //this.url = new
          // URL("file://"+URLEncoder.encode(f.getAbsolutePath(),"UTF-8"));
          this.url = f.toURL();
        }

      }
    } catch (MalformedURLException e) {
      log.error("Malformed URL: " + e);
      return;
    }
  }

}