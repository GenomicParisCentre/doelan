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

import fr.ens.transcriptome.nividic.util.event.GenericEvent;

/**
 * Interface for Workflows events.
 * @author Laurent Jourdren
 */
public interface WorkflowEvent extends GenericEvent {

  /** Workflow add event. */
  int ADD_EVENT = 1;

  /** Workflow remove event. */
  int REMOVE_EVENT = 2;

  /** Workflow link event. */
  int LINK_EVENT = 3;

  /** Workflow unlink event. */
  int UNLINK_EVENT = 4;

  /** Workflow start event. */
  int START_EVENT = 5;

  /** Workflow end event. */
  int END_EVENT = 6;

  /** Workflow stop event. */
  int STOP_EVENT = 7;

  /** Workflow pause event. */
  int PAUSE_EVENT = 8;

  /** Workflow resume event. */
  int RESUME_EVENT = 9;

  /** Workfow start an algorithm event. */
  int START_ALGORITHM_EVENT = 10;

  /** End of an alogorithm of Workfow event. */
  int END_ALGORITHM_EVENT = 11;

  /** Workflow elements stop event. */
  int ELEMENTS_STOP_EVENT = 12;

  /** Workflow elements pause event. */
  int ELEMENTS_PAUSE_EVENT = 13;

  /** Workflow elements resume event. */
  int ELEMENTS_RESUME_EVENT = 14;

  /**
   * Get the source of the event.
   * @return Returns the source
   */
  Workflow getSource();

}