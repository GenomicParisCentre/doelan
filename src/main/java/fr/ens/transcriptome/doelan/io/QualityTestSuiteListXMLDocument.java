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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import fr.ens.transcriptome.doelan.DoelanException;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteList;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteURL;

/**
 * @author Laurent jourdren
 */
class QualityTestSuiteListXMLDocument {

  /** Stores the test suite object this object operates on. */
  private QualityTestSuiteList list;

  private Document document;

  //
  // Getters
  //

  /**
   * Return the test suite list object for this document.
   * @return the test suite list object
   */
  public QualityTestSuiteList getTestSuiteList() {
    return list;
  }

  /**
   * Return the dom4j working document
   * @return the dom4j working document
   */
  public Document getDocument() {
    return document;
  }

  //
  // Setters
  //

  /**
   * Set the test suite object this documnet operates on.
   * @param list the test suite list object
   */
  public void setTestSuiteList(final QualityTestSuiteList list) {
    this.list = list;
  }

  /**
   * Set the dom4j working document
   * @param document The dom4j working document
   */
  public void setDocument(final Document document) {
    this.document = document;
  }

  //
  // Constructor
  //

  /**
   * Creates a new instance of ParametersXMLDocument and sets the paramter
   * object to be processed.
   * @param list the test suite list object
   */
  public QualityTestSuiteListXMLDocument(final QualityTestSuiteList list) {
    setTestSuiteList(list);
    createDocument();
  }

  /**
   * Create the xml document
   * @return the xml document
   */
  public Document createDocument() {

    Document document = DocumentHelper.createDocument();
    final Element root = document.addElement("qualitytestsuitelist");

    final QualityTestSuiteList l = getTestSuiteList();

    if (l != null) {

      String[] types = l.getChipTypes();

      for (int i = 0; i < types.length; i++) {

        final Element chipType = root.addElement("chiptype");

        final Element name = chipType.addElement("name");
        name.addText(types[i]);

        QualityTestSuiteURL[] urls = l.getTestSuiteURLs(types[i]);

        for (int j = 0; j < urls.length; j++) {
          final Element testsuiteElement = chipType
              .addElement("qualitytestsuite");
          Element nameTestSuiteElement = testsuiteElement.addElement("name");

          nameTestSuiteElement.addText(urls[j].getName());

          Element urlElement = testsuiteElement.addElement("url");
          urlElement.addText(urls[j].getURL().toString());
        }
      } // chip types
    } // list not null

    setDocument(document);
    return document;
  }

  //
  // Writer
  //

  /**
   * Writes a configuration (or parts of it) to the given writer.
   * @param out the output writer
   * @param prefix the prefix of the subset to write; if <b>null </b>, the whole
   *                 configuration is written
   * @param root the name of the root element of the resulting document; <b>null
   *                 </b> for a default name
   * @param pretty flag for the pretty print mode
   * @throws IOException if an IO error occurs
   * @throws DocumentException if there is an error during processing
   */
  public void write(final Writer out, final String prefix, final String root,
      final boolean pretty) throws IOException, DocumentException {
    OutputFormat format = pretty ? OutputFormat.createPrettyPrint()
        : OutputFormat.createCompactFormat();

    XMLWriter writer = new XMLWriter(out, format);
    writer.write(getDocument());
  }

  /**
   * Writes a configuration (or parts of it) to the given writer. This
   * overloaded version always uses pretty print mode.
   * @param out the output writer
   * @param prefix the prefix of the subset to write; if <b>null </b>, the whole
   *                 configuration is written
   * @param root the name of the root element of the resulting document; <b>null
   *                 </b> for a default name
   * @throws IOException if an IO error occurs
   * @throws DocumentException if there is an error during processing
   */
  public void write(final Writer out, final String prefix, final String root)
      throws IOException, DocumentException {
    write(out, prefix, root, true);
  }

  /**
   * Writes a configuration (or parts of it) to the given writer. The resulting
   * document's root element will be given a default name.
   * @param out the output writer
   * @param prefix the prefix of the subset to write; if <b>null </b>, the whole
   *                 configuration is written
   * @param pretty flag for the pretty print mode
   * @throws IOException if an IO error occurs
   * @throws DocumentException if there is an error during processing
   */
  public void write(final Writer out, final String prefix, final boolean pretty)
      throws IOException, DocumentException {
    write(out, prefix, null, pretty);
  }

  /**
   * Writes a configuration (or parts of it) to the given writer. The resulting
   * document's root element will be given a default name. This overloaded
   * version always uses pretty print mode.
   * @param out the output writer
   * @param prefix the prefix of the subset to write; if <b>null </b>, the whole
   *                 configuration is written
   * @throws IOException if an IO error occurs
   * @throws DocumentException if there is an error during processing
   */
  public void write(final Writer out, final String prefix) throws IOException,
      DocumentException {
    write(out, prefix, true);
  }

  /**
   * Writes the wrapped configuration to the given writer. The resulting
   * document's root element will be given a default name.
   * @param out the output writer
   * @param pretty flag for the pretty print mode
   * @throws IOException if an IO error occurs
   * @throws DocumentException if there is an error during processing
   */
  public void write(final Writer out, final boolean pretty) throws IOException,
      DocumentException {
    write(out, null, null, pretty);
  }

  /**
   * Writes the wrapped configuration to the given writer. The resulting
   * document's root element will be given a default name. This overloaded
   * version always uses pretty print mode.
   * @param out the output writer
   * @throws IOException if an IO error occurs
   * @throws DocumentException if there is an error during processing
   */
  public void write(final Writer out) throws IOException, DocumentException {
    write(out, true);
  }

  //
  // Read XML
  //

  public static QualityTestSuiteList parse(final Reader reader)
      throws DocumentException, DoelanException {

    SAXReader saxReader = new SAXReader();
    Document document = saxReader.read(reader);
    return parse(document);
  }

  public static QualityTestSuiteList parse(final Document document)
      throws DocumentException, DoelanException {

    Element root = document.getRootElement();
    QualityTestSuiteList tsl = new QualityTestSuiteList();

    // iterate through child elements of root with element name "foo"
    for (Iterator i = root.elementIterator("chiptype"); i.hasNext();) {
      final Element chiptypeElement = (Element) i.next();

      String name = null;

      for (Iterator i2 = chiptypeElement.elementIterator("name"); i2.hasNext();) {
        name = ((Element) i2.next()).getText();
        tsl.addChipType(name);
      }

      for (Iterator i3 = chiptypeElement.elementIterator("qualitytestsuite"); i3
          .hasNext();) {
        final Element qualitytestsuiteElement = (Element) i3.next();

        QualityTestSuiteURL tsURL = new QualityTestSuiteURL();

        for (Iterator i4 = qualitytestsuiteElement.elementIterator("name"); i4
            .hasNext();) {
          final Element tsNameElement = (Element) i4.next();
          tsURL.setName(tsNameElement.getTextTrim());
        }

        for (Iterator i5 = qualitytestsuiteElement.elementIterator("url"); i5
            .hasNext();) {
          final Element tsURLElement = (Element) i5.next();
          tsURL.setUrl(tsURLElement.getTextTrim());
        }

        tsl.addTestSuite(name, tsURL);

      } // qualitytestsuite Element
    } // chiptype

    return tsl;
  }

}