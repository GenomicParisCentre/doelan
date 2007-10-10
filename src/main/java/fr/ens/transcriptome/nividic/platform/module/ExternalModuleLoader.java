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

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.util.StringUtils;

/**
 * Load external modules.
 * @author Laurent Jourdren
 */
public class ExternalModuleLoader extends URLClassLoader implements
    ModuleLoader {

  //For logging system
  private static Logger log = Logger.getLogger(ExternalModuleLoader.class);

  private final ClassLoader cl = ExternalModuleLoader.class.getClassLoader();

  /**
   * Load a class.
   * @param className Class to load
   * @return The class Object of the className
   * @throws ClassNotFoundException if the className is not found
   */
  public Class loadClass(final String className) throws ClassNotFoundException {
    return this.cl.loadClass(className);
  }

  //
  // Other methods
  //

  /**
   * Find all the URLs of the libraries in the modules directories.
   * @param paths Paths where find the libraries
   * @return the URLs of all the jar/zip file in the paths
   */
  public static URL[] findLibraries(final URL[] paths) {

    URL[] result = new URL[0];

    if (paths == null)
      return result;

    Set libraries = new HashSet();

    for (int i = 0; i < paths.length; i++) {

      final String dirName = paths[i].getFile();
      final File dir = new File(dirName);

      if (dir.exists())
        if (dir.isDirectory()) {

          File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(final File f) {
              final String ext = StringUtils.getFileExtension(f.getName())
                  .toLowerCase();
              return (f.isFile() && (".jar".equals(ext) || ".zip".equals(ext)));
            }
          });

          for (int j = 0; j < files.length; j++)
            libraries.add(files[j]);

        } else {
          final String ext = StringUtils.getFileExtension(dir.getName())
              .toLowerCase();
          if (dir.isFile() && (".jar".equals(ext) || ".zip".equals(ext)))
            libraries.add(dir);

        }

    }

    result = new URL[libraries.size()];
    Iterator it = libraries.iterator();
    int k = 0;
    while (it.hasNext()) {
      try {
        final File path = (File) it.next();
        result[k++] = new URL("file:" + path.getAbsolutePath());
      } catch (MalformedURLException e) {
        log.error("CoreLibrariesClassLoader error, Malformed URL : "
            + e.getMessage());
      }
    }

    return result;
  }

  /**
   * Find all the URLs of the libraries in the modules directories.
   * @param paths Paths where find the libraries
   * @return the URLs of all the jar/zip file in the paths
   */
  public static URL[] getLibrariesPaths(final URL[] paths) {

    URL[] parentURLs = null;

    ClassLoader parentClassLoader = ExternalModuleLoader.class.getClassLoader();
    if (parentClassLoader instanceof URLClassLoader)

      parentURLs = ((URLClassLoader) parentClassLoader).getURLs();

    return concatURLs(parentURLs, findLibraries(paths));
  }

  /**
   * Concat 2 array of URLs.
   * @param urls1 First array of URL
   * @param urls2 Second array of URL
   * @return A concatened array of URLs
   */
  private static URL[] concatURLs(final URL[] urls1, final URL[] urls2) {

    if (urls1 == null && urls2 == null)
      return null;
    if (urls1 == null)
      return urls2;
    if (urls2 == null)
      return urls1;

    URL[] result = new URL[urls1.length + urls2.length];

    for (int i = 0; i < urls1.length; i++)
      result[i] = urls1[i];

    for (int i = 0; i < urls2.length; i++)
      result[urls1.length + i] = urls2[i];

    return result;
  }

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

    log.debug("load module class name : " + moduleReference.getClassName());

    Object o = null;
    try {
      o = loadClass(moduleReference.getClassName(), true).newInstance();
    } catch (InstantiationException e) {
      throw new PlatformException("Error while instantiation of a module");
    } catch (IllegalAccessException e) {
      throw new PlatformException("Illegal access to the module");
    } catch (ClassNotFoundException e) {
      throw new PlatformException("Module not found");
    }

    if (o instanceof Module)
      return (Module) o;

    throw new PlatformException("Not a module");

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param paths moduleDirectory Path to load modules and dependancies libraries
   *                 of the modules.
   */
  public ExternalModuleLoader(final URL[] paths) {
    //super(getLibrariesPaths(paths));
    super(findLibraries(paths));
  }

}