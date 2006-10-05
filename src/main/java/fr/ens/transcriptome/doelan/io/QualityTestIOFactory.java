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

package fr.ens.transcriptome.doelan.io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A factory for testsuitelist io.
 * @author Laurent Jourdren
 */
public class QualityTestIOFactory {

  /**
   * Create IO object for quality tests suites lists.
   * @param name Name of the stream
   * @return a new QualityTestSuiteListIO
   */
  public QualityTestSuiteListIO createQualityTestSuiteListIO(final String name) {

    QualityTestSuiteListXMLIO result = new QualityTestSuiteListXMLIO();
    result.setFilename(name);

    return result;
  }

  /**
   * Create IO object for quality tests suites lists.
   * @param in InputStream to read
   * @return a new QualityTestSuiteListIO
   */
  public QualityTestSuiteListIO createQualityTestSuiteListIO(
      final InputStream in) {

    QualityTestSuiteListXMLIO result = new QualityTestSuiteListXMLIO();
    result.setInputStream(in);

    return result;
  }

  /**
   * Create IO object for quality tests suites lists.
   * @param out OutputStream to write
   * @return a new QualityTestSuiteListIO
   */
  public QualityTestSuiteListIO createQualityTestSuiteListIO(
      final OutputStream out) {

    QualityTestSuiteListXMLIO result = new QualityTestSuiteListXMLIO();
    result.setOutputStream(out);

    return result;
  }

}