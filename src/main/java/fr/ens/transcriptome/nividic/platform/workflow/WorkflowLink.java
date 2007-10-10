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

import fr.ens.transcriptome.nividic.platform.PlatformException;

/**
 * This class defines a link between two workflow elements.
 * @author Laurent Jourdren
 */
public final class WorkflowLink {

  private WorkflowElement from;
  private WorkflowElement to;
  private String toReference;
  private boolean algorithmLink;
  private boolean referenceMode;

  //
  // Getters
  //

  /**
   * Get The in element.
   * @return Returns the in element
   */
  public WorkflowElement getFrom() {
    return from;
  }

  /**
   * Get the out element.
   * @return Returns the out
   */
  public WorkflowElement getTo() {
    return to;
  }

  /**
   * Get the out reference.
   * @return Returns the outReference
   */
  String getOutReference() {
    return toReference;
  }

  /**
   * Test if the workflow link is in reference mode.
   * @return Returns the referenceMode
   */
  boolean isReferenceMode() {
    return referenceMode;
  }

  //
  // Setters
  //

  /**
   * Set the in element.
   * @param from The in to set
   * @throws PlatformException if the element is null
   */
  private void setFrom(final WorkflowElement from)
      throws PlatformException {

    if (from == null)
      throw new PlatformException("in link element is null");
    this.from = from;
  }

  /**
   * Set the out element.
   * @param to The out to set
   * @throws PlatformException if the element is null
   */
  private void setTo(final WorkflowElement to) throws PlatformException {

    if (to == null)
      throw new PlatformException("out link element is null");
    this.to = to;
  }

  /**
   * Set the out reference.
   * @param outReference The outReference to set
   */
  private void setToReference(final String outReference) {
    this.toReference = outReference;
  }

  //
  // Others methods
  //

  /**
   * Link the elements.
   * @throws PlatformException if an error occurs while linking the
   *                 elements
   */
  private void link() throws PlatformException {

    getFrom().addNextElementLink(this);
    if (!isReferenceMode()) {
      getTo().addPreviousElementLink(this);
      linkAlgorithms();
    }
  }

  /**
   * Unlink the elements.
   * @throws PlatformException if an error occurs while linking the
   *                 elements
   */
  void unlink() throws PlatformException {

    if (this.algorithmLink)
      unlinkAlgorithms();

    getFrom().removeNextElementLink(this);
    getTo().removePreviousElementLink(this);

  }

  /**
   * Link the algorithm of the elements.
   * @throws PlatformException if an error occurs while linking the
   *                 algorithms
   */
  void linkAlgorithms() throws PlatformException {

    replaceReferenceByElement();
    if (!this.algorithmLink && getFrom().isAlgorithmInstance()
        && getTo().isAlgorithmInstance()) {
      getFrom().getAlgorithm().out().add(getTo().getAlgorithm().in());
      this.algorithmLink = true;
    }

  }

  /**
   * Unlink the algorithm of the elements.
   * @throws PlatformException
   * @throws PlatformException if an error occurs while linking the
   *                 algorithms
   */
  private void unlinkAlgorithms() throws PlatformException {

    if (this.algorithmLink && getFrom().isAlgorithmInstance()
        && getTo().isAlgorithmInstance()) {
      getFrom().getAlgorithm().out().remove(getTo().getAlgorithm().in());
      this.algorithmLink = false;
    }

  }

  /**
   * Replace identifier to workflow element by workflow elements objects.
   * @throws PlatformException if the element of the reference doesn't
   *                 exits
   */
  void replaceReferenceByElement() throws PlatformException {

    if (!isReferenceMode())
      return;

    WorkflowElement wfe = from.getWorkflow().getElement(getOutReference());

    if (wfe == null)
      throw new PlatformException(
          "Can't find an element for reference link (" + getOutReference()
              + ")");
    this.referenceMode = false;
    setTo(wfe);
    getTo().addPreviousElementLink(this);
    linkAlgorithms();
    //link();
  }

  /**
   * Get the unique identifier of a (virtual) link
   * @param in First element of the link
   * @param out Second element of the link
   * @return the unique identifier of a (virtual) link
   */
  public static String getId(final WorkflowElement in, final WorkflowElement out) {
    if (in == null || out == null)
      return null;
    return in.getId() + "-" + out.getId();
  }

  /**
   * Get the unique identifier of a (virtual) link
   * @param in First element of the link
   * @param outReference Reference to second element of the link
   * @return the unique identifier of a (virtual) link
   */
  public static String getId(final WorkflowElement in, final String outReference) {
    if (in == null || outReference == null)
      return null;
    return in.getId() + "-" + outReference;
  }

  /**
   * Get the unique identifier of the link
   * @return the unique identifier of the link
   */
  public String getId() {
    if (isReferenceMode())
      return getId(getFrom(), getOutReference());
    return getId(getFrom(), getTo());
  }

  /**
   * ToString method.
   * @return a description of the link
   */
  public String toString() {

    return "from " + (from == null ? null : from.getId()) + " to "
        + (to == null ? toReference : to.getId());
  }

  //
  // Public constructor
  //

  /**
   * Public constructor.
   * @param in In element of the link
   * @param out Out element of the link
   * @throws PlatformException if the link can'be created
   */
  WorkflowLink(final WorkflowElement in, final WorkflowElement out)
      throws PlatformException {

    if (in.getWorkflow() == null || (in.getWorkflow() == null))
      throw new PlatformException("Workflow of elements are null");

    if (in.getWorkflow() != out.getWorkflow())
      throw new PlatformException("Workflow of elements aren't the same");

    setFrom(in);
    setTo(out);
    link();
  }

  /**
   * Public constructor.
   * @param from From element of the link
   * @param outReference Identifier of Out element of the link
   * @throws PlatformException if the link can'be created
   */
  WorkflowLink(final WorkflowElement from, final String outReference)
      throws PlatformException {

    if (outReference == null || "".equals(outReference))
      throw new PlatformException("Workflow of elements are null");

    setFrom(from);
    setToReference(outReference);
    this.referenceMode = true;
    link();

  }

}