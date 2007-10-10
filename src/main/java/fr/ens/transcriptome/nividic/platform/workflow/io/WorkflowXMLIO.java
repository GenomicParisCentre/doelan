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

package fr.ens.transcriptome.nividic.platform.workflow.io;

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

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.workflow.Workflow;

/**
 * This class read and write workflow in xml.
 * @author Laurent Jourdren
 */
public class WorkflowXMLIO implements WorkflowIO {

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
   * @param filename Filename to set
   */
  public void setFilename(final String filename) {
    this.filename = filename;
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
   * Write a test suite
   * @param workflow workflow to write
   * @throws PlatformException if an error occurs while reading data
   */
  public void write(final Workflow workflow) throws PlatformException {

    if (getFilename() == null && getOutputStream() == null)
      throw new PlatformException("OutputStream and Filename are null");

    try {

      if (getOutputStream() == null)
        setOutputStream(new FileOutputStream(getFilename()));

      final PrintWriter pw = new PrintWriter(getOutputStream());
      final WorkflowXMLDocument doc = new WorkflowXMLDocument(workflow);
      doc.createDocument();
      doc.write(pw);
      pw.close();

    } catch (FileNotFoundException e) {
      throw new PlatformException("File not found: " + getFilename());
    } catch (IOException e) {
      throw new PlatformException("IOException: " + e);
    } catch (DocumentException e) {
      throw new PlatformException("Error while creating XML document");
    }

  }

  /**
   * Read a test suite.
   * @return A new QualityTestSuite object
   * @throws PlatformException if an error occurs while wrinting data
   */
  public Workflow read() throws PlatformException {

    if (getFilename() == null && getInputStream() == null)
      throw new PlatformException("InputStream and Filename are null");

    Workflow result = null;

    try {
      if (getInputStream() == null)
        setInputStream(new FileInputStream(getFilename()));

      final Reader r = new InputStreamReader(getInputStream());

      result = WorkflowXMLDocument.parse(r);
      r.close();
    } catch (DocumentException e) {
      throw new PlatformException("Error while reading data");
    } catch (FileNotFoundException e) {
      throw new PlatformException("File not found: " + getFilename());
    } catch (IOException e) {
      throw new PlatformException("Error while reading file : "
          + getFilename());
    }

    return result;
  }

  //
  // Constructor
  //

  /**
   * Constructor, read or write a workflow in a XML file.
   * @param filename File to open
   */
  public WorkflowXMLIO(final String filename) {
    setFilename(filename);
  }

  /**
   * Constructor, read or write a workflow in a XML file.
   * @param is InputStream to open
   */
  public WorkflowXMLIO(final InputStream is) {
    setInputStream(is);
  }

  /**
   * Constructor, read or write a workflow in a XML file.
   * @param os OutputStream to open
   */
  public WorkflowXMLIO(final OutputStream os) {
    setOutputStream(os);
  }

}