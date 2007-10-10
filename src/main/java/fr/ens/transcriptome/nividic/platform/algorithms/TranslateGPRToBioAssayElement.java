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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.io.InputStreamBioAssayReader;
import fr.ens.transcriptome.nividic.om.io.GPRReader;
import fr.ens.transcriptome.nividic.om.io.NividicIOException;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.data.BioAssayOMData;
import fr.ens.transcriptome.nividic.platform.data.Data;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * this element transform a gpr file reference to a BioAssay Object.
 *
 * @author Laurent Jourdren
 */
public class TranslateGPRToBioAssayElement extends Algorithm implements Module {

  // For logging system
  private static Logger log = Logger.getLogger(TranslateGPRToBioAssayElement.class);

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("TranslateGPRToBioAssay", "Translate GPR to BioAssay");
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

    Container work = c.filterFormat("BioAssay").filterType("GPR");

    Iterator it = work.iterator();
    while (it.hasNext()) {
      Data rd = (Data) it.next();
      File f = (File) rd.getData();

      BioAssay b = null;

      try {

        try {
          InputStreamBioAssayReader baio = new GPRReader(new FileInputStream(f));

          b = baio.read();
        } catch (FileNotFoundException e) {
          throw new PlatformException("Error while translate a GPR file : " + e.getMessage());
        }
        if (b != null) {
          // Remove the GPR data from container
          c.remove(rd);
          // and replace it by a BioAssay
          c.add(new BioAssayOMData(b));
        }

      } catch (NividicIOException e) {
        throw new PlatformException("Error while translate a GPR file : " + e.getMessage());
      }
    }
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is <b>null</b>.
   */
  public TranslateGPRToBioAssayElement() throws PlatformException {
    // MUST BE EMPTY
  }

}
