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

import java.util.ArrayList;
import java.util.Iterator;

import fr.ens.transcriptome.nividic.platform.PlatformException;

/**
 * Pad out.
 * @author Laurent Jourdren
 */
public class PadOut {

  private ArrayList nextPadIns = new ArrayList();

  /**
   * Attach a padIn to the padOut.
   * @param pad PadIn to chain
   * @throws PlatformException if pad is <b>null </b> or if the object is
   *                 already chain
   */
  public void add(final PadIn pad) throws PlatformException {

    if (pad == null)
      throw new PlatformException("Pad to chain is null");

    if (this.nextPadIns.contains(pad))
      throw new PlatformException("padIn is alread link to this padOut");

    this.nextPadIns.add(pad);
  }

  /**
   * Detach a padIn to the padOut.
   * @param pad PadIn to chain
   * @throws PlatformException if pad is <b>null </b> or if the object is
   *                 not chain
   */
  public void remove(final PadIn pad) throws PlatformException {
    if (pad == null)
      throw new PlatformException("Pad to chain is null");

    if (!this.nextPadIns.contains(pad))
      throw new PlatformException(
          "this padIn is not linked to this padOut");

    this.nextPadIns.remove(pad);
  }

  /**
   * Break the link.
   */
  public void removeAll() {
    this.nextPadIns = new ArrayList();
  }

  /**
   * Get the number of PadIns.
   * @return The number of PadIns
   */
  int getNumberPadIns() {
    return this.nextPadIns.size();
  }

  void notifyContainer(final Container c) throws PlatformException {

    if (c == null)
      throw new PlatformException("Container to send is null");

    Iterator it = this.nextPadIns.iterator();
    while (it.hasNext()) {
      ((PadIn) it.next()).handleEvent(c);
    }

  }

  /**
   * Default contructor.
   */
  public PadOut() {
  }

}