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

package fr.ens.transcriptome.doelan.data;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.data.Data;
import fr.ens.transcriptome.nividic.platform.data.DataDefaults;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.util.SystemUtils;

/**
 * This class define a nividic data object for testsuite results.
 * @author Laurent Jourdren
 */
public class TestSuiteResultData extends Data implements Module {

  //For logging system
  private static Logger log = Logger.getLogger(TestSuiteResultData.class);

  /**
   * Get the name of the data.
   * @return The name of the data
   */
  public String getName() {
    return SystemUtils.getClassNameWithoutPackage(TestSuiteResultData.class);
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
    return DoelanDataDefaults.TESTSUITE_RESULT_TYPE;
  }

  /**
   * Get the class of the data.
   * @return The class of the data.
   */
  public Class getDataClass() {
    return TestSuiteResult.class;
  }

  /**
   * Set the Result.
   * @param result Result to set
   */
  public void setData(final TestSuiteResult result) {

    try {
      super.setData(result);
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
      md = new ModuleDescription("TestSuiteResult",
          "QualityTestSuiteResult result data type");
    } catch (PlatformException e) {
      log.error("Unable to create module description");
    }
    return md;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param result Result of a QualityUnitTest
   */
  public TestSuiteResultData(final TestSuiteResult result) {
    setData(result);
  }

}