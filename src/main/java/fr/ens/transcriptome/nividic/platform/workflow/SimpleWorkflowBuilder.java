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

package fr.ens.transcriptome.nividic.platform.workflow;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.ModuleQuery;

/**
 * This class provides an easy maner to build simples workflows
 * @author Laurent Jourdren
 */
public class SimpleWorkflowBuilder {

  //For log system
  private static Logger log = Logger.getLogger(SimpleWorkflowBuilder.class);

  private Workflow workflow;
  private WorkflowElement lastElement;

  //
  // Getters
  //

  /**
   * Get the workflow
   * @return the workflow
   */
  public Workflow getWorkflow() {
    return workflow;
  }

  //
  // Other methods
  //

  /**
   * Add an element to the workflow.
   * @param moduleName Name of the module to add
   * @return The SimpleWorkflowBuilder object
   */
  public SimpleWorkflowBuilder addElement(final String moduleName) {
    ModuleQuery query = new ModuleQuery(moduleName);
    addElement(query, null);
    return this;
  }

  /**
   * Add an element to the workflow.
   * @param moduleName Name of the module to add
   * @param id Identifier of the element
   * @return The SimpleWorkflowBuilder object
   */
  public SimpleWorkflowBuilder addElement(final String moduleName,
      final String id) {
    ModuleQuery query = new ModuleQuery(moduleName);
    addElement(query, id);
    return this;
  }

  /**
   * Add an element to the workflow.
   * @param query Query of the module to add
   * @return The SimpleWorkflowBuilder object
   */
  public SimpleWorkflowBuilder addElement(final ModuleQuery query) {
    addElement(query, null);
    return this;
  }

  /**
   * Add an element to the workflow.
   * @param query Query of the module to add
   * @param id Identifier of the element
   * @return The SimpleWorkflowBuilder object
   */
  public SimpleWorkflowBuilder addElement(final ModuleQuery query,
      final String id) {

    if (query == null || query.getModuleName() == null
        || query.getModuleName().equals(""))
      return this;

    WorkflowElement wfe;

    if (id == null)
      wfe = new WorkflowElement();
    else
      wfe = new WorkflowElement(id);

    wfe.setModuleQuery(query);

    try {
      this.workflow.addElement(wfe);
    } catch (PlatformException e) {
      log.error("Not the same workflow : " + e.getMessage());
    }

    if (lastElement != null)
      try {
        this.workflow.link(lastElement, wfe);
      } catch (PlatformException e) {
        log.error("Not the same workflow : " + e.getMessage());
      }
    else
      this.workflow.setRootElementName(wfe.getId());

    lastElement = wfe;
    return this;
  }

  //
  // Constructors
  //

  /**
   * Public constructor.
   */
  public SimpleWorkflowBuilder() {
    this.workflow = new Workflow();
  }

  /**
   * Public constructor.
   * @param name Name of the workflow
   */
  public SimpleWorkflowBuilder(final String name) {
    this.workflow = new Workflow(name);
  }

}