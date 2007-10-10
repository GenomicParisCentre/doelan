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

import java.io.File;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * Wrapper for gal bioassay.
 * @author Laurent Jourdren
 */
public class ArrayListFileData extends Data implements Module {

  // For logging system
  private static Logger log = Logger.getLogger(BioAssayFileData.class);

  /**
   * Get the name of the data.
   * @return The name of the data
   */
  public String getName() {
    return SystemUtils.getClassNameWithoutPackage(ArrayListFileData.class);
  }

  /**
   * Get the format of the data.
   * @return The name of the format of the data
   */
  public String getFormat() {
    return DataDefaults.FILE_FORMAT;
  }

  /**
   * Get the type of the data.
   * @return The type of the data.
   */
  public String getType() {
    return DataDefaults.BIOASSAY_TYPE;
  }

  /**
   * Get the class of the data.
   * @return The class of the data.
   */
  public Class getDataClass() {
    return BioAssay.class;
  }

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("ArrayListFileData", "BioAssay GAL data type");
    } catch (PlatformException e) {
      log.error("Unable to create module description");
    }
    return md;
  }

  /**
   * Set the data.
   * @param arrayList The array list to set
   * @throws PlatformException if data type is bad or if data is null
   */
  public void setData(final File arrayList) throws PlatformException {
    super.setData(arrayList);
  }

  //
  // Constructor
  //

  /**
   * Constructor.
   * @param arrayList The array list to set
   * @throws PlatformException if data type is bad or if data is null
   */
  public ArrayListFileData(final File arrayList) throws PlatformException {
    setData(arrayList);
  }

}