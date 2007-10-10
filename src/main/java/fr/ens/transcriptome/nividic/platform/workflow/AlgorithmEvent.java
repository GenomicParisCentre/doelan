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
 * Interface for Algorithm event.
 * @author Laurent Jourdren
 */
public interface AlgorithmEvent extends GenericEvent {

  /** Algorithm start event. */
  int START_EVENT = 1;

  /** Algorithm end event. */
  int END_EVENT = 2;

  /** Algorithm progress event. */
  int PROGRESS_EVENT = 3;

  /** Algorithm warning event. */
  int WARNING_EVENT = 4;

  /** No more algorithm to execute. */
  int NO_MORE_ALGORITHM_TO_EXECUTE = 5;

  /**
   * Get the source of the event.
   * @return Returns the source
   */
  Algorithm getSource();

}