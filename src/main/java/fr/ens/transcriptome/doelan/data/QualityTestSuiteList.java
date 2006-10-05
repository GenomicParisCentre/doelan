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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.ens.transcriptome.doelan.DoelanException;

/**
 * This class defines a container for testsuites.
 * @author Laurent Jourdren
 */
public class QualityTestSuiteList {

  private Map suites = new HashMap();
  private Map reverseIndex = new HashMap();

  /**
   * Add a testSuite to the list
   * @param chipType Type of the TestSuite
   * @param testSuiteURL TestSuite to add
   * @throws DoelanException if an error occur while adding the element
   */
  public void addTestSuite(final String chipType,
      final QualityTestSuiteURL testSuiteURL) throws DoelanException {

    if (testSuiteURL == null || testSuiteURL.getName().equals(""))
      throw new DoelanException("Test suite name is empty");

    Set s = (Set) this.suites.get(chipType);

    if (s == null)
      throw new DoelanException("The chip type doesn't exists");

    Iterator it = s.iterator();
    while (it.hasNext()) {
      QualityTestSuiteURL qtsurl = (QualityTestSuiteURL) it.next();
      if (qtsurl.getName().equals(testSuiteURL.getName()))
        throw new DoelanException("This test suite name already exists");
    }

    s.add(testSuiteURL);

    this.suites.put(chipType, s);
    this.reverseIndex.put(testSuiteURL, chipType);

  }

  /**
   * Add a chip type to the list
   * @param chipType Type of the TestSuite
   * @throws DoelanException if an error occurs while adding a new chip type
   */
  public void addChipType(final String chipType) throws DoelanException {

    if (chipType == null)
      throw new DoelanException("The chip type is null");

    Set s = (Set) this.suites.get(chipType);

    if (s != null)
      throw new DoelanException("The chip type already exists");

    this.suites.put(chipType, new HashSet());

  }

  /**
   * Remove a test suite
   * @param testSuiteURL Test suite to remove
   * @throws DoelanException if an error occur while removing the element
   */
  public void removeTestSuite(final QualityTestSuiteURL testSuiteURL)
      throws DoelanException {

    if (testSuiteURL == null)
      throw new DoelanException("The test suite doesn't exists");

    String chipType = getChipType(testSuiteURL);
    if (chipType == null)
      throw new DoelanException("This test suite has no chip type");

    Set s = (Set) this.suites.get(chipType);

    s.remove(testSuiteURL);
    this.reverseIndex.remove(testSuiteURL);

  }

  /**
   * Remove a chip if it is empty.
   * @param chipType The chip type to remove
   * @throws DoelanException if an error occur while removing the element
   */
  public void removeChipType(final String chipType) throws DoelanException {

    if (chipType == null)
      throw new DoelanException("The chip type doesn't exists");

    Set s = (Set) this.suites.get(chipType);
    if (s == null)
      throw new DoelanException("The chip type doesn't exists");

    if (s.size() != 0)
      throw new DoelanException("The chip type contains tests suites");

    this.suites.remove(chipType);
  }

  /**
   * Rename a chip type
   * @param oldName Old name of the chip type
   * @param newName New name of the chip type
   * @throws DoelanException if an error occur while removing the element
   */
  public void renameChipType(final String oldName, final String newName)
      throws DoelanException {

    if (oldName == null || newName == null)
      throw new DoelanException("The new or old name is null");

    if (this.suites.containsKey(newName))
      throw new DoelanException("The name already exists");

    Set s = (Set) this.suites.get(oldName);
    if (s == null)
      throw new DoelanException("The old chip name doesn't exists");

    this.suites.put(newName, s);
    this.suites.remove(oldName);

    Iterator it = s.iterator();
    while (it.hasNext())
      this.reverseIndex.put(it.next(), newName);
  }

  /**
   * Rename a test suite
   * @param chipType Chip type of the test suite to rename
   * @param oldName Old name of the test suite
   * @param newName New name of the test suite
   * @throws DoelanException if an error occurs while renaming the test suite
   */
  public void renameTestSuite(final String chipType, final String oldName,
      final String newName) throws DoelanException {

    if (oldName == null || newName == null)
      throw new DoelanException("The new or old name is null");

    Set s = (Set) this.suites.get(chipType);
    if (s == null)
      throw new DoelanException("The chip type doesn't exists");

    Iterator it = s.iterator();
    while (it.hasNext()) {

      QualityTestSuiteURL qtsurl = (QualityTestSuiteURL) it.next();
      if (qtsurl.getName().equals(newName))
        throw new DoelanException("The test suite already exists");
    }

    it = s.iterator();
    while (it.hasNext()) {

      QualityTestSuiteURL qtsurl = (QualityTestSuiteURL) it.next();
      if (qtsurl.getName().equals(oldName)) {
        qtsurl.setName(newName);
        return;
      }
    }

    throw new DoelanException("The test suite doesn't exists");
  }

  /**
   * Clear the list.
   */
  public void clear() {
    this.suites.clear();
    this.reverseIndex.clear();
  }

  /**
   * Get the type list of the TestSuites
   * @return An array string with the names of the types
   */
  public String[] getChipTypes() {

    Set s = this.suites.keySet();
    final int size = s.size();
    String[] result = new String[size];

    int i = 0;
    Iterator it = s.iterator();
    while (it.hasNext())
      result[i++] = (String) it.next();

    return result;
  }

  /**
   * Return all the TestSuiteURL for a chip type
   * @param chipType Chip type of the testsuites
   * @return An array of TestSuiteURL
   */
  public QualityTestSuiteURL[] getTestSuiteURLs(final String chipType) {

    Set s = (Set) this.suites.get(chipType);
    if (s == null)
      return null;

    QualityTestSuiteURL[] result = new QualityTestSuiteURL[s.size()];

    int i = 0;
    Iterator it = s.iterator();
    while (it.hasNext())
      result[i++] = (QualityTestSuiteURL) it.next();

    return result;
  }

  /**
   * Get the type of a test suite from this test suite url.
   * @param url URL of the test suite.
   * @return The type of the test suite
   */
  public String getChipType(final QualityTestSuiteURL url) {
    return (String) this.reverseIndex.get(url);
  }

  /**
   * Return the number of chip types.
   * @return The number of chip types
   */
  public int size() {
    return this.suites.size();
  }

  /**
   * Test if a test suite is set.
   * @return true if a test suite is set.
   */
  public boolean isATestSuite() {

    Iterator it1 = this.suites.keySet().iterator();

    while (it1.hasNext()) {
      String key = (String) it1.next();
      Set s = (Set) this.suites.get(key);

      if (s.size() > 0)
        return true;
    }

    return false;
  }

}