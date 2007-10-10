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

package fr.ens.transcriptome.nividic.platform.algorithms;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.io.BioAssayWriter;
import fr.ens.transcriptome.nividic.om.io.GPRWriter;
import fr.ens.transcriptome.nividic.om.io.NividicIOException;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.data.Data;
import fr.ens.transcriptome.nividic.platform.data.DataDefaults;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * Write a BioAssay object on disk.
 * @author Laurent Jourdren
 */
public class WriteBioAssayOMToGPRFileElement extends Algorithm implements Module {

  // For logging system
  private static Logger log = Logger
      .getLogger(WriteBioAssayOMToGPRFileElement.class);

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("WriteRomdeOMToGPRFile",
          "Write BioAssay Data to GPR Files");
    } catch (PlatformException e) {
      log.error("Unable to create the description of the module");
    }
    return md;
  }

  /**
   * Defines the parameters of the element.
   * @return the parameters of the element
   */
  protected Parameters defineParameters() {
    return null;
  }

  protected void doIt(final Container c, final Parameters parameters)
      throws PlatformException {

    log.debug(this);

    Container work = c.filterFormat(DataDefaults.BIOASSAY_TYPE).filterType(
        DataDefaults.OM_FORMAT);

    Iterator it = work.iterator();
    while (it.hasNext()) {
      Data rd = (Data) it.next();
      BioAssay b = (BioAssay) rd.getData();

      try {
        FileOutputStream os;

        try {
          os = new FileOutputStream("/tmp/" + rd.getInstanceId() + ".gpr");
          BioAssayWriter baw = new GPRWriter(os);
          baw.write(b);
        } catch (FileNotFoundException e) {
          throw new PlatformException("Cannot find the GPR file");
        }

      } catch (NividicIOException e) {
        throw new PlatformException("Error while writing the GPR file : "
            + e.getMessage());
      }
    }

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *                 <b>null </b>.
   */
  public WriteBioAssayOMToGPRFileElement() throws PlatformException {
    // MUST BE EMPTY
  }

}