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

import java.awt.Color;
import java.awt.Image;
import java.util.Arrays;
import java.util.Iterator;

import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.data.DoelanDataUtils;
import fr.ens.transcriptome.doelan.data.QualityTestResult;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
import fr.ens.transcriptome.doelan.data.TestSuiteResult;
import fr.ens.transcriptome.nividic.NividicRuntimeException;
import fr.ens.transcriptome.nividic.om.ArrayBlock;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.BioAssayFactory;
import fr.ens.transcriptome.nividic.om.BioAssayUtils;
import fr.ens.transcriptome.nividic.om.GenepixArrayList;
import fr.ens.transcriptome.nividic.om.ImaGeneArrayList;
import fr.ens.transcriptome.nividic.om.SpotIterator;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.util.graphics.ArrayPlot;
import fr.ens.transcriptome.nividic.util.graphics.DefaultColor;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This algorithm execute all the global tests and compute the ouput array list.
 * @author Laurent Jourdren
 */
public class DoelanExecuteGlobalTests extends Algorithm implements Module {

  // For log system
  // private static Logger log =
  // Logger.getLogger(DoelanExecuteGlobalTests.class);

  //
  // Other methods
  //

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {
    ModuleDescription md = null;
    try {
      md = new ModuleDescription("DoelanExecuteGlobalTests",
          "Execute global tests");
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

    // Execute all the global tests
    TestSuiteResult testSuiteResult = DoelanDataUtils.getTestSuiteResult(c);
    executeGlobalTest(c, testSuiteResult);

    // Generate the output Array list
    BioAssay arrayList = DoelanDataUtils.getArrayList(c);

    if (arrayList != null) {

      BioAssay bioAssay = DoelanDataUtils.getBioAssay(c);

      QualityUnitTestResult[] unitResults = DoelanDataUtils
          .getQualityUnitTestResults(c);

      BioAssay newArrayList = reFlag(arrayList, bioAssay, unitResults,
          testSuiteResult.getSpotRejectedId());

      // Set the arrayplot image
      Image arrayplot = getArrayPlot(createArrayListForArrayPlot(arrayList,
          bioAssay, unitResults));

      if (arrayplot != null)
        testSuiteResult.setArrayPlot(arrayplot);

      testSuiteResult.setNewArrayList(newArrayList);

    }
  }

  /**
   * Execute all the global tests
   * @param c The container to be manipulated
   * @param testSuiteResult The result object of the test suite
   * @throws PlatformException if an error occurs while executing the tests
   */
  private void executeGlobalTest(final Container c,
      final TestSuiteResult testSuiteResult) throws PlatformException {

    if (testSuiteResult == null)
      return;

    Iterator it = testSuiteResult.getGlobalTests().iterator();
    while (it.hasNext()) {
      TestSuiteResult.GlobalTest gt = (TestSuiteResult.GlobalTest) it.next();
      gt.executeTest(c);
    }

    computeTestSuiteFinalResult(c);
  }

  /**
   * Compute the final result of the test suite.
   * @param c The container to be manipulated
   */
  private void computeTestSuiteFinalResult(final Container c) {

    TestSuiteResult globalResult = DoelanDataUtils.getTestSuiteResult(c);
    QualityTestResult[] results = DoelanDataUtils.getQualityTestResults(c);

    boolean r = true;
    for (int i = 0; i < results.length; i++)
      if (!results[i].isError())
        r = r && results[i].getResult();

    globalResult.setResult(r);
  }

  private boolean[] compileResults(
      final QualityUnitTestResult[] qualityUnitTestresults, final boolean filter) {

    if (qualityUnitTestresults == null)
      return null;

    boolean[] result = null;

    for (int i = 0; i < qualityUnitTestresults.length; i++) {
      QualityUnitTestResult r = qualityUnitTestresults[i];

      if (r.isError())
        continue;

      final boolean[] flags = r.getNewFlags();

      if (!filter || r.isFilterFlags()) {

        if (result == null)
          result = (boolean[]) flags.clone();
        else
          for (int j = 0; j < flags.length; j++)
            result[j] = result[j] & flags[j];
      }
    }

    return result;
  }

  private BioAssay createArrayListForArrayPlot(final BioAssay gal,
      final BioAssay gpr, final QualityUnitTestResult[] result) {

    if (gal == null || gpr == null || result == null || result.length == 0)
      return null;

    if (!BioAssayUtils.containLocations(gpr, gal))
      return null;

    // Create new Gal object
    BioAssay bioAssayArrayPlot = BioAssayFactory.createBioAssay();
    bioAssayArrayPlot.getAnnotation().addProperties(gal.getAnnotation());
    bioAssayArrayPlot.setSpotEmptyTester(gpr.getSpotEmptyTester());

    bioAssayArrayPlot.setIds(gal.getIds());

    if (gal.isDescriptions())
      bioAssayArrayPlot.setDescriptions(gal.getDescriptions());
    bioAssayArrayPlot.setLocations(gal.getLocations());

    // Compile the results
    boolean[] globalResult = compileResults(result, false);
    boolean[] globalResultRequest = compileResults(result, true);

    if (globalResultRequest == null)
      globalResultRequest = new boolean[gpr.size()];

    // Set the color of the spots
    int[] colors = new int[globalResult.length];
    for (int i = 0; i < colors.length; i++)
      if (globalResult[i])
        colors[i] = DefaultColor.LIGHT_GREEN.getRGB();
      else if (globalResultRequest[i])
        colors[i] = Color.WHITE.getRGB();
      else
        colors[i] = DefaultColor.LIGHT_RED.getRGB();

    int[] colorsToSet = new int[gal.size()];

    // Copy the color on the output gal
    SpotIterator si2 = gpr.iterator();
    int i = 0;
    while (si2.hasNext()) {

      si2.next();

      int loc = si2.getLocation();
      int galIndex = gal.getIndexFromALocation(loc);

      if (galIndex != -1)
        colorsToSet[galIndex] = colors[i];
      i++;
    }

    bioAssayArrayPlot.setDataFieldInt(ArrayPlot.FIELD_NAME_RGB_COLOR,
        colorsToSet);

    bioAssayArrayPlot.setSpotEmptyTester(gpr.getSpotEmptyTester());

    return bioAssayArrayPlot;
  }

  /**
   * Create a new array list (a GAL BioAssay object) without bad spots.
   * @param gal initial array list
   * @param gpr initial BioAssay
   * @param result Map of the unit tests results
   * @return an new array list without the bad spots
   */
  private BioAssay reFlag(final BioAssay gal, final BioAssay gpr,
      final QualityUnitTestResult[] result, final String rejectedSpotId) {

    if (gal == null || gpr == null || result == null
        || new ImaGeneArrayList(gal).isImageneArrayList())
      return null;

    // Test if the gal file is compatible with the gpr data
    SpotIterator si = gal.iterator();
    while (si.hasNext()) {
      si.next();
      if (gpr.getIndexFromALocation(si.getLocation()) == -1)
        return null;
    }

    // Create new Gal object
    BioAssay newGal = BioAssayFactory.createBioAssay();
    newGal.getAnnotation().addProperties(gal.getAnnotation());
    newGal.setSpotEmptyTester(gpr.getSpotEmptyTester());

    final String[] newGalIds = (String[]) gal.getIds().clone();
    final int[] newGalLocations = (int[]) gal.getLocations().clone();
    final String[] newGalDesc = (String[]) gal.getDescriptions().clone();

    newGal.setIds(newGalIds);
    newGal.setLocations(newGalLocations);
    newGal.setDescriptions(newGalDesc);

    final int[] gprLocations = gpr.getLocations();

    boolean[] globalResultRequest = null;

    for (int i = 0; i < result.length; i++) {
      QualityUnitTestResult r = result[i];

      if (r.isError())
        continue;

      final boolean[] flags = r.getNewFlags();

      if (globalResultRequest == null) {
        globalResultRequest = new boolean[flags.length];
        Arrays.fill(globalResultRequest, true);
      }

      if (r.isFilterFlags())
        for (int j = 0; j < globalResultRequest.length; j++) {

          if (globalResultRequest[j] && !flags[j]) {
            globalResultRequest[j] = false;

            int loc = gprLocations[j];
            int galIndex = gal.getIndexFromALocation(loc);

            if (galIndex >= 0) {

              StringBuffer sb = new StringBuffer();
              sb.append("id=").append(newGalIds[galIndex]).append(
                  "; description=").append(newGalDesc[galIndex]).append(
                  "; fail to ").append(r.getTestDescription());

              newGalDesc[galIndex] = sb.toString();
              newGalIds[galIndex] = rejectedSpotId;

            }

          } else if (!flags[j]) {

            int loc = gprLocations[j];
            int galIndex = gal.getIndexFromALocation(loc);

            if (galIndex >= 0) {

              StringBuffer sb = new StringBuffer();
              sb.append(newGalDesc[galIndex]).append(", ").append(
                  r.getTestDescription());

              newGalDesc[galIndex] = sb.toString();
            }
          }
        }
    }

    return newGal;
  }

  private Image getArrayPlot(final BioAssay gal) {

    if (gal == null)
      return null;

    ArrayBlock[] blocks;

    getLogger().debug("Gal type: " + BioAssayUtils.getBioAssayType(gal));

    switch (BioAssayUtils.getBioAssayType(gal)) {

    case BioAssayUtils.GAL_BIOASSAY_TYPE:
      blocks = new GenepixArrayList(gal).getBlocks();
      break;

    case BioAssayUtils.IMAGENE_ARRAYLIST_BIOASSAY_TYPE:
      blocks = new ImaGeneArrayList(gal).getBlocks();
      break;

    default:
      return null;
    }

    try {
      ArrayPlot ap = new ArrayPlot(blocks, Defaults.ARRAY_PLOT_WIDTH, -1,
          Defaults.ARRAY_PLOT_MARGIN, gal);
      return ap.getImage();
    } catch (NividicRuntimeException e) {
      return null;
    }
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *           <b>null </b>.
   */
  public DoelanExecuteGlobalTests() throws PlatformException {
    // MUST BE EMPTY
  }

}
