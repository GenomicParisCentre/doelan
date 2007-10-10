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
import java.util.List;
import java.util.Map;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleLocation;
import fr.ens.transcriptome.nividic.platform.module.ModuleManager;
import fr.ens.transcriptome.nividic.platform.module.ModuleQuery;
import fr.ens.transcriptome.nividic.util.StringUtils;
import fr.ens.transcriptome.nividic.util.parameter.NonFixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class discribe a workflow element.
 * @author Laurent Jourdren
 */
public final class WorkflowElement {

  private Workflow workflow;
  private static int count;
  private int instanceId = count++;
  private String id;
  private String comment;
  private ModuleQuery moduleQuery;
  private ModuleLocation moduleLocation;
  private Algorithm algorithm;

  private Parameters parameters = new NonFixedParameters();

  private Map<String, WorkflowLink> nextLinks = new HashMap<String, WorkflowLink>();
  private Map<String, WorkflowLink> previousLinks = new HashMap<String, WorkflowLink>();

  //
  // Getters
  //

  /**
   * Get the identifier of instance the element
   * @return The identifier of instance the element
   */
  public int getInstanceId() {
    return instanceId;
  }

  /**
   * Get the id of the Workflow element
   * @return The id of the Workflow element
   */
  public String getId() {
    return id;
  }

  /**
   * Get the comment about this workflow element.
   * @return The comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * Get the parameters of the workflow element.
   * @return The parameters of the workflow element.
   */
  public Parameters getParameters() {
    if (getAlgorithm() == null)
      return this.parameters;

    return getAlgorithm().getParameters();
  }

  /**
   * Get the workflow of this workflow element
   * @return The workflow of this workflow element
   */
  public Workflow getWorkflow() {
    return workflow;
  }

  /**
   * Get the algorithm
   * @return the algorithm
   */
  public Algorithm getAlgorithm() {
    return algorithm;
  }

  /**
   * Get the module Query.
   * @return The module query
   */
  public ModuleQuery getModuleQuery() {
    return moduleQuery;
  }

  /**
   * Get the module location.
   * @return The module location
   */
  ModuleLocation getModuleLocation() {
    return moduleLocation;
  }

  //
  // Setters
  //

  /**
   * Set the id of the workflow element.
   * @param id The id of the workflow element
   */
  public void setId(final String id) {
    this.id = id;
    if (isAlgorithmInstance())
      getAlgorithm().setId(id);
  }

  /**
   * Set the comment about the workflow element.
   * @param comment The comment
   */
  public void setComment(final String comment) {
    this.comment = comment;
  }

  /**
   * Set the parameters of the workflow element.
   * @param parameters Parameters to set
   */
  public void setParameters(final Parameters parameters) {
    this.parameters = parameters;
  }

  /**
   * Set the workflow of this element
   * @param workflow The workflow of this element
   */
  void setWorkflow(final Workflow workflow) {

    // remove listener if exists
    if (this.workflow != null && getAlgorithm() != null
        && this.workflow != workflow)
      getWorkflow().removeListener(getAlgorithm());

    this.workflow = workflow;

    // set listener if algorithm exists
    if (getAlgorithm() != null) {
      getWorkflow().addListener(getAlgorithm());
      getAlgorithm().addListener(getWorkflow());
    }
  }

  /**
   * Set the algorithm.
   * @param algorithm Algorithm to set
   */
  void setAlgorithm(final Algorithm algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Set the module query.
   * @param query The moduleQuery to set
   */
  public void setModuleQuery(final ModuleQuery query) {
    moduleQuery = query;
  }

  /**
   * Set the module location.
   * @param location The location of the module
   */
  void setModuleLocation(final ModuleLocation location) {
    moduleLocation = location;
  }

  //
  // Other methods : Algorithm management
  //

  /**
   * Test if an instance of the algorithm exists.
   * @return true if an instance of the algorithm exists
   */
  public boolean isAlgorithmInstance() {
    return getAlgorithm() != null;
  }

  /**
   * Get the type of the algorithm.
   * @return the type of the alogrithm or -1 if no instance of the algorithm
   *         exists.
   */
  public int getAlgorithmType() {

    if (this.moduleLocation == null)
      return -1;
    return this.moduleLocation.getType();
  }

  /**
   * Create a new instance of the algorithm
   */
  void newAlgorithmInstance() throws PlatformException {

    final ModuleManager mm = ModuleManager.getManager();

    // Get the location of the module
    final ModuleLocation ml = mm.getModuleLocation(getModuleQuery());

    if (ml == null)
      throw new PlatformException("Location of this module is unknow : "
          + getModuleQuery().getModuleName());
    setModuleLocation(ml);

    // test the type of the module
    if (ml.getType() <= ModuleLocation.MODULE_TYPE_DATA
        && ml.getType() > ModuleLocation.MODULE_TYPE_ALGORITHM)
      new PlatformException("Invalid module type");

    // Load the module
    Module m = mm.loadModule(ml);

    if (m == null)
      throw new PlatformException("unable to load algorithm");

    Algorithm algo = null;

    try {
      algo = (Algorithm) m;
    } catch (ClassCastException e) {
      throw new PlatformException("Invalid type of module");
    }

    // Set the parameters
    /*
     * final Parameters algoParams = algo.getParameters(); String [] paramNames =
     * getParameters().getParametersNames(); for (int i = 0; i <
     * paramNames.length; i++) { final String name = paramNames[i]; final String
     * value = getParameters().getParameter(name).getStringValue();
     * algoParams.setParameter(name,value); }
     */

    // algo.defineParameters();
    try {
      Parameters params = algo.getParameters();
      String[] names = params.getParametersNames();
      for (int i = 0; i < names.length; i++) {
        Parameter p = params.getParameter(names[i]);

        if (p.getType() == Parameter.DATATYPE_INTEGER)
          p.getIntValue();
        else if (p.getType() == Parameter.DATATYPE_DOUBLE)
          p.getDoubleValue();
        if (p.getType() == Parameter.DATATYPE_BOOLEAN)
          p.getBooleanValue();
        else if (p.getType() == Parameter.DATATYPE_STRING)
          p.getStringValue();

      }

    } catch (ParameterException e) {
      throw new PlatformException("Invalid default parameter value");
    }

    // if is the first instance of the algorithm put the parameter to it
    if (getAlgorithm() == null) {

      String[] paramNames = getParameters().getParametersNames();
      for (int i = 0; i < paramNames.length; i++) {
        final Parameter p = getParameters().getParameter(paramNames[i]);

        try {
          algo.getParameters().setParameter(p.getName(), p.getStringValue());
        } catch (ParameterException e) {
          throw new PlatformException("Invalid algorithm parameter : "
              + e.getMessage());
        }
      }
    }

    algo.setId(getId());
    setAlgorithm(algo);

    // Set listener
    if (getWorkflow() != null) {
      getWorkflow().addListener(algo);
      algo.addListener(getWorkflow());
    }
  }

  //
  // Other methods : link management
  //

  /**
   * Get a next link from from this id.
   * @param id Identifier of the next element link to get
   * @return a workflow link
   */
  WorkflowLink getNextElementLink(final String id) {
    return this.nextLinks.get(id);
  }

  /**
   * Get a previous link from from this id.
   * @param id Identifier of the previous element link to get
   * @return a workflow link
   */
  WorkflowLink getPreviousElementLink(final String id) {
    return this.previousLinks.get(id);
  }

  /**
   * Return the identifers of the next elements.
   * @return The identifiers of the nexts elements.
   */
  public String[] getNextElementsIdentifiers() {

    final Map<String, WorkflowLink> map = this.nextLinks;

    final int size = map.size();
    if (size == 0)
      return null;

    String[] result = new String[size];

    int i = 0;

    for (Map.Entry<String, WorkflowLink> e : map.entrySet()) {

      // final String key = e.getKey();
      final WorkflowLink wfl = e.getValue();
      final String ref;
      if (wfl.isReferenceMode())
        ref = wfl.getOutReference();
      else
        ref = wfl.getTo().getId();
      result[i++] = ref;
    }

    return result;
  }

  /**
   * Return the identifers of the previous elements.
   * @return The identifiers of the previous elements.
   */
  public String[] getPreviousElementsIndentifier() {

    final Map<String, WorkflowLink> map = this.previousLinks;

    final int size = map.size();
    if (size == 0)
      return null;

    String[] result = new String[size];

    int i = 0;

    for (Map.Entry<String, WorkflowLink> e : map.entrySet()) {

      WorkflowElement wfe = (e.getValue()).getFrom();
      result[i++] = wfe.getId();
    }

    return result;
  }

  /**
   * Get the next element of this element.
   * @return An array of WorkflowElement
   */
  public WorkflowElement[] getNextElements() {

    List<WorkflowElement> al = new ArrayList<WorkflowElement>();
    final Map<String, WorkflowLink> map = this.nextLinks;
    Iterator it = map.keySet().iterator();
    while (it.hasNext())
      al.add((map.get(it.next())).getTo());

    WorkflowElement[] result = new WorkflowElement[al.size()];
    al.toArray(result);

    return result;
  }

  /**
   * Get the previous element of this element.
   * @return An array of WorkflowElement
   */
  public WorkflowElement[] getPreviousElements() {

    List<WorkflowElement> al = new ArrayList<WorkflowElement>();
    final Map<String, WorkflowLink> map = this.previousLinks;
    Iterator it = map.keySet().iterator();
    while (it.hasNext())
      al.add((map.get(it.next())).getFrom());

    WorkflowElement[] result = new WorkflowElement[al.size()];
    al.toArray(result);

    return result;
  }

  /**
   * Add link to a next element.
   * @param wfl Workflow link to add
   * @throws PlatformException if an error occurs while the link is created.
   */
  void addNextElementLink(final WorkflowLink wfl) throws PlatformException {

    if (wfl == null || wfl.getId() == null)
      throw new PlatformException("link is null");
    if (this.nextLinks.containsKey(wfl.getId()))
      throw new PlatformException("link already exists");

    this.nextLinks.put(wfl.getId(), wfl);
  }

  /**
   * Add link to a previous element.
   * @param wfl Workflow link to add
   * @throws PlatformException if an error occurs while the link is created.
   */
  void addPreviousElementLink(final WorkflowLink wfl) throws PlatformException {

    if (wfl == null)
      throw new PlatformException("link is null");
    if (this.previousLinks.containsKey(wfl.getId()))
      throw new PlatformException("link already exists");

    this.previousLinks.put(wfl.getId(), wfl);
  }

  /**
   * Remove link to a next element.
   * @param wfl Workflow link to remove
   * @throws PlatformException if an error occurs while the link is removed.
   */
  void removeNextElementLink(final WorkflowLink wfl) throws PlatformException {

    if (wfl == null)
      throw new PlatformException("link is null");
    if (!this.nextLinks.containsKey(wfl.getId()))
      throw new PlatformException("link don't exists");

    this.nextLinks.remove(wfl.getId());
  }

  /**
   * Remove link to a next element.
   * @param wfl Workflow link to remove
   * @throws PlatformException if an error occurs while the link is removed.
   */
  void removePreviousElementLink(final WorkflowLink wfl)
      throws PlatformException {

    if (wfl == null)
      throw new PlatformException("link is null");
    if (!this.previousLinks.containsKey(wfl.getId()))
      throw new PlatformException("link don't exists");

    this.previousLinks.remove(wfl.getId());
  }

  /**
   * Link this element to another element.
   * @param outReference Reference of second element to link
   * @throws PlatformException if an error occurs while link elements
   */
  public void link(final String outReference) throws PlatformException {
    new WorkflowLink(this, outReference);
  }

  /**
   * Replace all the links which use reference for the out element by link which
   * use WorkflowElement objects.
   * @throws PlatformException if an error occurs while replacing references
   */
  void replaceAllLinkReferences() throws PlatformException {

    Iterator it = this.nextLinks.keySet().iterator();
    while (it.hasNext()) {
      WorkflowLink wfl = this.nextLinks.get(it.next());
      wfl.replaceReferenceByElement();
    }
  }

  /**
   * Activate all link of this element.
   * @throws PlatformException if an error occurs while activating the links
   */
  void activate() throws PlatformException {

    if (getWorkflow() == null)
      throw new PlatformException(
          "Cant activate an element outside of a workflow");

    /*
     * if (!isAlgorithmInstance()) newAlgorithmInstance();
     */

    Iterator it = this.nextLinks.keySet().iterator();
    while (it.hasNext()) {
      WorkflowLink wfl = this.nextLinks.get(it.next());
      wfl.linkAlgorithms();
    }

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public WorkflowElement() {
    setId(StringUtils.getRandomIdentifier("workflowelement"));
  }

  /**
   * Public constructor.
   * @param id The id of the workflow element
   */
  public WorkflowElement(final String id) {
    setId(id);
  }

}