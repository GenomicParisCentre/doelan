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

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * Wrapper for BioAssay Objects.
 * @author Laurent Jourdren
 */
public final class BioAssayOMData extends Data {

  // For logging system
  private static Logger log = Logger.getLogger(BioAssayOMData.class);

  /**
   * Get the name of the data.
   * @return The name of the data
   */
  public String getName() {
    return SystemUtils.getClassNameWithoutPackage(BioAssayOMData.class);
  }

  /**
   * Get the format of the data.
   * @return The name of the format of the data
   */
  public String getFormat() {
    return DataDefaults.OM_FORMAT;
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
   * Set the data.
   * @param bioAssay File to set
   * @throws PlatformException if data type is bad or if data is null
   */
  public void setData(final BioAssay bioAssay) throws PlatformException {
    try {
      super.setData(bioAssay);
    } catch (PlatformException e) {
      log.error("Cast exception");
    }
  }

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("BioAssayOMData",
          "BioAssay OM data type");
    } catch (PlatformException e) {
      log.error("Unable to create module description");
    }
    return md;
  }

  /**
   * Constructor.
   * @param bioAssay BioAssay to set
   * @throws PlatformException if data type is bad or if data is null
   */
  public BioAssayOMData(final BioAssay bioAssay) throws PlatformException {
    setData(bioAssay);
  }

}