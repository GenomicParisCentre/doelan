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

package fr.ens.transcriptome.doelan.algorithms;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;

/**
 * This class is the super class of QualityUnitTest and QualityGlobalTest.
 * @author Laurent Jourdren
 */
public abstract class QualityTest extends Algorithm implements Module {

  /** Identifier for result event. */
  public static final int RESULT_EVENT = 1000;

  /** Identifier for result event. */
  public static final int GLOBAL_TEST_INIT_EVENT = 1001;

  /** Identifier for result event. */
  public static final int CONFIGURE_TEST_EVENT = 1002;

  /**
   * Test if the test is deletable().
   * @return true if the test is deletable
   */
  public abstract boolean isDeletable();

  /**
   * Test if the test is modifiable.
   * @return true if the test is modifiable
   */
  public abstract boolean isModifiable();

  /**
   * Test if the test could be showed.
   * @return true if the test could be showed
   */
  public abstract boolean isShowable();

  /**
   * Test if the test could be diplayed in the list of tests to add.
   * @return true if the test could be showed
   */
  public abstract boolean isAddable();

  /**
   * Test if only one instance of the test could be created.
   * @return true if only one instance of the test could be created
   */
  public abstract boolean isUniqueInstance();

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *                 <b>null </b>.
   */
  public QualityTest() throws PlatformException {
    // MUST BE EMPTY
  }

}
