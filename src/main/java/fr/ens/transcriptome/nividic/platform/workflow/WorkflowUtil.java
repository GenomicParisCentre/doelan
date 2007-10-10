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

import fr.ens.transcriptome.nividic.platform.PlatformException;

/**
 * Utility class for workflows.
 * @author Laurent Jourdren
 */
public final class WorkflowUtil {

  /**
   * Merge 2 workflows.
   * @param a First workflow to merge
   * @param b Second workflow to merge
   * @param wfe Workflow element of workflow a used to link workflow b
   * @return Workflow a with element of workflow b linked. Workflow b is empty.
   * @throws PlatformException if an error occurs while merge the 2 workflows
   */
  public static Workflow merge(final Workflow a, final Workflow b,
      final WorkflowElement wfe) throws PlatformException {

    if (a == null)
      return null;
    if (b == null || wfe == null || wfe.getWorkflow() != a)
      return a;

    String[] names = b.getElementsNames();
    for (int i = 0; i < names.length; i++) {
      WorkflowElement e = b.getElement(names[i]);

      try {
        b.removeElement(names[i]);
        e.setWorkflow(a);
        a.addElement(e);
      } catch (PlatformException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

      a.link(wfe, a.getElement(b.getRootElementName()));
      b.setRootElementName(null);
    }

    return a;
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private WorkflowUtil() {
  }

}