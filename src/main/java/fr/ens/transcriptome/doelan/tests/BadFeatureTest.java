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

package fr.ens.transcriptome.doelan.tests;

import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * This class defines a test for not found spot flag.
 * @author Laurent Jourdren
 */
public class BadFeatureTest extends FeatureFlagTest implements Module {

  /** Default value of the threshold. */
  private static final double DEFAULT_THRESHOLD = 5.0;
  private static AboutModule aboutModule;

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {

    if (aboutModule == null) {

      ModuleDescription md = null;
      try {
        md = new ModuleDescription("BadFeatureTest",
            "Test for bad feature flag (Flag=-100)");
        md.setWebsite(DoelanRegistery.getAppURL());
        md.setHTMLDocumentation(SystemUtils.readTextRessource("/files/test-"
            + SystemUtils.getClassShortName(this.getClass()) + ".html"));
        md.setStability(AboutModule.STATE_STABLE);
        md.setVersion(Defaults.DEFAULT_TEST_VERSION);
      } catch (PlatformException e) {
        getLogger().error("Unable to create the module description");
      }
      aboutModule = md;
    }

    return aboutModule;
  }

  protected int getFlagFilterValue() {
    return BioAssay.FLAG_BAD;
  }

  protected String getFlagFilerType() {
    return "Bad";
  }

  protected double getDefaultThreshold() {
    return DEFAULT_THRESHOLD;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *                 <b>null </b>.
   */
  public BadFeatureTest() throws PlatformException {
    // MUST BE EMPTY
  }
}