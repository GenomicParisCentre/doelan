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

/**
 * This class load an internal Module
 * @author Laurent Jourdren
 */
public class InternalModuleLoader extends ClassLoader implements ModuleLoader {

  //
  // Implements ModuleLoader
  //

  /**
   * Load a module.
   * @param moduleReference Module to load
   * @return A module
   * @throws PlatformException if an error occurs while loading the module
   */
  public Module loadModule(final ModuleLocation moduleReference)
      throws PlatformException {

    Object o = null;
    try {

      // Use the classloader used to load this class
      Class c = InternalModuleLoader.class.getClassLoader().loadClass(
          moduleReference.getClassName());

      if (c == null)
        throw new PlatformException("Module not found");

      o = c.newInstance();
    } catch (InstantiationException e) {
      throw new PlatformException("Error while instantiation of a module");
    } catch (IllegalAccessException e) {
      throw new PlatformException("Illegal access to the module");
    } catch (ClassNotFoundException e) {
      throw new PlatformException("Module not found");
    }

    if (o instanceof Module)
      return (Module) o;

    // If not a module
    throw new PlatformException("Not a module");

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public InternalModuleLoader() {
    super();
  }

}