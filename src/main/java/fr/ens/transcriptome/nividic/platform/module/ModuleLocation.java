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

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.util.Version;

/**
   * Store information about how loading a module.
   *
   * @author Laurent Jourdren
   */
public final class ModuleLocation {

  /** Data module. */
  public static final int MODULE_TYPE_DATA = 1;
  /** Source algorithm module. */
  public static final int MODULE_TYPE_ALGORITHM = 2;

  private String name;
  private Version version;
  private boolean internal;
  private String className;
  private String classFile;
  private int type;
  private URL url;

  //
  // Getters
  //

  /**
   * Get the class name.
   * @return The class name
   */
  public String getClassName() {
    return className;
  }

  /**
   * Test if the class is internal.
   * @return true if the class is internal
   */
  public boolean isInternal() {
    return internal;
  }

  /**
   * Get the name of the class.
   * @return The name of the class
   */
  public String getName() {
    return name;
  }

  /**
   * Get the type of the module
   * @return the type of the module
   */
  public int getType() {
    return type;
  }

  /**
   * Get the file used to load the class.
   * @return The file used to load the file
   */
  public String getClassFile() {
    return classFile;
  }

  /**
   * Get the version of the module
   * @return The version of the module
   */
  public Version getVersion() {
    return version;
  }

  /**
   * Get the URL of the server to execute the module.
   * @return The URL of the module
   */
  public URL getUrl() {
    return url;
  }

  //
  // Setters
  //

  /**
   * Set the name of the class.
   * @param name the name of the class
   */
  private void setClassName(final String name) {
    className = name;
  }

  /**
   * Set if the class is internal.
   * @param internal is internal
   */
  private void setInternal(final boolean internal) {
    this.internal = internal;
  }

  /**
   * Set the name of the module.
   * @param name The name of the module
   */
  private void setName(final String name) {
    this.name = name;
  }

  /**
   * Set the type of the module.
   * @param type The type of the module
   * @throws PlatformException if the type is invalid
   */
  private void setType(final int type) throws PlatformException {
    if (type < MODULE_TYPE_DATA || type > MODULE_TYPE_ALGORITHM)
      throw new PlatformException("Invalid module type");
    this.type = type;
  }

  /**
   * Set the file used to load the class.
   * @param file The file used to load the class
   */
  private void setClassFile(final String file) {
    classFile = file;
  }

  /**
   * Set the version of the module.
   * @param version The version to set
   */
  public void setVersion(final Version version) {
    this.version = version;
  }

  /**
   * Set the URL of the server to execute the module.
   * @param url The URL of the module
   */
  public void setUrl(final URL url) {
    this.url = url;
  }

  //
  // Others methods
  //

  /**
   * Test if the module is local.
   * @return true if the module is local
   */
  public boolean isLocal() {
    return this.url == null;
  }

  //
  // Constructors
  //

  /**
   * Constructor for an internal module.
   * @param moduleName Module name
   * @param version the version of the module
   * @param type Module type
   * @param className Name of the main class of the module
   * @throws PlatformException if the type is wrong
   */
  public ModuleLocation(
    final String moduleName,
    final Version version,
    final int type,
    final String className)
    throws PlatformException {

    setName(moduleName);
    setVersion(version);
    setType(type);
    setClassName(className);
    setInternal(true);
  }

  /**
   * Constructor for an internal module.
   * @param moduleName Module name
   * @param version the version of the module
   * @param type Module type
   * @param className Name of the main class of the module
   * @param classFile The file used to load the class
   * @throws PlatformException if the type is wrong
   */
  ModuleLocation(
    final String moduleName,
    final Version version,
    final int type,
    final String className,
    final String classFile)
    throws PlatformException {

    setName(moduleName);
    setVersion(version);
    setType(type);
    setClassName(className);
    setClassFile(classFile);
    setInternal(false);
  }

}
