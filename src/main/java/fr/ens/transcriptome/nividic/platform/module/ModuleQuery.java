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

import java.net.URL;

import fr.ens.transcriptome.nividic.util.Version;

/**
 * Define a module query.
 * @author Laurent Jourdren
 */
public class ModuleQuery {

  private String moduleName;
  private URL url;
  private Version version;
  private Version recommendedVersion;
  private Version minimalVersion;
  private ModuleLocation moduleLocation;
  private int type = -1;

  //
  // Getters
  //

  /**
   * Get the URL of the module.
   * @return The URL of the module
   */
  public URL getURL() {
    return url;
  }

  /**
   * Get the mininal version of the element.
   * @return The minimal version of the element
   */
  public Version getMinimalVersion() {
    return minimalVersion;
  }

  /**
   * Get the module name.
   * @return The module name.
   */
  public String getModuleName() {
    return moduleName;
  }

  /**
   * Get the recommended version of the module.
   * @return The recommended version of the module
   */
  public Version getRecommendedVersion() {
    return recommendedVersion;
  }

  /**
   * Get the version of the module.
   * @return The version of the module
   */
  public Version getVersion() {
    return version;
  }

  /**
   * Get the type of the module to search
   * @return Get the type of the module
   */
  public int getType() {
    return type;
  }

  /**
   * Get the module location.
   * @return Returns the moduleLocation
   */
  public ModuleLocation getModuleLocation() {
    return moduleLocation;
  }

  //
  // Setters
  //

  /**
   * Set the URL of the module
   * @param url The URL of the module
   */
  public void setURL(final URL url) {
    this.url = url;
  }

  /**
   * Set the minimal version of the module.
   * @param version The minmal version of the module
   */
  public void setMinimalVersion(final Version version) {
    minimalVersion = version;
  }

  /**
   * Set the module name.
   * @param name The module name
   */
  public void setModuleName(final String name) {
    moduleName = name;
  }

  /**
   * Set the recommended version of the module.
   * @param version The version to set
   */
  public void setRecommendedVersion(final Version version) {
    recommendedVersion = version;
  }

  /**
   * Set the module version needed
   * @param version The module version neeeded
   */
  public void setVersion(final Version version) {
    this.version = version;
  }

  /**
   * Set the type of the module to search
   * @param type The type of the module to search
   */
  public void setType(final int type) {
    this.type = type;
  }

  /**
   * Set the module location.
   * @param moduleLocation The moduleLocation to set
   */
  public void setModuleLocation(final ModuleLocation moduleLocation) {
    this.moduleLocation = moduleLocation;
    if (moduleLocation != null)
      setModuleName(moduleLocation.getName());
  }

  //
  // Constructor
  //

  /**
   * Public constructor
   */
  public ModuleQuery() {
  }

  /**
   * Constructor, set the module name of the query.
   * @param moduleName The name of the module for the query
   */
  public ModuleQuery(final String moduleName) {
    setModuleName(moduleName);
  }

  /**
   * Constructor, set the module name of the query.
   * @param moduleLocation The module location for the query
   */
  public ModuleQuery(final ModuleLocation moduleLocation) {
    setModuleLocation(moduleLocation);
  }

}