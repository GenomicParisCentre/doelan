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

import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.om.BioAssay;
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
 * This element compute ratio of a BioAssay Object.
 * @author Laurent Jourdren
 */
public class ComputeRatiosElement extends Algorithm implements Module {

  // For log system
  private static Logger log = Logger.getLogger(ComputeRatiosElement.class);

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("ComputeRatios", "Compute ratios for BioAssay");
    } catch (PlatformException e) {
      log.error("Unable to create the module description");
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

    final Container work = c.filterFormat(DataDefaults.OM_FORMAT).filterType(
        DataDefaults.BIOASSAY_TYPE);

    final Iterator it = work.iterator();
    while (it.hasNext()) {
      Data rd = (Data) it.next();
      BioAssay b = (BioAssay) rd.getData();

      int size = b.size();
      double[] ratios = new double[size];
      int[] reds = b.getReds();
      int[] greens = b.getGreens();

      if (greens == null)
        throw new PlatformException("Greens data is null");
      if (reds == null)
        throw new PlatformException("Reds data is null");

      for (int i = 0; i < size; i++)
        ratios[i] = (0.0 + greens[i]) / (0.0 + reds[i]);

      b.setRatios(ratios);

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
  public ComputeRatiosElement() throws PlatformException {
    // MUST BE EMPTY
  }

}