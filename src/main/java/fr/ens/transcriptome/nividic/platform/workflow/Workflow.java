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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.ModuleLocation;
import fr.ens.transcriptome.nividic.util.StringUtils;
import fr.ens.transcriptome.nividic.util.Version;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;

/**
 * This class handle a workflow.
 * @author Laurent Jourdren
 */
public final class Workflow implements Runnable, AlgorithmListener {

  private String name;
  private String type;
  private Version version;
  private String description;
  private String generator;
  private String author;
  private String organisation;
  private Properties annotations = new Properties();
  private String firstElementName;
  private Map elements = new HashMap();
  private ArrayList listeners = new ArrayList();
  private String arguments;
  private boolean threadRunning;

  //
  // Getters
  //

  /**
   * Get the author of the workflow.
   * @return The author of the workflow
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Get the description of the workflow.
   * @return The description of the workflow
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get the name of the workflow.
   * @return The name of the wokflow
   */
  public String getName() {
    return name;
  }

  /**
   * Get the id of next element of the workflow.
   * @return The next element of the workflow
   */
  public String getRootElementName() {
    return firstElementName;
  }

  /**
   * Get the generator of the workflow.
   * @return The generator of the workflow
   */
  public String getGenerator() {
    return generator;
  }

  /**
   * Get the organisation of the authors of workflow
   * @return The organisation of the authors of workflow
   */
  public String getOrganisation() {
    return organisation;
  }

  /**
   * Get the number of element in the workflow.
   * @return The number of the element in the workflow
   */
  public int size() {
    return this.elements.size();
  }

  /**
   * Get the arguments of the workflows.
   * @return Returns the arguments
   */
  public String getArguments() {
    return arguments;
  }

  /**
   * Test if the thread is running.
   * @return Returns the threadRunning
   */
  public boolean isThreadRunning() {
    return threadRunning;
  }

  /**
   * Get the annotations of the workflow.
   * @return Returns the annotations
   */
  public Properties getAnnotations() {
    return annotations;
  }

  /**
   * Get the type of the workflow.
   * @return Returns the type
   */
  public String getType() {
    return type;
  }

  /**
   * Get the version fo the workflow.
   * @return Returns the version
   */
  public Version getVersion() {
    return version;
  }

  //
  // Setters
  //

  /**
   * Set the authors of the workflow
   * @param author Author of the workflow
   */
  public void setAuthor(final String author) {
    this.author = author;
  }

  /**
   * Set the description of the workflow.
   * @param description Description of the workflow
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * Set the name of the workflow.
   * @param name The name of the workflow
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Set the id of next element of the workflow.
   * @param element The next element of the workflow
   */
  public void setRootElementName(final String element) {
    firstElementName = element;
  }

  /**
   * Set the id of next element of the workflow.
   * @param element The next element of the workflow
   * @throws PlatformException if an error occurs while adding the element
   */
  public void setRootElement(final WorkflowElement element)
      throws PlatformException {
    addElement(element);
    firstElementName = element.getId();
  }

  /**
   * Set the name of the generator of the workflow
   * @param generator The name of the generator
   */
  public void setGenerator(final String generator) {
    this.generator = generator;
  }

  /**
   * Set the organisation of the authors of workflow
   * @param organisation The organisation of the authors of workflow
   */
  public void setOrganisation(final String organisation) {
    this.organisation = organisation;
  }

  /**
   * Set the arguments of the threads.
   * @param arguments The arguments to set
   */
  public void setArguments(final String arguments) {
    this.arguments = arguments;
  }

  /**
   * Set if the thread is running.
   * @param threadRunning The threadRunning to set
   */
  private void setThreadRunning(final boolean threadRunning) {
    this.threadRunning = threadRunning;
  }

  /**
   * Set the type of the workflow.
   * @param type The type to set
   */
  public void setType(final String type) {
    this.type = type;
  }

  /**
   * Set the version of the workflow.
   * @param version The version to set
   */
  public void setVersion(final Version version) {
    this.version = version;
  }

  //
  // Other mehods
  //

  /**
   * Return the first element of the workflow if exists.
   * @return The first element of the workflow
   */
  public WorkflowElement getRootElement() {

    final String fen = getRootElementName();

    if (fen == null)
      return null;

    if (!this.elements.containsKey(fen))
      return null;
    return (WorkflowElement) this.elements.get(fen);
  }

  /**
   * Add a Workflow element to the workflow.
   * @param element Workflow element to add
   * @throws PlatformException if element is null or if element is already
   *                 a part of another workflow
   */
  public void addElement(final WorkflowElement element)
      throws PlatformException {

    if (element == null)
      throw new PlatformException("element is null");

    if (element.getWorkflow() != null && element.getWorkflow() != this)
      throw new PlatformException("This workflow element is "
          + "already a part of another workflow");

    // create a new instance of the algorithm
    if (!element.isAlgorithmInstance())
      element.newAlgorithmInstance();

    element.setWorkflow(this);

    this.elements.put(element.getId(), element);

    // send the event to all listeners
    sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.ADD_EVENT, element,
        "add element"));
  }

  /**
   * Get a workflow element.
   * @param elementName The name of the workflow element to get
   * @return a workflow element
   */
  public WorkflowElement getElement(final String elementName) {
    if (elementName == null)
      return null;
    return (WorkflowElement) this.elements.get(elementName);
  }

  /**
   * Remove a workflow element.
   * @param elementName The name of the workflow element to remove
   * @throws PlatformException if element is null
   */
  public void removeElement(final String elementName)
      throws PlatformException {

    if (elementName == null || !this.elements.containsKey(elementName))
      throw new PlatformException("element is null");

    final WorkflowElement wfe = this.getElement(elementName);
    removeElement(wfe);
  }

  /**
   * Remove a workflow element.
   * @param element The name of the workflow element to remove
   * @throws PlatformException if element is null
   */
  public void removeElement(final WorkflowElement element)
      throws PlatformException {

    if (element == null)
      throw new PlatformException("element is null");

    if (element.getWorkflow() != this)
      throw new PlatformException("This workflow element is "
          + "already a part of another workflow");

    WorkflowElement[] previous = element.getPreviousElements();
    WorkflowElement[] nexts = element.getNextElements();

    for (int i = 0; i < previous.length; i++)
      unlink(previous[i], element);

    for (int i = 0; i < nexts.length; i++)
      unlink(element, nexts[i]);

    this.elements.remove(element.getId());

    //  send the event to all listeners
    sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.REMOVE_EVENT,
        element, "remove element"));

  }

  /**
   * Remove a workflow element.
   * @param element The name of the workflow element to remove
   * @throws PlatformException if element is null
   */
  public void removeElementAndJoinPreviousAndNextElements(
      final WorkflowElement element) throws PlatformException {

    if (element == null)
      throw new PlatformException("element is null");

    if (element.getWorkflow() != this)
      throw new PlatformException("This workflow element is "
          + "already a part of another workflow");

    WorkflowElement[] previous = element.getPreviousElements();
    WorkflowElement[] nexts = element.getNextElements();

    removeElement(element);

    for (int i = 0; i < previous.length; i++)
      for (int j = 0; j < nexts.length; j++)
        link(previous[i], nexts[j]);

  }

  /**
   * Replace a workflow element.
   * @param elementToReplace The name element to replace
   * @param element The new Element
   * @throws PlatformException if element is null
   */
  public void replaceElement(final WorkflowElement elementToReplace,
      final WorkflowElement element) throws PlatformException {

    if (element == null || elementToReplace == null)
      throw new PlatformException("one or more element is null");

    if (element.getWorkflow() == null)
      addElement(element);

    if (elementToReplace.getWorkflow() != this || element.getWorkflow() != this)
      throw new PlatformException("one or more element is  "
          + "already a part of another workflow");

    WorkflowElement[] previous = elementToReplace.getPreviousElements();
    WorkflowElement[] nexts = elementToReplace.getNextElements();

    for (int i = 0; i < previous.length; i++) {
      unlink(previous[i], elementToReplace);
      link(previous[i], element);
    }
    for (int i = 0; i < nexts.length; i++) {
      unlink(elementToReplace, nexts[i]);
      link(element, nexts[i]);
    }
  }

  /**
   * Replace a workflow element.
   * @param elementToReplace The name element to replace
   * @param element The new Element
   * @throws PlatformException if element is null
   */
  public void replaceAndRemoveElement(final WorkflowElement elementToReplace,
      final WorkflowElement element) throws PlatformException {
    replaceElement(elementToReplace, element);
    removeElement(elementToReplace);
  }

  /**
   * Insert a element after another
   * @param element Reference element
   * @param elementToInsert Element to insert
   * @throws PlatformException if an error occurs while inserting the
   *                 element
   */
  public void insertAfter(final WorkflowElement element,
      final WorkflowElement elementToInsert) throws PlatformException {

    if (element == null || elementToInsert == null)
      throw new PlatformException("one or more element is null");

    if (elementToInsert.getWorkflow() == null)
      addElement(elementToInsert);

    if (elementToInsert.getWorkflow() != this || element.getWorkflow() != this)
      throw new PlatformException("one or more element is  "
          + "already a part of another workflow");

    WorkflowElement[] nexts = element.getNextElements();

    for (int i = 0; i < nexts.length; i++) {

      unlink(element, nexts[i]);
      link(elementToInsert, nexts[i]);
    }

    link(element, elementToInsert);

  }

  /**
   * Insert a element before another
   * @param element Reference element
   * @param elementToInsert Element to insert
   * @throws PlatformException if an error occurs while inserting the
   *                 element
   */
  public void insertBefore(final WorkflowElement element,
      final WorkflowElement elementToInsert) throws PlatformException {

    if (element == null || elementToInsert == null)
      throw new PlatformException("one or more element is null");

    if (elementToInsert.getWorkflow() == null)
      addElement(elementToInsert);

    if (elementToInsert.getWorkflow() != this || element.getWorkflow() != this)
      throw new PlatformException("one or more element is  "
          + "already a part of another workflow");

    WorkflowElement[] previous = element.getPreviousElements();

    for (int i = 0; i < previous.length; i++) {
      unlink(previous[i], element);
      link(previous[i], elementToInsert);
    }

    link(elementToInsert, element);
  }

  /**
   * Get the name of the workflow elements.
   * @return An array of workflow elements
   */
  public String[] getElementsNames() {

    final int size = this.elements.size();

    String[] result = new String[size];
    Iterator it = this.elements.keySet().iterator();
    int i = 0;
    while (it.hasNext())
      result[i++] = (String) it.next();

    return result;
  }

  /**
   * Get the workflow elements.
   * @return An array of workflow elements
   */
  public WorkflowElement[] getElements() {

    final int size = this.elements.size();

    if (size == 0)
      return null;

    WorkflowElement[] result = new WorkflowElement[size];
    Iterator it = this.elements.values().iterator();
    int i = 0;
    while (it.hasNext())
      result[i++] = (WorkflowElement) it.next();

    return result;

  }

  /**
   * Test if the workflow contains a element.
   * @param wfe Workflow element to test.
   * @return true if the workflow contains the element
   */
  public boolean contains(final WorkflowElement wfe) {

    return wfe != null && wfe.getWorkflow() == this
        && this.elements.containsKey(wfe.getId());
  }

  /**
   * Test if the workflow contains a element with the specifed identifer.
   * @param id Identifier to test.
   * @return true if the workflow contains the element
   */
  public boolean contains(final String id) {

    return this.elements.containsKey(id);
  }

  /**
   * Link two element
   * @param in First element to link
   * @param out Second element to link
   * @throws PlatformException if an error occurs while link elements
   */
  public void link(final WorkflowElement in, final WorkflowElement out)
      throws PlatformException {
    final WorkflowLink wfl = new WorkflowLink(in, out);

    // send the event to all listeners
    sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.LINK_EVENT, wfl,
        "new link"));
  }

  /**
   * Link two element
   * @param in First element to link
   * @param out Seconde element to link
   * @throws PlatformException if an error occurs while link elements
   */
  public void unlink(final WorkflowElement in, final WorkflowElement out)
      throws PlatformException {
    if (!(contains(in) && contains(out)))
      throw new PlatformException("This element can't be unlinked");

    String id = WorkflowLink.getId(in, out);

    WorkflowLink wfl = in.getNextElementLink(id);
    if (wfl == null)
      throw new PlatformException("The link doesn't exists");
    wfl.unlink();

    // send the event to all listeners
    sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.UNLINK_EVENT, wfl,
        "unlink"));
  }

  /*
   * Replace all the links which use reference for the out element by link which
   * use WorkflowElement objects. @throws PlatformException if an error
   * occurs while replacing references
   */
  /*
   * public void replaceAllLinkReferences() throws PlatformException {
   * String[] e = getElementsNames(); // Create instances of the algorithms for
   * (int i = 0; i < e.length; i++) { final WorkflowElement wfe =
   * getElement(e[i]); wfe.replaceAllLinkReferences(); } }
   */

  /**
   * Activate the workflow.
   * @throws PlatformException if an error occurs while activate the
   *                 workflow
   */
  public void activate() throws PlatformException {

    String[] e = getElementsNames();

    //  Create instances of the algorithms
    for (int i = 0; i < e.length; i++) {
      final WorkflowElement wfe = getElement(e[i]);
      wfe.activate();
    }
  }

  /**
   * Add a listener.
   * @param listener Listener to add.
   */
  public void addListener(final WorkflowListener listener) {

    if (listener == null || this.listeners.contains(listener))
      return;

    this.listeners.add(listener);
  }

  /**
   * Remove a listener.
   * @param listener Listener to remove
   */
  public void removeListener(final WorkflowListener listener) {
    if (listener == null || !this.listeners.contains(listener))
      return;

    this.listeners.remove(listener);
  }

  /**
   * Send event to all the listeners.
   * @param event a AlgorithmEvent object
   */
  private void sendEvent(final WorkflowEvent event) {

    if (event == null)
      return;

    Iterator it = this.listeners.iterator();
    while (it.hasNext())
      ((WorkflowListener) it.next()).workflowStateChanged(event);
  }

  //
  // Workflow commands
  //

  /**
   * Start the workflow.
   * @param args The arguments of the workflow
   * @throws PlatformException if an error occurs while running the
   *                 workflow
   */
  private void start(final String args) throws PlatformException {

    final String fen = getRootElementName();

    if (fen == null)
      throw new PlatformException(
          "no name for the first element of the workflow");

    if (!this.elements.containsKey(fen))
      throw new PlatformException("unknow first element of the workflow");

    WorkflowElement wfe = (WorkflowElement) this.elements.get(fen);

    if (wfe.getAlgorithm() == null)
      throw new PlatformException("the workflow is not activated");

    if (wfe.getAlgorithmType() != ModuleLocation.MODULE_TYPE_ALGORITHM)
      throw new PlatformException(
          "first element of the workflow must be a startAlgorithm");

    sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.START_EVENT, this,
        "start"));
    try {
      wfe.getAlgorithm().startWorkflow(args);
    } catch (ParameterException e) {
      sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.END_EVENT, this,
          "stop"));
      throw new PlatformException("Wrong workflow arguments");
    }
    sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.END_EVENT, this,
        "stop"));
  }

  /**
   * Stop all elements of the workflow.
   * @throws PlatformException if no workflow running
   */
  public void stop() throws PlatformException {

    if (!isThreadRunning())
      throw new PlatformException("No workflow running");

    sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.ELEMENTS_STOP_EVENT,
        this, "stop"));
  }

  /**
   * Pause all elements of the workflow.
   * @throws PlatformException if no workflow running
   */
  public void pause() throws PlatformException {

    if (!isThreadRunning())
      throw new PlatformException("No workflow running");

    sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.ELEMENTS_PAUSE_EVENT,
        this, "pause"));
  }

  /**
   * Resume all elements of the workflow.
   * @throws PlatformException if no workflow running
   */
  public void resume() throws PlatformException {

    if (!isThreadRunning())
      throw new PlatformException("No workflow running");

    sendEvent(new SimpleWorkflowEvent(this,
        WorkflowEvent.ELEMENTS_RESUME_EVENT, this, "resume"));
  }

  /**
   * Handle changes of the algorithms of the workflow.
   * @param event Event to handle
   */
  public void algorithmStateChanged(final AlgorithmEvent event) {

    if (event == null)
      return;

    switch (event.getId()) {
    case AlgorithmEvent.START_EVENT:
      sendEvent(new SimpleWorkflowEvent(this,
          WorkflowEvent.START_ALGORITHM_EVENT, event.getSource(),
          "workflow starts an algorithm"));
      break;

    case AlgorithmEvent.END_EVENT:
      sendEvent(new SimpleWorkflowEvent(this,
          WorkflowEvent.END_ALGORITHM_EVENT, event.getSource(),
          "workflow ends an algorithm"));
      break;

    case AlgorithmEvent.NO_MORE_ALGORITHM_TO_EXECUTE:
      sendEvent(new SimpleWorkflowEvent(this, WorkflowEvent.END_EVENT, null,
          "end of the workflow"));
      break;

    default:
      break;
    }
  }

  //
  // Thread methods
  //

  /**
   * Run method.
   */
  public void run() {

    setThreadRunning(true);

    try {
      start(getArguments());
    } catch (PlatformException e) {
      setThreadRunning(false);

      Iterator it = this.listeners.iterator();
      while (it.hasNext())
        ((WorkflowListener) it.next()).workflowNewException(e);
    }

    setThreadRunning(false);
  }

  //
  // Constructors
  //

  /**
   * Public constructor.
   */
  public Workflow() {
    setName(StringUtils.getRandomIdentifier("workflow"));
  }

  /**
   * Public constructor.
   * @param name The name of the workflow
   */
  public Workflow(final String name) {
    setName(name);
  }

}