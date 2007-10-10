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

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.data.BioAssayFileData;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.util.parameter.FixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.ParameterBuilder;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This element set in memory references to gpr files.
 * @author Laurent Jourdren
 */
public class LoadGPRNamesElement extends Algorithm implements Module {

  // For logging system
  private static Logger log = Logger.getLogger(LoadGPRNamesElement.class);

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("LoadGPRNames", "Load GPR names");
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

    FixedParameters sp = new FixedParameters();
    try {
      sp
          .addParameter(new ParameterBuilder()
              .withName("files")
              .withDescription(
                  "GPR files to load."
                      + " Tabulation character is the separator character between files")
              //.withType(Parameter.DATATYPE_STRING)
              .getParameter());
    } catch (ParameterException e) {
      log.error("Error while setting the parameter of the module");
    }

    return sp;
  }

  protected void doIt(final Container c, final Parameters parameters)
      throws PlatformException {

    // Get the strings of the files
    String value = null;

    try {
      value = parameters.getParameter("files").getStringValue();
    } catch (ParameterException e) {
      log.error("Wrong parameter");
    }

    if (value == null)
      return;

    // Add a BioAssaGPR in the container of each file
    String[] files = value.split("\t");
    for (int i = 0; i < files.length; i++) {

      BioAssayFileData bag = new BioAssayFileData(new File(files[i]));

      c.add(bag);
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
  public LoadGPRNamesElement() throws PlatformException {
    // MUST BE EMPTY
  }

}
