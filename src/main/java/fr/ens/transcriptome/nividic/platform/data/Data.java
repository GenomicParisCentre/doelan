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

package fr.ens.transcriptome.nividic.platform.data;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.Module;

/**
 * A wrapper for data used in workflows.
 * @author Laurent Jourdren
 */
public abstract class Data implements Module {

  private static int count;

  private int instanceId = count++;

  private Object data;

  private static boolean wellFormedNividic;

  //
  // Getters
  //

  /**
   * Get the name of the data.
   * @return The name of the data
   */
  public abstract String getName();

  /**
   * Get the data.
   * @return The data
   */
  public Object getData() {
    return data;
  }

  /**
   * Get the format of the data.
   * @return The format of the data
   */
  public abstract String getFormat();

  /**
   * Get the identifier of the data
   * @return The identifier of the data
   */
  public int getInstanceId() {
    return instanceId;
  }

  /**
   * Get the type of the data.
   * @return The type of the data.
   */
  public abstract String getType();

  /**
   * Get the class of the data.
   * @return The class of the data.
   */
  public abstract Class getDataClass();

  //
  // Setters
  //

  /**
   * Set the data.
   * @param data The data
   */
  protected void setData(final Object data) throws PlatformException {

    if (data == null)
      throw new PlatformException("Data is null");

    if (!wellFormedNividic) {
      if (getDataClass() == null)
        throw new PlatformException("A data must have a class type");
      if (!(getDataClass().isAssignableFrom(data.getClass())))
        throw new PlatformException("Invalid data class type");

      /*
       * boolean module = false; Class[] interfaces =
       * this.getClass().getInterfaces(); for (int i = 0; i < interfaces.length;
       * i++) if (Module.class.equals(interfaces[i])) { module = true; break; }
       * if (!module) throw new PlatformException("Data is not a module");
       * wellFormedNividic = true; }
       */

      if (!(Module.class.isAssignableFrom(getClass())))
        throw new PlatformException("Data is not a module");

      if (data == null)
        throw new PlatformException("A data can't be null");

      this.data = data;
    }

  }
  //
  // Other methods
  //

}