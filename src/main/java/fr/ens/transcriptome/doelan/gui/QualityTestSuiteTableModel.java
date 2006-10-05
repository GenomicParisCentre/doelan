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

package fr.ens.transcriptome.doelan.gui;

import java.util.HashMap;
import java.util.Map;

import fr.ens.transcriptome.doelan.algorithms.DoelanConfigure;
import fr.ens.transcriptome.doelan.algorithms.QualityTest;
import fr.ens.transcriptome.doelan.algorithms.QualityUnitTest;
import fr.ens.transcriptome.doelan.data.QualityTestResult;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.AlgorithmEvent;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowElement;
import fr.ens.transcriptome.nividic.platform.workflow.gui.WorkflowTableAbstractModel;

/**
 * Table model for QualityUnitTest
 * @author Laurent Jourdren
 */
public class QualityTestSuiteTableModel extends WorkflowTableAbstractModel {

  private Map testsResults = new HashMap();
  private boolean configurePass;

  private final String[] columnNames = new String[] {"Test name",
      "Description", "Finished", "Passed"};

  private final Class[] columnClasses = new Class[] {String.class,
      String.class, Boolean.class, Boolean.class};

  //
  // Getters
  //

  /**
   * Get a result from an algorithm *
   * @param algorithm Algorithm of the result
   * @return The result of an algorithm
   */
  private QualityUnitTestResult getResult(final Algorithm algorithm) {
    return (QualityUnitTestResult) this.testsResults.get(algorithm);
  }

  /**
   * Get a result from an algorithm *
   * @param element Workflow element of the result
   * @return The result of an algorithm
   */
  private QualityTestResult getResult(final WorkflowElement element) {
    if (element == null)
      return null;
    return (QualityTestResult) this.testsResults.get(element.getAlgorithm());
  }

  //
  // Setters
  //

  /**
   * Set a result from an algorithm
   * @param algorithm Algorithm of the result
   * @param result to set
   */
  private void setResult(final Algorithm algorithm,
      final QualityTestResult result) {

    if (algorithm == null || result == null)
      return;
    this.testsResults.put(algorithm, result);
  }

  //
  // Methods from TableAbstractModel
  //

  /**
   * Get the name of a column.
   * @param col number of the column
   * @return the name of the column
   */
  public String getColumnName(final int col) {
    return columnNames[col];
  }

  /**
   * Get the class of a column.
   * @param col number of the column
   * @return the class of the column
   */
  public Class getColumnClass(final int col) {
    return columnClasses[col];
  }

  /**
   * Get the number of column.
   * @return the column count
   */
  public int getColumnCount() {
    return columnNames.length;
  }

  /**
   * Get a value.
   * @param rowIndex index of the row
   * @param columnIndex index of the column
   * @return An object with the value
   */
  public Object getValueAt(final int rowIndex, final int columnIndex) {

    final WorkflowElement wfe = getWorkflowElementAt(rowIndex);
    Algorithm algo = null;

    if (wfe != null)
      algo = wfe.getAlgorithm();

    switch (columnIndex) {
    case 0:
      if (wfe == null)
        return null;
      return "" + wfe.getId();

    case 1:

      if (algo == null || !(algo instanceof Module))
        return null;
      // return ((Module) algo).aboutModule().getName();
      return ((Module) algo).aboutModule().getShortDescription();

    case 2:

      if (algo == null || algo instanceof DoelanConfigure)
        return new Boolean(this.configurePass);

      QualityTestResult result1 = getResult(wfe);
      return new Boolean(result1 != null);

    case 3:

      if (algo == null || algo instanceof DoelanConfigure)
        return new Boolean(this.configurePass);

      QualityTestResult result2 = getResult(wfe);
      return new Boolean(result2 == null ? false : result2.getResult());

    default:
      return null;
    }
  }

  /**
   * Get the tip
   * @param row the row number
   * @return The tip about the test
   */
  public String getTip(final int row) {

    final WorkflowElement wfe = getWorkflowElementAt(row);
    final Algorithm algo = wfe.getAlgorithm();

    if (algo instanceof Module)

      return "Description : "
          + ((Module) algo).aboutModule().getShortDescription();

    return null;
  }

  /**
   * Returns true if the cell at rowIndex and columnIndex is editable.
   * Otherwise, setValueAt on the cell will not change the value of that cell.
   * @param rowIndex the row whose value to be queried
   * @param columnIndex the column whose value to be queried
   * @return true if the cell is editable
   */
  public boolean isCellEditable(final int rowIndex, final int columnIndex) {

    return false;
  }

  /**
   * Sets the value in the cell at columnIndex and rowIndex to aValue.
   * @param aValue the new value
   * @param rowIndex the row whose value is to be changed
   * @param columnIndex the column whose value is to be changed
   */
  public void setValueAt(final Object aValue, final int rowIndex,
      final int columnIndex) {

    if (columnIndex != 0)
      return;

    final WorkflowElement wfe = getWorkflowElementAt(rowIndex);
    final QualityUnitTest test = (QualityUnitTest) wfe.getAlgorithm();

    if (aValue == null || !(aValue instanceof String))
      return;

  }

  //
  // Methods from WorkflowTableAbstactModel
  //

  /**
   * Test if an element is a showable Workflow element.
   * @param element Element to test
   * @return true if the element to test is showable Workflow element
   */
  public boolean filterWorkflowElement(final WorkflowElement element) {

    if (element == null)
      return false;
    final Algorithm algo = element.getAlgorithm();
    if (algo == null)
      return false;

    if (algo instanceof QualityTest)
      return ((QualityTest) algo).isShowable();

    return false;
  }

  /**
   * Test if an algorithm is a showable quality test.
   * @param about Information about the algorithm
   * @param algorithm Algorithm to test
   * @return true if the algorithm to test is showable
   */
  public boolean filterModule(final AboutModule about,
      final Algorithm algorithm, final boolean local) {

    if (!local || algorithm == null)
      return false;

    if (algorithm instanceof QualityTest)
      return ((QualityTest) algorithm).isShowable();

    return false;
  }

  //
  // Listeners
  //

  /**
   * Handle algorithm events
   * @param e Algorithm event
   */
  public void algorithmStateChanged(final AlgorithmEvent e) {

    if (e == null)
      return;

    if (e.getId() == QualityTest.RESULT_EVENT
        || e.getId() == QualityTest.CONFIGURE_TEST_EVENT) {

      final QualityTest qut = (QualityTest) e.getSource();
      final int pos = getRowAlgorithm(qut);

      if (e.getId() == QualityTest.RESULT_EVENT)
        setResult(qut, (QualityTestResult) e.getObjectValue());
      else
        this.configurePass = true;

      fireTableCellUpdated(pos, 3);
      fireTableCellUpdated(pos, 2);
    }

  }

  /**
   * Throws an execption to a listener.
   * @param e Exception to throw.
   */
  public void workflowNewException(final PlatformException e) {
  }

  //
  // Other methods
  //

  /**
   * Clear results.
   */
  public void clearResults() {
    this.testsResults.clear();
  }

  //
  // Constructor
  //

  public QualityTestSuiteTableModel() {
    super();
  }

}