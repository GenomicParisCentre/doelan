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

package fr.ens.transcriptome.nividic.platform.module;

import java.util.Date;

import fr.ens.transcriptome.nividic.util.Version;

/**
 * This interface describe how access to the description of a module
 * @author Laurent Jourdren
 */
public interface AboutModule {

  /** Constant for state stable */
  int STATE_STABLE = 1;
  /** Constant for state testing */
  int STATE_TESTING = 2;
  /** Constant for state unstable */
  int STATE_UNSTABLE = 3;
  /** Constant for state experimental */
  int STATE_EXPERIMENTAL = 4;

  //
  // Getters
  //

  /**
   * Get the authors of the module.
   * @return The authors of the module
   */
  String[] getAuthors();

  /**
   * Get the dependancies of the module.
   * @return The dependancies of the module
   */
  ModuleDependancy[] getDependancies();

  /**
   * Get the email to contact authors of the module
   * @return The email to contact the authors of the module
   */
  String getEmail();

  /**
   * Get a long description of the module.
   * @return A long description of the module
   */
  String getLongDescription();

  /**
   * Get the minimal version of Nividic needed to run the module.
   * @return The minimal version of Nividic needed to run the module
   */
  Version getMinimalNividicVersion();

  /**
   * Get the module name.
   * @return The name of the module
   */
  String getName();

  /**
   * Get the name of the organisation of the authors of the module.
   * @return The name of the organisation of the authors of the module
   */
  String getOrganisation();

  /**
   * Get a short description of the module.
   * @return A short description of the module.
   */
  String getShortDescription();

  /**
   * Get the state of stability of the module.
   * @return the state of stability of the module
   */
  int getStability();

  /**
   * Get the version of the module.
   * @return The version of the module
   */
  Version getVersion();

  /**
   * Get the website of the module.
   * @return The website of the module
   */
  String getWebsite();

  /**
   * Get the licence of the module.
   * @return The licence of the module
   */
  String getLicence();

  /**
   * Get copyright of the module.
   * @return The copyright of the module
   */
  String getCopyright();

  /**
   * Get the date of the version of the module.
   * @return The date of the version of the module
   */
  Date getVersionDate();

  /**
   * Get the bibliography about the module.
   * @return The bibliography about the module
   */
  String[] getBibliography();

  /**
   * Get an HTLM documentation of the module.
   * @return an HTLM documentation of the module
   */
  String getHTMLDocumentation();

}
