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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.util.Version;

/**
 * This class start the discovery of the modules and the loading of the modules.
 * @author Laurent Jourdren
 */
public final class ModuleManager {

  // For logging system
  private static Logger log = Logger.getLogger(ModuleManager.class);

  /** the unique instance of the module manager. */
  private static ModuleManager module;

  private Map moduleIndex = new HashMap();
  private Set moduleList = new HashSet();
  private ModuleLoader internalLoader = new InternalModuleLoader();
  private ModuleLoader externalLoader;
  private ModuleFinder moduleFinder = new ModuleFinder();
  private Set externalModulesURLs = new HashSet();
  private Set internalModulesClasses = new HashSet();

  //
  // Static methods
  //

  /**
   * Get the manager.
   * @return The manager.
   */
  public static ModuleManager getManager() {
    if (module == null)
      module = new ModuleManager();

    return module;
  }

  //
  // Other methods
  //

  /**
   * Create the module index.
   * @param modules Modules to put in the index
   */
  private void createModuleIndex(final ModuleLocation[] modules) {

    if (modules == null)
      return;

    for (int i = 0; i < modules.length; i++) {

      final ModuleLocation m = modules[i];
      final String name = m.getName();
      final String version = m.getVersion().toString();

      if (!moduleIndex.containsKey(name))
        moduleIndex.put(name, new HashMap());

      Map map = (Map) this.moduleIndex.get(name);

      if (map.containsKey(version))
        log.warn("Modules have the same version :" + name);
      else {
        map.put(version, m);
        this.moduleList.add(m);

        StringBuffer sb = new StringBuffer();
        if (m.isInternal())
          sb.append("Add internal module : ");
        else
          sb.append("Add external module : ");
        sb.append(name + " (version " + version + ")");
        log.info(sb.toString());
      }
    }

  }

  /**
   * Get a module location.
   * @param name Name of the module to get
   * @return A module location if module exists
   */
  public ModuleLocation getModuleLocation(final String name) {
    return getModuleLocation(new ModuleQuery(name));
  }

  /**
   * Get a module location.
   * @param query Module query
   * @return A module location if module exists
   */
  public ModuleLocation getModuleLocation(final ModuleQuery query) {

    if (query == null)
      return null;

    if (query.getModuleLocation() != null
        && this.moduleList.contains(query.getModuleLocation()))
      return query.getModuleLocation();

    if (query.getModuleName() == null)
      return null;

    Map nomVersions = (Map) this.moduleIndex.get(query.getModuleName());
    if (nomVersions == null || nomVersions.size() == 0)
      return null;

    Version[] versions = new Version[nomVersions.size()];
    Iterator it = nomVersions.keySet().iterator();
    int i = 0;
    while (it.hasNext())
      versions[i++] = new Version((String) it.next());

    Version toLoad = null;

    // Test if the recommended version exists
    if (query.getRecommendedVersion() != null)
      for (int j = 0; j < versions.length; j++) {
        if (query.getRecommendedVersion().equals(versions[j])) {
          toLoad = versions[j];
          break;
        }
      }

    // Get the latest version
    if (toLoad == null) {
      toLoad = Version.getMaximalVersion(versions);
      if (query.getMinimalVersion() != null
          && query.getMinimalVersion().compareTo(toLoad) > 0)
        toLoad = null;
    }

    if (toLoad == null)
      return null;

    ModuleLocation mr = (ModuleLocation) nomVersions.get(toLoad.toString());
    if (query.getType() == -1 || query.getType() == mr.getType())
      return mr;

    return null;
  }

  /**
   * Load a module
   * @param moduleReference Reference of the module to load.
   * @return The module if exists
   */
  public Module loadModule(final ModuleLocation moduleReference) {

    if (moduleReference == null)
      return null;

    try {
      if (moduleReference.isInternal())
        return this.internalLoader.loadModule(moduleReference);
      else if (externalLoader != null)
        return this.externalLoader.loadModule(moduleReference);
    } catch (PlatformException e) {
      log.error("Error while loading module : " + moduleReference.getName()
          + " (" + e.getMessage() + ")");
    }

    return null;
  }

  /**
   * Find the modules and set the classpaths to load it.
   */
  public void findModules() {

    // Create an array of internal classes
    Class[] internalClasses = new Class[this.internalModulesClasses.size()];
    this.internalModulesClasses.toArray(internalClasses);

    /*
     * int i = 0; Iterator it = this.internalModulesClasses.iterator(); while
     * (it.hasNext()) internalClasses[i++] = (Class) it.next();
     */

    // find modules in this internal classes
    this.moduleFinder.findInternalModules(internalClasses);

    //  Create an array of external modules path
    URL[] paths = new URL[this.externalModulesURLs.size()];
    this.externalModulesURLs.toArray(paths);

    /*
     * i = 0; it = this.externalModulesURLs.iterator(); while (it.hasNext())
     * paths[i++] = (URL) it.next();
     */

    // find modules in this paths
    for (int j = 0; j < paths.length; j++)
      this.moduleFinder.findExternalModulesInPath(new File(paths[j].getFile()));

    // Set the loader for the external modules
    this.externalLoader = new ExternalModuleLoader(paths);

    // Create the index
    createModuleIndex(this.moduleFinder.getModules());

  }

  /**
   * Add a path to the list of module directories.
   * @param directory Directory to add.
   */
  public void addPath(final String directory) {

    if (directory == null)
      return;
    final File d = new File(directory);

    if (d.exists() && d.isDirectory())
      try {
        this.externalModulesURLs.add(new URL("file:" + directory));
      } catch (MalformedURLException e) {
        log.error("Malformed URL : " + e.getMessage());
      }
  }

  /**
   * Add some paths to the list of module directories.
   * @param directories Directories to add.
   */
  public void addPaths(final String[] directories) {

    if (directories == null)
      return;
    for (int i = 0; i < directories.length; i++)
      addPath(directories[i]);
  }

  /**
   * Add a class to the list of internal module.
   * @param c The class to add
   */
  public void addInternalModule(final Class c) {

    if (c == null)
      return;

    this.internalModulesClasses.add(c);
  }

  /**
   * Add classes to the list of internal module.
   * @param classes The classes to add
   */
  public void addInternalModules(final Class[] classes) {

    if (classes == null)
      return;

    for (int i = 0; i < classes.length; i++)
      addInternalModule(classes[i]);
  }

  /**
   * Get the list of the modules.
   * @return A array of moduleReference
   */
  public ModuleLocation[] getListModules() {

    if (this.moduleList.size() == 0)
      return null;
    ModuleLocation[] result = new ModuleLocation[this.moduleList.size()];

    this.moduleList.toArray(result);

    return result;
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private ModuleManager() {
  }

}