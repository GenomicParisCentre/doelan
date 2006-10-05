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

import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.doelan.data.DoelanDataUtils;
import fr.ens.transcriptome.doelan.data.TestSuiteResult;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.DefaultSpotEmptyTester;
import fr.ens.transcriptome.nividic.om.SpotEmptyTester;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.platform.workflow.SimpleAlgorithmEvent;
import fr.ens.transcriptome.nividic.util.SystemUtils;
import fr.ens.transcriptome.nividic.util.parameter.FixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterBuilder;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * Configure Defaults for the test suite.
 * @author Laurent Jourdren
 */
public class DoelanConfigure extends QualityTest implements Module {

  private static final String IDENTIFIER_COLUMN = "Identifier";
  private static final String DESCRIPTION_COLUMN = "Description";

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    ModuleDescription md = null;
    try {
      md = new ModuleDescription("DoelanConfigure",
          "Configure global parameters for the test suite");
      md.setStability(AboutModule.STATE_STABLE);
      md.setWebsite(DoelanRegistery.getAppURL());
      md.setHTMLDocumentation(SystemUtils.readTextRessource("/files/test-"
          + SystemUtils.getClassShortName(this.getClass()) + ".html"));
      md.setVersion(Defaults.DEFAULT_TEST_VERSION);
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

      final Parameter emptyIds = new ParameterBuilder().withName("emptyIds")
          .withLongName("Empty identifiers").withType(
              Parameter.DATATYPE_ARRAY_STRING).withDescription(
              "Identifiers for empty spots").withDefaultValue("\"empty\"")
          .getParameter();

      final Parameter emptyIdColumn = new ParameterBuilder().withName(
          "emptyIdColumn").withLongName("Empty identifiers column").withType(
          Parameter.DATATYPE_STRING).withDescription(
          "Column for empty identifiers").withChoices(
          new String[] {IDENTIFIER_COLUMN, DESCRIPTION_COLUMN})
          .withDefaultValue(IDENTIFIER_COLUMN).getParameter();

      final Parameter rejectedlId = new ParameterBuilder().withName(
          "rejectedlId").withLongName("New identifier for rejected spots")
          .withType(Parameter.DATATYPE_STRING).withDescription(
              "New identifier for rejected spots").withDefaultValue(
              Defaults.REJECTED_SPOT_IDENTIFIER).getParameter();

      final FixedParameters params = new FixedParameters();
      params.addParameter(emptyIds);
      params.addParameter(emptyIdColumn);
      params.addParameter(rejectedlId);

      return params;

    } catch (ParameterException e) {
      getLogger().error("Error while creating parameters: " + e);
    }

    return null;
  }

  /**
   * Test if the test is deletable().
   * @return true if the test is deletable
   */
  public boolean isDeletable() {
    return false;
  }

  /**
   * Test if only one instance of the test could be created.
   * @return true if only one instance of the test could be created
   */
  public boolean isUniqueInstance() {
    return true;
  }

  /**
   * Test if the test is modifiable.
   * @return true if the test is modifiable
   */
  public boolean isModifiable() {
    return true;
  }

  /**
   * Test if the test could be showed.
   * @return true if the test could be showed
   */
  public boolean isShowable() {
    return true;
  }

  /**
   * Test if the test could be diplayed in the list of tests to add.
   * @return true if the test could be showed
   */
  public boolean isAddable() {
    return false;
  }

  protected void doIt(final Container c, final Parameters parameters)
      throws PlatformException {

    // Get test suite result
    BioAssay bioAssay = DoelanDataUtils.getBioAssay(c);
    BioAssay gal = DoelanDataUtils.getArrayList(c);

    TestSuiteResult tsr = DoelanDataUtils.getTestSuiteResult(c);

    String[] emptyIds;
    try {
      emptyIds = parameters.getParameter("emptyIds").getArrayStringValues();
      final String rejectedlId = parameters.getParameter("rejectedlId")
          .getStringValue();
      final String emptyIdColumn = parameters.getParameter("emptyIdColumn")
          .getStringValue();

      tsr.setSpotRejectedId(rejectedlId);
      tsr.setEmptySpotIds(emptyIds);

      if (emptyIds != null) {
        SpotEmptyTester set = new DefaultSpotEmptyTester(emptyIds,
            DESCRIPTION_COLUMN.equals(emptyIdColumn));

        bioAssay.setSpotEmptyTester(set);

        if (gal != null)
          gal.setSpotEmptyTester(bioAssay.getSpotEmptyTester());

      }

    } catch (ParameterException e) {
      getLogger().error(
          "Error while creating parameters (" + this.getClass().getName()
              + "): " + e.getMessage());
    }

    sendEvent(new SimpleAlgorithmEvent(this, CONFIGURE_TEST_EVENT,
        this.getId(), "set configure global parameters"));
  } //

  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *           <b>null </b>.
   */
  public DoelanConfigure() throws PlatformException {
    // MUST BE EMPTY
  }

}
