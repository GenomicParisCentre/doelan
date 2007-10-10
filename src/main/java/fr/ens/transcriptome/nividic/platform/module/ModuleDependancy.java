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

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.util.Version;

/**
 * Describe an depedancy for a module.
 *
 * @author Laurent Jourdren
 */
public class ModuleDependancy {

  private String moduleName;
  private Version minimalVersion = new Version();
  private Version recommendedVersion = new Version();

  //
  // Getters
  //

  /**
   * Get the minimal version need by the dependancy.
   * @return The minimal version need by the dependandy
   */
  public Version getMinimalVersion() {
    return minimalVersion;
  }

  /**
   * Get the name of the module of the dependancy
   * @return The name of the module of the dependancy
   */
  public String getModuleName() {
    return moduleName;
  }

  /**
   * Get the minimal version need by the dependancy.
   * @return The minimal version need by the dependandy
   */
  public Version getRecommendedVersion() {
    return recommendedVersion;
  }

  //
  // Setters
  //

  /**
   * Set the minimal version need by the dependancy.
   * @param version The version to set
   * @throws PlatformException if the version is null
   */
  public void setMinimalVersion(final Version version) throws PlatformException {

    if (version == null)
      throw new PlatformException("Minimal version of dependancy is null");
    minimalVersion = version;
  }

  /**
   * Set the module name of the dependancy.
   * @param name The name of the dependancy
   * @throws PlatformException if the name is null
   */
  public void setModuleName(final String name) throws PlatformException {

    if (name == null)
      throw new PlatformException();
    moduleName = name;
  }

  /**
   * Set the recommended version need by the dependancy.
   * @param version The version to set
   * @throws PlatformException if the version is null
   */
  public void setRecommendedVersion(final Version version) throws PlatformException {

    if (version == null)
      throw new PlatformException("Minimal version of dependancy is null");
    recommendedVersion = version;
  }

  //
  // Constructors
  //

  /**
   * Public Constructor.
   * @param moduleName Name of the dependancy
   * @throws PlatformException if the version is null
   */
  public ModuleDependancy(final String moduleName) throws PlatformException {
    setModuleName(moduleName);
  }

  /**
     * Public Constructor.
     * @param moduleName Name of the dependancy
     * @param minimalVersion Minimal version of the dependancy
     * @throws PlatformException if the version is null or if the version is null
     */
  public ModuleDependancy(final String moduleName, final Version minimalVersion)
    throws PlatformException {

    this(moduleName);
    setMinimalVersion(minimalVersion);
  }

  /**
   * Public Constructor.
   * @param moduleName Name of the dependancy
   * @param minimalVersion Minimal version of the dependancy
   * @param recommendedVersion Recommended version of the dependancy
   * @throws PlatformException if the version is null or if the version is null
   */
  public ModuleDependancy(
    final String moduleName,
    final Version minimalVersion,
    final Version recommendedVersion)
    throws PlatformException {

    this(moduleName, minimalVersion);
    setRecommendedVersion(recommendedVersion);
  }

}
