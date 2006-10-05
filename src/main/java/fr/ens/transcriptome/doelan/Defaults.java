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

package fr.ens.transcriptome.doelan;

import fr.ens.transcriptome.doelan.algorithms.DoelanConfigure;
import fr.ens.transcriptome.doelan.algorithms.DoelanExecuteGlobalTests;
import fr.ens.transcriptome.doelan.algorithms.DoelanGenerateReport;
import fr.ens.transcriptome.doelan.algorithms.DoelanLoadGenepixData;
import fr.ens.transcriptome.doelan.algorithms.DoelanShowReport;
import fr.ens.transcriptome.doelan.tests.AbsentSpotTest;
import fr.ens.transcriptome.doelan.tests.BadFeatureTest;
import fr.ens.transcriptome.doelan.tests.GenericTest;
import fr.ens.transcriptome.doelan.tests.GlobalMaxBadFeatureRemoved;
import fr.ens.transcriptome.doelan.tests.GlobalMaxBadFeaturesTest;
import fr.ens.transcriptome.doelan.tests.HeterogeneousFeatureTest;
import fr.ens.transcriptome.doelan.tests.MaxDiameterFeatureTest;
import fr.ens.transcriptome.doelan.tests.MinDiameterFeatureTest;
import fr.ens.transcriptome.doelan.tests.NotFoundFeatureTest;
import fr.ens.transcriptome.doelan.tests.SaturatedPixelsTest;
import fr.ens.transcriptome.doelan.tests.MinimalIntensityTest;
import fr.ens.transcriptome.nividic.util.Version;

/**
 * Default values of the application.
 * @author Laurent Jourdren
 */
public final class Defaults {

  /** Config file. */
  public static final String CONFIG_FILE = "/files/app.properties";
  /** Debug mode. */
  public static final boolean DEBUG = false;
  /** sub directory for data. */
  public static final String SUBDIR_DATA = "data";
  /** sub directory for the reports. */
  public static final String SUBDIR_REPORT = "reports";
  /** Default test suite list file. */
  public static final String TEST_SUITE_LIST_FILE = "default.tsl";
  /** Name of the application. */
  public static final String APP_NAME = "Doelan";
  /** Description of the application. */
  public static final String APP_DESCRITPION = "a microarray quality test suite software";
  /** Version of the application. */
  public static final String VERSION = "??.??";
  /** Default version of the tests. */
  public static final Version DEFAULT_TEST_VERSION = new Version("1.0.1");
  /** Copyrigth date. */
  public static final String COPYRIGHT_DATE = "2004-200x";
  /** Manufacturer. */
  public static final String MANUFACTURER = "École Normale Supérieure Microarray Platform";
  /** Authors */
  public static final String AUTHORS = "Laurent Jourdren";
  /** Web site. */
  public static final String WEBSITE = "http://transcriptome.ens.fr/doelan";
  /** Web site. */
  public static final String MANUFACTURER_WEBSITE = "http://transcriptome.ens.fr/doelan";

  /** Licence. */
  public static final String LICENCE = "General Public Licence";
  /** Internals modules. */
  public static final Class[] INTERNALS_MODULES = {AbsentSpotTest.class,
  //DiameterFeatureTest.class,
      MinDiameterFeatureTest.class, MaxDiameterFeatureTest.class,
      DoelanLoadGenepixData.class, DoelanExecuteGlobalTests.class,
      DoelanGenerateReport.class, DoelanShowReport.class,
      NotFoundFeatureTest.class, BadFeatureTest.class,
      SaturatedPixelsTest.class, HeterogeneousFeatureTest.class,
      MinimalIntensityTest.class, GlobalMaxBadFeaturesTest.class,
      GlobalMaxBadFeatureRemoved.class, DoelanConfigure.class,
      GenericTest.class};

  /** Main window width. */
  public static final int WINDOW_WIDTH = 800;
  /** Main window height. */
  public static final int WINDOW_HEIGHT = 600;

  /** Array plot width. */
  public static final int ARRAY_PLOT_WIDTH = 600;
  /** Array plot margin. */
  public static final int ARRAY_PLOT_MARGIN = 10;

  /** Chart plot width. */
  public static final int DOLEAN_CHART_WIDTH = 500;
  /** Chart plot height. */
  public static final int DOELAN_CHART_HEIGHT = 400;

  /** Type of workflow for doelan test suite. */
  public static final String DOELAN_WORKFLOW_TYPE = "DoelanTestSuite";
  /** Version of the workflow for doelan test suite. */
  public static final String DOELAN_WORKFLOW_VERSION = "1.0";
  /** Annatation key for the version of the test suite workflow. */
  public static final String DOELAN_WORKFLOW_VERSION_ANNOTATION_KEY = "doelan.workflow.version";
  /** Annaotation key of the type of the chip. */
  public static final String CHIP_TYPE_ANNOTATION_KEY = "doelan.chip.type";
  /** Annatotation key of the test suite name. */
  public static final String TEST_SUITE_NAME_ANNOTATION_KEY = "doelan.testsuite.name";
  /** The identifier of the rejected spots. */
  public static final String REJECTED_SPOT_IDENTIFIER = "rejected";

}