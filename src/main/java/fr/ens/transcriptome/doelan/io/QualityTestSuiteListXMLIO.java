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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;

import org.dom4j.DocumentException;

import fr.ens.transcriptome.doelan.DoelanException;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteList;

/**
 * @author Laurent Jourdren
 */
class QualityTestSuiteListXMLIO implements QualityTestSuiteListIO {

  private String filename;
  private InputStream in;
  private OutputStream out;

  //
  // Getters
  //

  /**
   * Get the filename.
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

  /**
   * Get the inputstream.
   * @return The inputstream
   */
  public InputStream getInputStream() {
    return in;
  }

  /**
   * Get the outputstream.
   * @return The outputstream
   */
  public OutputStream getOutputStream() {
    return out;
  }

  //
  // Setters
  //

  /**
   * Set the filename.
   * @param string
   */
  public void setFilename(final String string) {
    filename = string;
  }

  /**
   * Set the inputstream.
   * @param stream The inputStream to set
   */
  public void setInputStream(final InputStream stream) {
    in = stream;
  }

  /**
   * Set the outputStream
   * @param stream The outputStream to set
   */
  public void setOutputStream(final OutputStream stream) {
    out = stream;
  }

  //
  // Methods from QualityTestSuiteIO
  //

  /**
   * Write a test suite list.
   * @param suite Test list suite to write
   * @throws DoelanException if an error occurs while reading data
   */
  public void write(final QualityTestSuiteList suite) throws DoelanException {

    if (getFilename() == null && getOutputStream() == null)
      throw new DoelanException("Filename is null");

    try {

      if (getOutputStream() == null)
        setOutputStream(new FileOutputStream(getFilename()));

      final PrintWriter pw = new PrintWriter(getOutputStream());
      final QualityTestSuiteListXMLDocument doc = new QualityTestSuiteListXMLDocument(
          suite);
      doc.createDocument();
      doc.write(pw);
      pw.close();

    } catch (FileNotFoundException e) {
      throw new DoelanException("File not found: " + getFilename());
    } catch (IOException e) {
      throw new DoelanException("IOException: " + e);
    } catch (DocumentException e) {
      throw new DoelanException("Error while creating XML document");
    }

  }

  /**
   * Read a test suite list.
   * @return A new QualityTestSuiteList object
   * @throws DoelanException if an error occurs while wrinting data
   */
  public QualityTestSuiteList read() throws DoelanException {

    if (getFilename() == null && getInputStream() == null)
      throw new DoelanException("Filename is null");

    QualityTestSuiteList result = null;

    try {
      if (getInputStream() == null)
        setInputStream(new FileInputStream(getFilename()));

      final Reader r = new InputStreamReader(getInputStream());

      result = QualityTestSuiteListXMLDocument.parse(r);

      getInputStream().close();
    } catch (DocumentException e) {
      throw new DoelanException("Error while reading data");
    } catch (FileNotFoundException e) {
      throw new DoelanException("File not found: " + getFilename());
    } catch (IOException e) {
      throw new DoelanException("Error while reading data");
    }

    return result;
  }

}
