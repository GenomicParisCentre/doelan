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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.ModuleQuery;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowElement;
import fr.ens.transcriptome.nividic.util.Version;
import fr.ens.transcriptome.nividic.util.parameter.NonFixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;
import fr.ens.transcriptome.nividic.util.parameter.ParametersDocument;

/**
 * A helper class that supports XML-like processing for workflows element
 * objects.
 * @author Laurent Jourdren
 */
public class WorkflowElementDocument {

  // For log system
  private static Logger log = Logger.getLogger(WorkflowElementDocument.class);

  /** Stores the workflow element object this object operates on. */
  private WorkflowElement workflowElement;

  private Document document;

  //
  // Getters
  //

  /**
   * Return the workflow element for this document.
   * @return the workflow element object
   */
  public WorkflowElement getWorkflowElement() {
    return workflowElement;
  }

  /**
   * Get the document.
   * @return The document object
   */
  public Document getDocument() {
    return document;
  }

  //
  // Setters
  //

  /**
   * Set the workflowElement object this document operates on.
   * @param workflowElement the parameter object
   */
  public void setWorkflowElement(final WorkflowElement workflowElement) {
    this.workflowElement = workflowElement;
  }

  /**
   * Set the document.
   * @param document Document to set
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
   * @param workflowElement the workflow element object
   */
  public WorkflowElementDocument(final WorkflowElement workflowElement) {
    setWorkflowElement(workflowElement);
  }

  /**
   * Create the xml document
   * @return the xml document
   */
  public Document createDocument() {

    final Document document = DocumentHelper.createDocument();
    final Element root = document.addElement("element");

    final WorkflowElement wfe = getWorkflowElement();

    if (wfe != null) {

      // id
      final Element name = root.addElement("id");
      name.addText(wfe.getId());

      // description
      if (wfe.getComment() != null) {
        final Element comment = root.addElement("comment");
        comment.addText(wfe.getComment());
      }

      // Module
      final Element module = root.addElement("module");

      ModuleQuery mq = wfe.getModuleQuery();

      if (mq != null) {

        // Module name
        if (mq.getModuleName() != null) {
          final Element moduleName = module.addElement("name");
          moduleName.addText(mq.getModuleName());
        }

        //  Module host
        if (mq.getURL() != null) {
          final Element urlHost = module.addElement("url");
          urlHost.addText(mq.getURL().toString());
        }

        // Module version
        if (mq.getVersion() != null) {
          final Element moduleVersion = module.addElement("version");
          moduleVersion.addText(mq.getVersion().toString());
        }

        // Module recommended version
        if (mq.getRecommendedVersion() != null) {
          final Element moduleRecommendedVersion = module
              .addElement("recommendedversion");
          moduleRecommendedVersion.addText(mq.getRecommendedVersion()
              .toString());
        }

        //    Module version
        if (mq.getMinimalVersion() != null) {
          final Element moduleMinimalVersion = module
              .addElement("minimalversion");
          moduleMinimalVersion.addText(mq.getMinimalVersion().toString());
        }

      }

      // Next elements
      String[] elements = wfe.getNextElementsIdentifiers();

      final Element nextElements = root.addElement("nextelements");
      if (elements != null)
        for (int i = 0; i < elements.length; i++) {
          final Element nextElement = nextElements.addElement("nextelement");
          nextElement.addText(elements[i]);
        }

      // Parameters

      try {
        Document params = new ParametersDocument(wfe.getParameters())
            .createDocument();
        root.add(params.getRootElement());
      } catch (ParameterException e) {
        log
            .error("Error while creating xml for parameters : "
                + e.getMessage());
      }

    } // WorkflowElement is null

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

  /**
   * Parse a writer object.
   * @param reader Object to read
   * @return a dom4j document
   * @throws DocumentException if error occurs while reading the document
   */
  public static Document parse(final Reader reader) throws DocumentException {

    SAXReader saxReader = new SAXReader();
    Document document = saxReader.read(reader);
    return document;
  }

  /**
   * Parse a dom4j document to set a Parameters object.
   * @param document dom4j document to read
   * @return The workflow element object
   * @throws DocumentException if an error occurs while reading the document
   */
  public static WorkflowElement parse(final Document document)
      throws DocumentException {

    final Element root = document.getRootElement();
    return parse(root);
  }

  /**
   * Parse a dom4j document to set a Parameters object.
   * @param element Elemt to parse
   * @return The workflow element object
   * @throws DocumentException if an error occurs while reading the document
   */
  public static WorkflowElement parse(final Element element)
      throws DocumentException {

    final WorkflowElement result = new WorkflowElement();
    final ModuleQuery mq = new ModuleQuery();
    result.setModuleQuery(mq);

    for (Iterator i1 = element.elementIterator("id"); i1.hasNext();) {
      final Element idElement = (Element) i1.next();
      result.setId(idElement.getText());
    }

    for (Iterator i1 = element.elementIterator("comment"); i1.hasNext();) {
      final Element commentElement = (Element) i1.next();
      result.setComment(commentElement.getText());
    }

    for (Iterator i1 = element.elementIterator("module"); i1.hasNext();) {
      final Element moduleElement = (Element) i1.next();

      for (Iterator i2 = moduleElement.elementIterator("name"); i2.hasNext();) {
        final Element moduleNameElement = (Element) i2.next();
        mq.setModuleName(moduleNameElement.getText());
      }

      for (Iterator i2 = moduleElement.elementIterator("url"); i2.hasNext();) {
        final Element hostElement = (Element) i2.next();
        try {
          mq.setURL(new URL(hostElement.getText()));
        } catch (MalformedURLException e) {
          log.error("Malformed url : " + e.getMessage());
        }
      }

      for (Iterator i2 = moduleElement.elementIterator("version"); i2.hasNext();) {
        final Element versionElement = (Element) i2.next();
        mq.setVersion(new Version(versionElement.getText()));
      }

      for (Iterator i2 = moduleElement.elementIterator("recommendedversion"); i2
          .hasNext();) {
        final Element recommededVersionELement = (Element) i2.next();
        mq
            .setRecommendedVersion(new Version(recommededVersionELement
                .getText()));
      }

      for (Iterator i2 = moduleElement.elementIterator("minimalversion"); i2
          .hasNext();) {
        final Element minimalVersionELement = (Element) i2.next();
        mq.setMinimalVersion(new Version(minimalVersionELement.getText()));
      }
    }

    // Next elements
    for (Iterator i1 = element.elementIterator("nextelements"); i1.hasNext();) {
      final Element nextElementsElement = (Element) i1.next();

      for (Iterator i2 = nextElementsElement.elementIterator("nextelement"); i2
          .hasNext();) {
        final Element nextElement = (Element) i2.next();

        try {
          result.link(nextElement.getText());
        } catch (PlatformException e) {
          log.error("Error while linking : " + e.getMessage());
        }

      }
    }

    // Parameters
    Parameters params = new NonFixedParameters();
    for (Iterator i1 = element.elementIterator("parameters"); i1.hasNext();) {
      final Element parametersElement = (Element) i1.next();

      try {
        ParametersDocument.parse(parametersElement, params);
      } catch (ParameterException e) {
        log.error("Paramater exception");
      }

    }
    result.setParameters(params);

    return result;
  }

}