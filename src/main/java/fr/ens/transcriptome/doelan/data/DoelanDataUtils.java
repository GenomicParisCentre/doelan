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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.platform.data.Data;
import fr.ens.transcriptome.nividic.platform.data.DataDefaults;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.platform.workflow.ContainerFilter;

/**
 * This class defines some useful method for quality tests
 * @author Laurent Jourdren
 */
public final class DoelanDataUtils {

  /**
   * This methods return the first BioAssay found in a Container
   * @param container Container of the objects
   * @return the first bioassay found in a Container
   */
  public static BioAssay getBioAssay(final Container container) {

    final Container bioassays = container.filterFormat(DataDefaults.OM_FORMAT)
        .filterType(DataDefaults.BIOASSAY_TYPE);

    Iterator it = bioassays.iterator();

    if (it.hasNext()) {
      Data rd = (Data) it.next();
      return (BioAssay) rd.getData();
    }

    return null;
  }

  /**
   * This methods return all the BioAssay found in a Container
   * @param container Container of the objects
   * @return the bioassay objects found in a Container
   */
  public static BioAssay[] getBioAssays(final Container container) {

    final Container bioassays = container.filterFormat(DataDefaults.OM_FORMAT)
        .filterType(DataDefaults.BIOASSAY_TYPE);

    BioAssay[] result = new BioAssay[bioassays.size()];

    Iterator it = bioassays.iterator();
    int i = 0;

    while (it.hasNext()) {
      Data rd = (Data) it.next();
      result[i++] = (BioAssay) rd.getData();
    }

    return result;
  }

  /**
   * This methods return the first array list found in a Container
   * @param container Container of the objects
   * @return the first array lits found in a Container
   */
  public static BioAssay getArrayList(final Container container) {

    final Container arraylists = container.filterFormat(DataDefaults.OM_FORMAT)
        .filterType(DataDefaults.ARRAYLIST_TYPE);

    Iterator it = arraylists.iterator();

    if (it.hasNext()) {
      Data rd = (Data) it.next();
      return (BioAssay) rd.getData();
    }

    return null;
  }

  /**
   * This methods return all the BioAssay found in a Container
   * @param container Container of the objects
   * @return the bioassay objects found in a Container
   */
  public static BioAssay[] getArrayLists(final Container container) {

    final Container arraylists = container.filterFormat(DataDefaults.OM_FORMAT)
        .filterType(DataDefaults.ARRAYLIST_TYPE);

    BioAssay[] result = new BioAssay[arraylists.size()];

    Iterator it = arraylists.iterator();
    int i = 0;

    while (it.hasNext()) {
      Data rd = (Data) it.next();
      result[i++] = (BioAssay) rd.getData();
    }

    return result;
  }

  /**
   * This methods return all the unit test results found in a Container
   * @param container Container of the objects
   * @return the unit test results objects found in a Container
   */
  public static QualityUnitTestResult[] getQualityUnitTestResults(
      final Container container) {

    final Container results = container.filterFormat(DataDefaults.OM_FORMAT)
        .filterType(DoelanDataDefaults.UNIT_TEST_RESULT_TYPE);

    QualityUnitTestResult[] result = new QualityUnitTestResult[results.size()];

    Iterator it = results.iterator();
    int i = 0;

    while (it.hasNext()) {
      Data rd = (Data) it.next();
      result[i++] = (QualityUnitTestResult) rd.getData();
    }

    return result;
  }

  /**
   * This methods return all the global test results found in a Container
   * @param container Container of the objects
   * @return the global test results objects found in a Container
   */
  public static QualityGlobalTestResult[] getQualityGlobalTestResults(
      final Container container) {

    final Container results = container.filterFormat(DataDefaults.OM_FORMAT)
        .filterType(DoelanDataDefaults.GLOBAL_TEST_RESULT_TYPE);

    QualityGlobalTestResult[] result = new QualityGlobalTestResult[results
        .size()];

    Iterator it = results.iterator();
    int i = 0;

    while (it.hasNext()) {
      Data rd = (Data) it.next();
      result[i++] = (QualityGlobalTestResult) rd.getData();
    }

    return result;
  }

  /**
   * This methods return all the test results found in a Container
   * @param container Container of the objects
   * @return the global results objects found in a Container
   */
  public static QualityTestResult[] getQualityTestResults(
      final Container container) {

    final Container results = container.filterFormat(DataDefaults.OM_FORMAT)
        .filter(new ContainerFilter() {

          public boolean accept(final Data rd) {

            return rd.getType()
                .equals(DoelanDataDefaults.UNIT_TEST_RESULT_TYPE)
                || rd.getType().equals(
                    DoelanDataDefaults.GLOBAL_TEST_RESULT_TYPE);
          }
        });

    QualityTestResult[] result = new QualityTestResult[results.size()];

    Iterator it = results.iterator();
    int i = 0;

    while (it.hasNext()) {
      Data rd = (Data) it.next();
      result[i++] = (QualityTestResult) rd.getData();
    }

    return result;
  }

  /**
   * This methods return all the test results found in a Container
   * @param container Container of the objects
   * @return the global results objects found in a Container
   */
  public static QualityTestResult[] getOrderedQualityTestResults(
      final Container container) {

    TestSuiteResult tsr = getTestSuiteResult(container);
    if (tsr == null)
      return null;
    String[] order = tsr.getTestIdsOrder();

    if (order == null)
      return null;

    final Container results = container.filterFormat(DataDefaults.OM_FORMAT)
        .filter(new ContainerFilter() {

          public boolean accept(final Data rd) {

            return rd.getType()
                .equals(DoelanDataDefaults.UNIT_TEST_RESULT_TYPE)
                || rd.getType().equals(
                    DoelanDataDefaults.GLOBAL_TEST_RESULT_TYPE);
          }
        });

    QualityTestResult[] result = new QualityTestResult[order.length];

    Map map = new HashMap();
    Iterator it = results.iterator();

    while (it.hasNext()) {
      Data rd = (Data) it.next();

      QualityTestResult r = (QualityTestResult) rd.getData();
      map.put(r.getTestId(), r);
    }

    for (int j = 0; j < result.length; j++)
      result[j] = (QualityTestResult) map.get(order[j]);

    return result;
  }

  /**
   * This methods return the first test suite result found in a Container
   * @param container Container of the objects
   * @return the first test suite result found in a Container
   */
  public static TestSuiteResult getTestSuiteResult(final Container container) {

    final Container arraylists = container.filterFormat(DataDefaults.OM_FORMAT)
        .filterType(DoelanDataDefaults.TESTSUITE_RESULT_TYPE);

    Iterator it = arraylists.iterator();

    if (it.hasNext()) {
      Data rd = (Data) it.next();
      return (TestSuiteResult) rd.getData();
    }

    return null;
  }

  //
  // Constructor
  //

  /**
   * Private constructor.
   */
  private DoelanDataUtils() {
  }

}
