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

package fr.ens.transcriptome.doelan.io;

import fr.ens.transcriptome.doelan.DoelanException;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteList;

/**
 * This interface defines generics methods to read and write test suites
 * parameters.
 *
 * @author Laurent Jourdren
 */
public interface QualityTestSuiteListIO {

  /**
   * Write a test suite
   * @param list Test suite list to write
   * @throws DoelanException if an error occurs while reading data
   */
  void write(QualityTestSuiteList list) throws DoelanException;

  /**
   * Read a test suite list.
   * @return A new QualityTestSuiteList object
   * @throws DoelanException if an error occurs while wrinting data
   */
  QualityTestSuiteList read() throws DoelanException;

}
