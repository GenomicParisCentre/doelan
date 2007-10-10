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

package fr.ens.transcriptome.nividic.platform.cli;

import fr.ens.transcriptome.nividic.platform.module.ModuleLocation;
import fr.ens.transcriptome.nividic.platform.module.ModuleManager;

/**
 * This class handle CLI option about modules
 *
 * @author Laurent Jourdren
 */
public final class ModuleCLI {

  /**
   * Show the list of available modules.
   */
  public static void listModules() {

    ModuleManager mm = ModuleManager.getManager();

    ModuleLocation[] mrs = mm.getListModules();

    if (mrs == null || mrs.length == 0)
      System.out.println("No modules found.");

    System.out.println("Internal/External\tType\tVersion\tName");
    for (int i = 0; i < mrs.length; i++) {
      ModuleLocation mr = mrs[i];

      StringBuffer sb = new StringBuffer();

      if (mr.isInternal())
        sb.append("Internal\t");
      else
        sb.append("External\t");

      switch (mr.getType()) {
        case ModuleLocation.MODULE_TYPE_DATA :
          sb.append("Data\t");
          break;

        case ModuleLocation.MODULE_TYPE_ALGORITHM :
          sb.append("Algorithm\t");
          break;

        default :
          break;
      }

      System.out.println(mr.getVersion().toString() + "\t" + mr.getName());
    }

  }

  /**
   * Show information about a module.
   * @param moduleName Name of the module to show information
   */
  public static void showModule(final String moduleName) {
    System.out.println("show module : " + moduleName);
  }

  /**
   * Private constructor.
   */
  private ModuleCLI() {
  }

}
