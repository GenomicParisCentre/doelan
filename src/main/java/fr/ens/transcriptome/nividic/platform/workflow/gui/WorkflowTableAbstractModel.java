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

package fr.ens.transcriptome.nividic.platform.workflow.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleLocation;
import fr.ens.transcriptome.nividic.platform.module.ModuleManager;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.AlgorithmEvent;
import fr.ens.transcriptome.nividic.platform.workflow.AlgorithmListener;
import fr.ens.transcriptome.nividic.platform.workflow.Workflow;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowElement;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowEvent;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowGraph;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowListener;
import fr.ens.transcriptome.nividic.util.NividicUtils;

/**
 * Abstract class for Workflow table model.
 * @author Laurent Jourdren
 */
public abstract class WorkflowTableAbstractModel extends AbstractTableModel
    implements AlgorithmListener, WorkflowListener {

  private Workflow workflow;
  private WorkflowElement[] displayElements;
  private ModuleLocation[] availableModules;
  private Map<Algorithm, Integer> mapIdRow = new HashMap<Algorithm, Integer>();

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

  /**
   * Get available modules.
   * @return Returns the availableModules
   */
  public ModuleLocation[] getAvailableModules() {

    return availableModules.clone();
  }

  /**
   * Get elements to display.
   * @return Returns the displayElements
   */
  public WorkflowElement[] getDisplayElements() {
    return displayElements.clone();
  }

  /**
   * Get the workflow element of a row
   * @param row the row being queried
   * @return The workflow element of the row
   */
  public WorkflowElement getWorkflowElementAt(final int row) {
    if (row < 0 || row > getRowCount())
      return null;
    return this.displayElements[row];
  }

  /**
   * Get the algorithm of a row
   * @param row the row being queried
   * @return The algorithm of the row
   */
  public Algorithm getAlgorithmAt(final int row) {
    final WorkflowElement wfe = getWorkflowElementAt(row);
    if (wfe == null)
      return null;
    return wfe.getAlgorithm();
  }

  /**
   * Get the row of an algorithm.
   * @param algorithm Algorithm
   * @return The row of the algorithm
   */
  public int getRowAlgorithm(final Algorithm algorithm) {

    if (algorithm == null)
      return -1;
    Object o = this.mapIdRow.get(algorithm);
    if (o == null)
      return -1;

    return ((Integer) o).intValue();
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

    // Add and remove listeners
    if (this.workflow != null)
      this.workflow.removeListener(this);
    if (workflow != null)
      workflow.addListener(this);

    this.workflow = workflow;
    refreshWorkflowElements();

  }

  //
  // Methods from AbstractTableModel
  //

  /**
   * Get the row count of the table.
   * @return The number of the row of the table
   */
  public int getRowCount() {
    return this.displayElements == null ? 0 : this.displayElements.length;
  }

  //
  // Other methods
  //

  /**
   * Filter a workflow element.
   * @param element Workflow element to filter
   * @return true if the workflow element must be selected
   */
  public abstract boolean filterWorkflowElement(final WorkflowElement element);

  /**
   * Filter a module.
   * @param about Description of the module to filter
   * @param algorithm Instance of the Module to filter
   * @param local true if the module is local
   * @return true if the module must be selected
   */
  public abstract boolean filterModule(final AboutModule about,
      final Algorithm algorithm, final boolean local);

  /**
   * Refresh the list of available algorithms
   */
  public final void refreshModules() {

    ModuleLocation[] locations = ModuleManager.getManager().getListModules();

    if (locations == null)
      return;

    List<ModuleLocation> modulesToAdd = new ArrayList<ModuleLocation>();

    for (int i = 0; i < locations.length; i++) {

      if (locations[i].getType() != ModuleLocation.MODULE_TYPE_ALGORITHM)
        continue;
      Module module = ModuleManager.getManager().loadModule(locations[i]);

      if (module == null)
        continue;
      Algorithm algo = null;

      try {
        algo = (Algorithm) module;
      } catch (ClassCastException e) {
        NividicUtils.nop();
      }

      if (algo != null
          && filterModule(module.aboutModule(), algo, locations[i].isLocal()))
        modulesToAdd.add(locations[i]);

    }
    this.availableModules = new ModuleLocation[modulesToAdd.size()];
    for (int i = 0; i < modulesToAdd.size(); i++)
      this.availableModules[i] = modulesToAdd.get(i);
  }

  /**
   * Refresh the list of the workflowElements.
   */
  public final void refreshWorkflowElements() {

    if (this.workflow == null)
      return;

    WorkflowElement[] elements = new WorkflowGraph(getWorkflow())
        .getElementsInsideTheGraph();

    if (elements == null)
      return;

    List<WorkflowElement> display = new ArrayList<WorkflowElement>(
        elements.length);

    for (int i = 0; i < elements.length; i++)
      if (filterWorkflowElement(elements[i]))
        display.add(elements[i]);

    this.displayElements = new WorkflowElement[display.size()];
    this.mapIdRow.clear();

    for (int i = 0; i < display.size(); i++) {
      final WorkflowElement wfe = display.get(i);
      this.displayElements[i] = wfe;

      final Algorithm algo = wfe.getAlgorithm();
      if (algo != null) {
        algo.addListener(this);
        this.mapIdRow.put(algo, i);
      }

    }
    fireTableDataChanged();
  }

  //
  // Listeners
  //

  /**
   * Algorithm listener
   * @param e Event
   */
  public void algorithmStateChanged(final AlgorithmEvent e) {
  }

  /**
   * Algorithm listener
   * @param e Event
   */
  public void workflowStateChanged(final WorkflowEvent e) {
    refreshWorkflowElements();
  }

  //
  // Constructors
  //

  /**
   * Public constructor.
   */
  public WorkflowTableAbstractModel() {
    refreshModules();
  }

}