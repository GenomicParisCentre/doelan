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

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.workflow.Workflow;

/**
 * This interface defines generics methods to read and write workflows.
 * @author Laurent Jourdren
 */
public interface WorkflowIO {

  /**
   * Write a workflow.
   * @param workflow Workflow to write
   * @throws PlatformException if an error occurs while reading data
   */
  void write(Workflow workflow) throws PlatformException;

  /**
   * Read a workflow.
   * @return A new Workflow object
   * @throws PlatformException if an error occurs while wrinting data
   */
  Workflow read() throws PlatformException;

}