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

import java.util.ArrayList;

/**
 * This class allow to get all the workflow element which are inside or outside
 * the workflow graph.
 * @author Laurent Jourdren
 */
public final class WorkflowGraph {

  private Workflow workflow;
  private ArrayList inGraph = new ArrayList();
  private ArrayList endElements = new ArrayList();

  //
  // Getters
  //

  /**
   * Get the workflow.
   * @return Returns the workflow
   */
  public Workflow getWorkflow() {
    return workflow;
  }

  //
  // Setters
  //

  /**
   * Set the workflow.
   * @param workflow The workflow to set
   */
  public void setWorkflow(final Workflow workflow) {
    if (this.workflow == workflow)
      return;
    this.workflow = workflow;
  }

  //
  // Other methods
  //

  private void tree(final WorkflowElement we) {

    if (we == null)
      return;
    if (this.inGraph.contains(we))
      return;

    this.inGraph.add(we);
    WorkflowElement[] nexts = we.getNextElements();

    if (nexts.length == 0)
      this.endElements.add(we);

    for (int i = 0; i < nexts.length; i++)
      tree(nexts[i]);
  }

  private void tree() {

    this.inGraph.clear();
    this.endElements.clear();
    tree(getWorkflow().getRootElement());
  }

  /**
   * Get all the workflow element in the workflowGraph.
   * @return An array of WorkflowElement which contains all the element in the
   *               graph.
   */
  public WorkflowElement[] getElementsInsideTheGraph() {

    if (getWorkflow() == null)
      return null;
    tree();

    WorkflowElement[] result = new WorkflowElement[this.inGraph.size()];

    for (int i = 0; i < this.inGraph.size(); i++)
      result[i] = (WorkflowElement) this.inGraph.get(i);

    return result;
  }

  /**
   * Get all the workflow elements at the end(s) of the workflowGraph.
   * @return An array of WorkflowElement which contains all the element at the
   *               end of the graph.
   */
  public WorkflowElement[] getEndElementsOfTheGraph() {

    if (getWorkflow() == null)
      return null;
    tree();

    WorkflowElement[] result = new WorkflowElement[this.endElements.size()];

    for (int i = 0; i < this.endElements.size(); i++)
      result[i] = (WorkflowElement) this.endElements.get(i);

    return result;
  }

  /**
   * Get all the workflow element outside the workflowGraph.
   * @return An array of WorkflowElement which contains all the element outside
   *               the graph.
   */
  public WorkflowElement[] getElementsOutsideTheGraph() {

    if (getWorkflow() == null)
      return null;
    tree();

    WorkflowElement[] elements = getWorkflow().getElements();

    ArrayList out = new ArrayList();

    for (int i = 0; i < elements.length; i++)
      if (!this.inGraph.contains(elements[i]))
        out.add(elements[i]);

    WorkflowElement[] result = new WorkflowElement[out.size()];
    for (int i = 0; i < out.size(); i++)
      result[i] = (WorkflowElement) out.get(i);

    return result;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public WorkflowGraph() {
  }

  /**
   * Public constructor.
   * @param workflow Workflow to set.
   */
  public WorkflowGraph(final Workflow workflow) {
    setWorkflow(workflow);
  }

}