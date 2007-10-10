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

package fr.ens.transcriptome.nividic.platform.workflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.ens.transcriptome.nividic.platform.data.Data;

/**
 * A container for nividic data objects.
 * @author Laurent Jourdren
 */
public final class Container {

  private static int count;

  private int id = count++;

  private Map<Integer, Data> dataMap = new HashMap<Integer, Data>();
  private Set<Data> dataVector = new HashSet<Data>();

  //
  // Getters
  //

  /**
   * Get the unique identifier of the container.
   * @return The identifier of the container
   */
  public int getId() {
    return id;
  }

  //
  // Other methods
  //

  /**
   * Add a nividic data object.
   * @param nividicData The nividic data object to add
   * @return <b>true </b> if add is ok.
   */
  public boolean add(final Data nividicData) {
    if (nividicData == null)
      return false;
    if (this.dataMap.containsKey(nividicData.getInstanceId()))
      return false;
    this.dataMap.put(nividicData.getInstanceId(), nividicData);
    this.dataVector.add(nividicData);
    return true;
  }

  /**
   * Remove a nividic data object.
   * @param nividicData The nividic data object to add
   * @return <b>true </b> if remove is ok.
   */
  public boolean remove(final Data nividicData) {
    if (nividicData == null)
      return false;
    this.dataMap.remove(nividicData.getInstanceId());
    this.dataVector.remove(nividicData);
    return true;
  }

  /**
   * Get the data object with the <b>id </b> identifier.
   * @param id The identifier of the object to get.
   * @return A data object
   */
  public Data get(final int id) {
    return this.dataMap.get(id);
  }

  /**
   * Return an iterator from the container.
   * @return The iterator
   */
  public Iterator iterator() {
    return this.dataVector.iterator();
  }

  /**
   * Return the size of the container.
   * @return The size of the container
   */
  public int size() {
    return this.dataMap.size();
  }

  /**
   * Filter the container.
   * @param cf Object used to filter the container
   * @return A new container with the data object selected
   */
  public Container filter(final ContainerFilter cf) {

    if (cf == null)
      return null;
    Container result = new Container();
    Iterator i = iterator();
    while (i.hasNext()) {
      Data rd = (Data) i.next();
      if (cf.accept(rd))
        result.add(rd);
    }

    return result;
  }

  /**
   * Filter the container using data name for rule.
   * @param name The name of the data objects to select.
   * @return A container
   */
  public Container filterName(final String name) {

    if (name == null)
      return null;
    return filter(new ContainerFilter() {
      public boolean accept(final Data rd) {
        return rd.getName().equals(name);
      }
    });
  }

  /**
   * Filter the container with the data object type for rule.
   * @param type The type of the object to be selected
   * @return A container
   */
  public Container filterType(final String type) {

    if (type == null)
      return null;
    return filter(new ContainerFilter() {
      public boolean accept(final Data rd) {
        return rd.getType().equals(type);
      }
    });
  }

  /**
   * Filter the container with the data object format for rule.
   * @param format The format of the object to be selected
   * @return A container
   */
  public Container filterFormat(final String format) {

    if (format == null)
      return null;
    return filter(new ContainerFilter() {
      public boolean accept(final Data rd) {
        return rd.getFormat().equals(format);
      }
    });
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public Container() {
  }

}
