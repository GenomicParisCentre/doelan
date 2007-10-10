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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.data.Data;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.util.StringUtils;

/**
 * This class find the nividic plateform module
 * @author Laurent jourdren
 */
public class ModuleFinder {

  // For logging system
  private static Logger log = Logger.getLogger(ModuleFinder.class);

  private FindModuleClassLoader loader = new FindModuleClassLoader();
  private Set moduleList = new HashSet();

  /**
   * Classloader used to load class from .class file or from jar ou zip files
   * for discovery of modules.
   * @author Laurent Jourdren
   */
  private final static class FindModuleClassLoader extends ClassLoader {

    private static final long MAX_FILE_LENGTH = 100000;
    private final ClassLoader cl = FindModuleClassLoader.class.getClassLoader();

    /**
     * Load a class.
     * @param className Class to load
     * @return The class Object of the className
     * @throws ClassNotFoundException if the className is not found
     */
    public Class loadClass(final String className)
        throws ClassNotFoundException {
      return this.cl.loadClass(className);
    }

    /**
     * Load a class from a file.
     * @param file Class file to load
     * @return A class object
     */
    public Class loadClass(final File file) {
      byte[] b = loadClassData(file);

      // if error or class size too large
      if (b == null)
        return null;

      return defineClass(null, b, 0, b.length);
    }

    /**
     * Load a class from a file.
     * @param zipfile The zipfile to use
     * @param entry entry to load
     */
    public Class loadClass(final ZipFile zipfile, final ZipEntry entry) {
      byte[] b = loadClassData(zipfile, entry);

      // if error or class size too large
      if (b == null)
        return null;

      return defineClass(null, b, 0, b.length);
    }

    /**
     * Load data from a file
     * @param file The file to load
     * @return an array of bytes
     */
    private byte[] loadClassData(final File file) {

      if (file == null)
        return null;

      final long size = file.length();

      if (size > MAX_FILE_LENGTH)
        return null;

      byte[] result = new byte[(int) size];

      FileInputStream fis;
      try {
        fis = new FileInputStream(file);
        fis.read(result);
        fis.close();
      } catch (FileNotFoundException e) {
        log.error("Error, class file not found : " + file.getAbsolutePath());
      } catch (IOException e) {
        log.error("Error while reading a class file : "
            + file.getAbsolutePath());
      }

      return result;
    }

    /**
     * Load data from a zipfile
     * @param zipfile The zipfile to use
     * @param entry entry to load
     * @return an array of bytes
     */
    private byte[] loadClassData(final ZipFile zipfile, final ZipEntry entry) {

      if (zipfile == null || entry == null)
        return null;

      final long size = entry.getSize();

      if (size > MAX_FILE_LENGTH)
        return null;

      byte[] result = new byte[(int) size];

      InputStream is;
      try {
        is = zipfile.getInputStream(entry);

        int count = 0;
        int read;

        // TODO optimize it
        while ((read = is.read()) != -1)
          result[count++] = (byte) read;

      } catch (IOException e) {
        log.error("Error while reading a file in a zip file : "
            + entry.getName() + " in " + zipfile.getName());
      }

      return result;
    }

  }

  /**
   * Find internal modules.
   * @param classes to test if are modules
   */
  public void findInternalModules(final Class[] classes) {

    for (int i = 0; i < classes.length; i++) {
      Class c = classes[i];
      ModuleLocation mr = testClassAndGetModuleReference(c, null);
      if (mr != null)
        addModule(mr);
    }
  }

  /**
   * Test if a class is a child of another class.
   * @param parent The parent class
   * @param child The child class
   * @return true if a class is a child of another class
   */
  private static boolean isChildOf(final Class parent, final Class child) {

    if (parent == null || child == null)
      return false;

    Class t = child;
    while (t != null) {
      if (parent.equals(t))
        return true;
      t = t.getSuperclass();
    }
    return false;
  }

  /**
   * Get the type of a module.
   * @param module Module to test
   * @return the type of the module or -1 if an error occurs
   */
  public static final int getModuleType(final Module module) {

    if (module == null)
      return -1;

    if (isChildOf(Data.class, module.getClass()))
      return ModuleLocation.MODULE_TYPE_DATA;
    else if (isChildOf(Algorithm.class, module.getClass()))
      return ModuleLocation.MODULE_TYPE_ALGORITHM;

    return -1;
  }

  /**
   * Test if a class is a module.
   * @param c The class to test
   * @param path Path used to load the module. null if module is an internal
   *                 module.
   * @return true if the class is a module
   */
  private ModuleLocation testClassAndGetModuleReference(final Class c,
      final String path) {

    if (c == null)
      return null;

    Class[] interfaces = c.getInterfaces();
    final String moduleClassName = Module.class.getName();

    for (int i = 0; i < interfaces.length; i++) {

      //if (Module.class.equals(interfaces[i])) {
      if (moduleClassName.equals(interfaces[i].getName())) {

        Module m = null;
        try {
          Object o = c.newInstance();
          if (!(o instanceof Module))
            continue;

          m = (Module) o;

          AboutModule am = m.aboutModule();
          int type = getModuleType(m);
          if (type == -1)
            return null;

          if (path == null)
            return new ModuleLocation(am.getName(), am.getVersion(), type, m
                .getClass().getName());

          return new ModuleLocation(am.getName(), am.getVersion(), type, m
              .getClass().getName(), path);

        } catch (InstantiationException e) {
          log.error("Instantation error : " + c.getName());
        } catch (IllegalAccessException e) {
          log.error("Illegal acces error : " + c.getName());
        } catch (PlatformException e) {
          log.error("Nividic error : " + c.getName());
        }

        break;
      }
    }
    return null;
  }

  /**
   * Find modules in the classpath.
   */
  public void findExternalModulesInClassPath() {

    String classpath = System.getProperty("java.class.path");
    String[] dirs = classpath.split("\\" + File.pathSeparatorChar);
    if (dirs == null)
      return;
    for (int i = 0; i < dirs.length; i++)
      findExternalModulesInPath(new File(dirs[i]));
  }

  /**
   * Find module in a path.
   * @param path Path to search
   */
  public void findExternalModulesInPath(final File path) {

    if (path == null)
      return;

    final String ext = StringUtils.getFileExtension(path.getName())
        .toLowerCase();

    if (path.isDirectory()) {
      File[] files = path.listFiles(new FileFilter() {
        public boolean accept(final File f) {
          final String ext = StringUtils.getFileExtension(f.getName())
              .toLowerCase();

          return (".jar".equals(ext) || ".zip".equals(ext));
        }
      });

      // Recursive call
      for (int i = 0; i < files.length; i++)
        findExternalModulesInPath(files[i]);

    } else if (".jar".equals(ext) || ".zip".equals(ext))
      findModulesInZip(path);
  }

  /*
   * private void findModuleInFile(final File file) { if (file == null) return;
   * Class c = null; try { c = this.loader.loadClass(file); } catch
   * (IllegalAccessError e) { log.error(&quot;Illegal access to a file while
   * searching module : &quot; + file.getAbsolutePath()); } if (c == null)
   * return; ModuleLocation mr = testClassAndGetModuleReference(c, file
   * .getAbsolutePath()); if (mr != null) addModule(mr); }
   */

  /**
   * This class discover module inside a jar or zip file.
   * @param file File to inspect
   */
  private void findModulesInZip(final File file) {

    if (file == null)
      return;

    log.debug("Find module in jar/zip file : " + file);

    try {
      JarFile jarfile = new JarFile(file);
      Enumeration e = jarfile.entries();
      while (e.hasMoreElements()) {
        ZipEntry entry = (ZipEntry) e.nextElement();
        if (!entry.isDirectory()
            && StringUtils.getFileExtension(entry.getName()).toLowerCase()
                .equals(".class")) {

          Class c = null;
          try {

            c = this.loader.loadClass(jarfile, entry);
          } catch (IllegalAccessError err) {
            log.error("Illegal access error : " + err.getMessage() + "["
                + entry.getName() + "]");
          } catch (ClassFormatError err) {
            log.error("Class format error : " + err.getMessage() + "["
                + entry.getName() + "]");
          } catch (NoClassDefFoundError err) {
            log.error("No class def found : " + err.getMessage() + "["
                + entry.getName() + "]");
          }

          if (c != null) {

            ModuleLocation mr = testClassAndGetModuleReference(c, file
                .getAbsolutePath());
            if (mr != null) {
              log.debug("find external module : " + mr.getName());
              addModule(mr);
            }
          }
        }
      }
    } catch (ZipException e) {
      log.error("Error while reading zip file : " + file.getName());
    } catch (IOException e) {
      log.error("Error while reading zip file : " + file.getName());
    }

  }

  /**
   * Add a module to the list.
   * @param module the module to add.
   */
  private void addModule(final ModuleLocation module) {

    this.moduleList.add(module);
  }

  /**
   * Get the modules found.
   * @return The modules found
   */
  public ModuleLocation[] getModules() {

    final int size = this.moduleList.size();

    if (size == 0)
      return null;

    ModuleLocation[] result = new ModuleLocation[size];

    Iterator it = this.moduleList.iterator();
    int i = 0;
    while (it.hasNext())
      result[i++] = (ModuleLocation) it.next();

    return result;
  }

}