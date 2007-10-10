/*
 *                Doelan development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public Licence.  This should
 * be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/gpl.txt
 *
 * Copyright (c) 2004-2005 ENS Microarray Platform
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 * For more information on the Doelan project and its aims,
 * or to join the Doelan mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/doelan
 */

package fr.ens.transcriptome.doelan.algorithms;

import java.applet.Applet;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import fr.ens.transcriptome.doelan.data.TestSuiteResult;
import fr.ens.transcriptome.doelan.data.TestSuiteResultData;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.BioAssayFactory;
import fr.ens.transcriptome.nividic.om.GenepixResults;
import fr.ens.transcriptome.nividic.om.io.InputStreamBioAssayReader;
import fr.ens.transcriptome.nividic.om.io.GPRReader;
import fr.ens.transcriptome.nividic.om.io.ImaGeneArrayListReader;
import fr.ens.transcriptome.nividic.om.io.ImaGeneOutputFileReader;
import fr.ens.transcriptome.nividic.om.io.NividicIOException;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.data.ArrayListOMData;
import fr.ens.transcriptome.nividic.platform.data.BioAssayOMData;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.util.parameter.FixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterBuilder;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class implements a module witch load GPR and GAL file.
 * @author Laurent Jourdren
 */
public class DoelanLoadGenepixData extends Algorithm implements Module {

  /** the applet Object to load data from the GPR. */
  private Applet applet;

  //
  // Getters
  //

  /**
   * Get the applet.
   * @return Returns the applet.
   */
  public Applet getApplet() {
    return applet;
  }

  //
  // Setters
  //

  /**
   * Set the applet.
   * @param applet The applet to set
   */
  public void setApplet(final Applet applet) {
    this.applet = applet;
  }

  //
  // Others methods
  //

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("DoelanLoadData", "Load GPR and GAL files");
      md.setStability(AboutModule.STATE_STABLE);
    } catch (PlatformException e) {
      getLogger().error("Unable to create the module description");
    }
    return md;
  }

  /**
   * Set the parameters of the element.
   * @return The defaults parameters to set.
   */
  protected Parameters defineParameters() {

    try {

      final Parameter chipTypeName = new ParameterBuilder().withName(
          "chipTypeName").withDescription("Name of the chip type")
          .getParameter();

      final Parameter testSuiteName = new ParameterBuilder().withName(
          "testSuiteName").withDescription("Name of the test suite name")
          .getParameter();

      final Parameter gprFile = new ParameterBuilder().withType(
          Parameter.DATATYPE_ARRAY_STRING).withName("gprFilenames")
          .withDescription("The path to the GPR file").getParameter();

      final Parameter galFile = new ParameterBuilder().withName("galFilename")
          .withDescription("The path to the GAL file").getParameter();

      final Parameter description = new ParameterBuilder().withName(
          "description").withDescription("Data description").getParameter();

      final Parameter loadFromApplet = new ParameterBuilder().withName(
          "loadFromApplet").withType(Parameter.DATATYPE_BOOLEAN)
          .withDescription("Flag to load data from applet").getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(chipTypeName);
      params.addParameter(testSuiteName);
      params.addParameter(gprFile);
      params.addParameter(galFile);
      params.addParameter(description);
      params.addParameter(loadFromApplet);

      return params;

    } catch (ParameterException e) {
      getLogger().error("Error while creating parameters : " + e);
    }

    return null;
  }

  /**
   * This method contains all the code to manipulate the container <b>c </b> in
   * this element.
   * @param c The container to be manipulated
   * @param parameters Parameters of the elements
   * @throws PlatformException if an error occurs while executing the test.
   */
  protected void doIt(final Container c, final Parameters parameters)
      throws PlatformException {

    try {

      String galFilename = null;
      BioAssay gpr = null;
      BioAssay gal = null;
      String chipTypeName = parameters.getParameter("chipTypeName")
          .getStringValue();
      String testSuiteName = parameters.getParameter("testSuiteName")
          .getStringValue();
      String description = parameters.getParameter("description")
          .getStringValue();

      if (parameters.getParameter("loadFromApplet").getBooleanValue()) {
        if (getApplet() == null)
          throw new PlatformException("No Applet");
        gpr = BioAssayFactory.createBioAssay(getApplet());

        galFilename = new GenepixResults(gpr).getGALFile();
      } else {

        gpr = loadResultFile(parameters.getParameter("gprFilenames")
            .getArrayStringValues());
        galFilename = parameters.getParameter("galFilename").getStringValue();
      }

      if (galFilename != null && new File(galFilename).exists())
        gal = loadArrayListFile(galFilename);

      if (gpr != null) {
        BioAssayOMData baro = new BioAssayOMData(gpr);
        c.add(baro);
      }
      if (gal != null) {
        ArrayListOMData alro = new ArrayListOMData(gal);
        c.add(alro);
      }

      TestSuiteResult testSuiteResult = new TestSuiteResult(chipTypeName,
          testSuiteName, description);

      c.add(new TestSuiteResultData(testSuiteResult));

    } catch (ParameterException e) {
      throw new PlatformException("Parameter error : " + e.getMessage());
    }

  }

  /**
   * Load a ATF file.
   * @param filename File to load
   * @return A BioAssay Object
   */
  private BioAssay loadATFFile(final String filename) {

    BioAssay b = null;

    try {
      InputStreamBioAssayReader bar = new GPRReader(new FileInputStream(filename));
      bar.addAllFieldsToRead();

      b = bar.read();
    } catch (FileNotFoundException e) {
      getLogger().error("File not found : " + filename);
    } catch (NividicIOException e) {
      getLogger().error("Can't load ATF file : " + filename);
    }

    return b;
  }

  /**
   * Load an Imagene result file.
   * @param filenameRed File to load
   * @param filenameGreen File to load
   * @return A BioAssay Object
   */
  private BioAssay loadImageneResultFile(final String filenameRed,
      final String filenameGreen) {

    BioAssay b = null;

    try {

      InputStream is1 = new FileInputStream(filenameRed);
      InputStream is2 = new FileInputStream(filenameGreen);

      ImaGeneOutputFileReader igofr = new ImaGeneOutputFileReader(is1, is2);
      igofr.addAllFieldsToRead();
      
      b = igofr.read();

    } catch (FileNotFoundException e) {
      getLogger().error("File not found. ");
    } catch (NividicIOException e) {
      getLogger().error("Can't load ATF file. ");
    }

    return b;
  }

  /**
   * Find the type of the result file and load the file
   * @param filenames Filenames of the files to load
   * @return A BioAssay object
   */
  private BioAssay loadResultFile(final String[] filenames) {

    if (filenames == null || filenames.length > 2 || filenames[0] == null)
      return null;

    if (filenames.length == 1)
      return loadATFFile(filenames[0]);
    if (filenames[1] == null)
      return null;

    String filenameRed = null;
    String filenameGreen = null;

    if (filenames[0].trim().endsWith("_1.txt"))
      filenameRed = filenames[0];
    else if (filenames[1].trim().endsWith("_1.txt"))
      filenameRed = filenames[1];

    if (filenames[0].trim().endsWith("_2.txt"))
      filenameGreen = filenames[0];
    else if (filenames[1].trim().endsWith("_2.txt"))
      filenameGreen = filenames[1];

    if (filenameGreen == null || filenameRed == null)
      return null;

    return loadImageneResultFile(filenameRed, filenameGreen);
  }

  /**
   * Load an Imagene Gene ID file.
   * @param filename File to load
   * @return A BioAssay Object
   */
  private BioAssay loadImageneGeneID(final String filename) {

    BioAssay ba = null;

    try {

      InputStream is = new FileInputStream(filename);
      ImaGeneArrayListReader igalr = new ImaGeneArrayListReader(is);

      ba = igalr.read();

    } catch (FileNotFoundException e) {
      getLogger().error("File not found. ");
    } catch (NividicIOException e) {
      getLogger().error("Can't load ATF file. ");
    }

    return ba;
  }

  /**
   * Find the type of the array list file and load the file
   * @param filename Filename of the file to load
   * @return A BioAssay object
   */
  private BioAssay loadArrayListFile(final String filename) {

    if (filename == null)
      return null;

    if (filename.trim().endsWith(".gal"))
      return loadATFFile(filename);

    return loadImageneGeneID(filename);

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *           <b>null </b>.
   */
  public DoelanLoadGenepixData() throws PlatformException {
    // MUST BE EMPTY
  }

}