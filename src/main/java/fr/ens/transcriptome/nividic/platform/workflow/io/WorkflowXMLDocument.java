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
import fr.ens.transcriptome.nividic.platform.workflow.Workflow;
import fr.ens.transcriptome.nividic.util.Version;

/**
 * A helper class that supports XML-like processing for workflows objects.
 * @author Laurent Jourdren
 */
class WorkflowXMLDocument {

  // For log system
  private static Logger log = Logger.getLogger(WorkflowXMLDocument.class);

  /** Stores the workflow element object this object operates on. */
  private Workflow workflow;

  private Document document;

  //
  // Getters
  //

  /**
   * Return the workflow for this document.
   * @return the workflow object
   */
  public Workflow getWorkflow() {
    return workflow;
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
   * Set the workflow object this document operates on.
   * @param workflow the parameter object
   */
  public void setWorkflow(final Workflow workflow) {
    this.workflow = workflow;
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
   * @param workflow the workflow object
   */
  public WorkflowXMLDocument(final Workflow workflow) {
    setWorkflow(workflow);
  }

  /**
   * Create the xml document
   * @return the xml document
   */
  public Document createDocument() {

    final Document document = DocumentHelper.createDocument();
    final Element root = document.addElement("workflow");

    final Workflow wf = getWorkflow();

    if (wf != null) {

      // name
      if (wf.getName() != null) {
        final Element name = root.addElement("name");
        name.addText(wf.getName());
      }

      // type
      if (wf.getType() != null) {
        final Element type = root.addElement("type");
        type.addText(wf.getType());
      }

      // version
      if (wf.getVersion() != null) {
        final Element version = root.addElement("version");
        version.addText(wf.getVersion().toString());
      }

      // description
      if (wf.getDescription() != null) {
        final Element description = root.addElement("description");
        description.addText(wf.getDescription());
      }

      //  author
      if (wf.getAuthor() != null) {
        final Element type = root.addElement("author");
        type.addText(wf.getAuthor());
      }

      // organisation
      if (wf.getOrganisation() != null) {
        final Element description = root.addElement("organisation");
        description.addText(wf.getOrganisation());
      }

      // generator
      if (wf.getGenerator() != null) {
        final Element description = root.addElement("generator");
        description.addText(wf.getGenerator());
      }

      // annotations
      if (wf.getAnnotations() != null && wf.getAnnotations().size() > 0) {
        final Element annotations = root.addElement("annotations");

        Iterator it = wf.getAnnotations().keySet().iterator();
        while (it.hasNext()) {
          final Element annotation = annotations.addElement("annotation");
          String name = (String) it.next();
          String value = wf.getAnnotations().getProperty(name);

          final Element annotationName = annotation.addElement("name");
          annotationName.addText(name);
          final Element annotationValue = annotation.addElement("value");
          annotationValue.addText(value);
        }

      }

      // First element
      if (wf.getRootElementName() != null) {
        final Element description = root.addElement("firstelement");
        description.addText(wf.getRootElementName());
      }

      // elements
      final Element elements = root.addElement("elements");
      String[] elementsNames = wf.getElementsNames();
      if (elementsNames != null)
        for (int i = 0; i < elementsNames.length; i++) {
          Document element = new WorkflowElementDocument(wf
              .getElement(elementsNames[i])).createDocument();
          elements.add(element.getRootElement());
        }

    } // Workflow is null

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
   * @return a workflow object
   * @throws DocumentException if error occurs while reading the document
   */
  public static Workflow parse(final Reader reader) throws DocumentException {

    SAXReader saxReader = new SAXReader();
    Document document = saxReader.read(reader);
    return parse(document);
  }

  /**
   * Parse a dom4j document to set a Parameters object.
   * @param document dom4j document to read
   * @return The workflow object
   * @throws DocumentException if an error occurs while reading the document
   */
  public static Workflow parse(final Document document)
      throws DocumentException {

    final Element root = document.getRootElement();
    return parse(root);
  }

  /**
   * Parse a dom4j document to set a Parameters object.
   * @param element Element to parse
   * @return The workflow object
   * @throws DocumentException if an error occurs while reading the document
   */
  public static Workflow parse(final Element element) throws DocumentException {

    final Workflow result = new Workflow();

    for (Iterator i1 = element.elementIterator("name"); i1.hasNext();) {
      final Element nameElement = (Element) i1.next();
      result.setName(nameElement.getText());
    }

    // description
    for (Iterator i1 = element.elementIterator("description"); i1.hasNext();) {
      final Element descriptionElement = (Element) i1.next();
      result.setDescription(descriptionElement.getText());
    }

    //  type
    for (Iterator i1 = element.elementIterator("type"); i1.hasNext();) {
      final Element typeElement = (Element) i1.next();
      result.setType(typeElement.getText());
    }

    //  version
    for (Iterator i1 = element.elementIterator("version"); i1.hasNext();) {
      final Element versionElement = (Element) i1.next();
      result.setVersion(new Version(versionElement.getText()));
    }

    // organisation
    for (Iterator i1 = element.elementIterator("organisation"); i1.hasNext();) {
      final Element organisationElement = (Element) i1.next();
      result.setOrganisation(organisationElement.getText());
    }

    // generator
    for (Iterator i1 = element.elementIterator("generator"); i1.hasNext();) {
      final Element generatorElement = (Element) i1.next();
      result.setGenerator(generatorElement.getText());
    }

    // first element
    for (Iterator i1 = element.elementIterator("firstelement"); i1.hasNext();) {
      final Element firstElement = (Element) i1.next();
      result.setRootElementName(firstElement.getText());
    }

    // annotations
    for (Iterator i1 = element.elementIterator("annotations"); i1.hasNext();) {
      final Element annotationsElement = (Element) i1.next();

      for (Iterator i2 = annotationsElement.elementIterator("annotation"); i2
          .hasNext();) {
        final Element annotationElement = (Element) i2.next();

        String name = null;
        String value = null;

        for (Iterator i3 = annotationElement.elementIterator("name"); i1
            .hasNext();) {
          final Element annotationNameElement = (Element) i3.next();
          name = annotationNameElement.getText();
        }

        for (Iterator i3 = annotationElement.elementIterator("value"); i1
            .hasNext();) {
          final Element annotationValueElement = (Element) i3.next();
          value = annotationValueElement.getText();
        }

        if (name != null && value != null)
          result.getAnnotations().setProperty(name, value);
      }
    }

    // elements
    for (Iterator i1 = element.elementIterator("elements"); i1.hasNext();) {
      final Element elementsElement1 = (Element) i1.next();

      for (Iterator i2 = elementsElement1.elementIterator("element"); i2
          .hasNext();) {
        final Element elementElement2 = (Element) i2.next();

        try {
          result.addElement(WorkflowElementDocument.parse(elementElement2));
        } catch (PlatformException e) {
          log.error("element is null or is already a part of a workflow");
        } catch (DocumentException e) {
          log.error("error while parsing a workflow xml document");
        }
      }

    }

    return result;
  }

}
