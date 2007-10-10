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

import org.apache.log4j.Logger;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.util.parameter.FixedParameters;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * Define an abstract algorithm.
 * @author Laurent Jourdren
 */
public abstract class Algorithm implements WorkflowListener {

  //For log system
  private Logger log = Logger.getLogger(this.getClass());

  private static int count;
  private int instanceId = count++;
  private Parameters parameters;
  private boolean parameterDefined;
  private String id = "Algorithm #" + instanceId;
  private ArrayList listeners = new ArrayList();
  private boolean pause;
  private boolean stop;

  private Algorithm algo = this;

  /** Flag to check if the init method has be done. */
  private boolean initDone;

  /** Input pad. */
  private final PadOut out = new PadOut();

  /** output pad. */
  private final GenericPadIn in = new GenericPadIn();

  private class GenericPadIn extends PadIn {

    /**
     * Start the workflow if this element is the first element of the workflow.
     * @param args arguments of the workflow
     * @throws ParameterException if an error occurs while running the worklflow
     * @throws PlatformException if bad arguments
     */
    private void start(final String args) throws ParameterException,
        PlatformException {

      getBuffer().add(new Container());
      Parameters parameters = getParameters();
      Parameter p = parameters.getParameter("args");
      if (p != null)
        if (args != null)
          p.setValue(args);
        else
          p.setValue("");

      start();
    }

    /**
     * Run the algorithm.
     */
    protected final void start() throws PlatformException {

      if (!initDone) {
        init();
        initDone = true;
      }

      //if (this.on)
      //return;
      while (!this.getBuffer().isEmpty()) {
        Container c = (Container) this.getBuffer().remove();

        if (checkStopAndPause())
          return;

        // Send information to listener of the algorithm
        algo.sendEvent(new SimpleAlgorithmEvent(algo,
            AlgorithmEvent.START_EVENT, c.getId()));

        doIt(c, getParameters());

        // Send information to listener of the algorithm
        algo.sendEvent(new SimpleAlgorithmEvent(algo, AlgorithmEvent.END_EVENT,
            c.getId()));

        if (checkStopAndPause())
          return;

        if (out().getNumberPadIns() == 0)
          sendEvent(new SimpleAlgorithmEvent(algo,
              AlgorithmEvent.NO_MORE_ALGORITHM_TO_EXECUTE, c.getId()));
        out().notifyContainer(c);
      }

    }

    /**
     * Check the state of the workflow and execute the request event.
     * @return true if the workflow must be stop
     */
    private boolean checkStopAndPause() {

      if (isStop()) {
        setStop(false);
        return true;
      }

      if (isPause()) {

        while (isPause()) {
          Thread.yield();
        }
      }

      return false;
    }

  };

  //
  // Getters
  //

  /**
   * Get the identifier of instance the algorithm
   * @return The identifier of instance the algorithm
   */
  public int getInstanceId() {
    return instanceId;
  }

  /**
   * Get the parameters of the algorithm.
   * @return The parameters of the algorithm
   */
  public final Parameters getParameters() {

    if (!parameterDefined) {
      this.parameters = defineParameters();
      if (this.parameters == null) {
        this.parameters = new FixedParameters();
      }

      this.parameterDefined = true;
    }

    return parameters;
  }

  /**
   * Get the identifier of the algorithm.
   * @return The identifier of the algorithm
   */
  public final String getId() {
    return id;
  }

  /**
   * Test if pause is activated.
   * @return Returns the pause
   */
  public boolean isPause() {
    return pause;
  }

  /**
   * Test if stop is activated.
   * @return Returns the stop
   */
  public boolean isStop() {
    return stop;
  }

  /**
   * Set the stop flag.
   * @param value Value to set.
   */
  private void setStop(final boolean value) {
    this.stop = value;
  }

  /**
   * Get the logger of the algorithm.
   * @return The logger of the algorithm
   */
  public Logger getLogger() {
    return this.log;
  }

  //
  // Setters
  //

  /**
   * Set the identifier of the algorithm.
   * @param id The identifier of the algorithm
   */
  final void setId(final String id) {
    this.id = id;
  }

  /**
   * Set the parameters of the algorithm.
   */
  protected abstract Parameters defineParameters();

  /**
   * This method contains all the code to manipulate the container <b>c </b> in
   * this element.
   * @param c The container to be manipulated
   * @param parameters Parameters of the algorithm
   * @throws PlatformException
   */
  protected abstract void doIt(final Container c, final Parameters parameters)
      throws PlatformException;

  //
  // Pads
  //

  /**
   * Get the padIn.
   * @return The padIn
   */
  public final PadIn in() {
    return in;
  }

  /**
   * Get the padOut.
   * @return The padout
   */
  public final PadOut out() {
    return out;
  }

  //
  // Abstract methods
  //

  /**
   * The init version of the algorithm.
   */
  protected void init() {
  }

  /**
   * Method to finalize the algorithm.
   */
  protected void terminate() {
  }

  //
  // Others methods
  //

  /**
   * Add a listener.
   * @param listener Listener to add.
   */
  public void addListener(final AlgorithmListener listener) {

    if (listener == null || this.listeners.contains(listener))
      return;
    this.listeners.add(listener);
  }

  /**
   * Remove a listener.
   * @param listener Listener to remove
   */
  public void removeListener(final AlgorithmListener listener) {
    if (listener == null || !this.listeners.contains(listener))
      return;

    this.listeners.remove(listener);
  }

  /**
   * Send event to all the listeners.
   * @param event a AlgorithmEvent object
   */
  protected void sendEvent(final AlgorithmEvent event) {

    if (event == null)
      return;

    Iterator it = this.listeners.iterator();
    while (it.hasNext())
      ((AlgorithmListener) it.next()).algorithmStateChanged(event);
  }

  /**
   * Stop execution of new data.
   */
  private void stop() {
    setStop(true);
  }

  /**
   * Pause execution of new data.
   */
  private void pause() {
    this.pause = true;
  }

  /**
   * Resume execution of new data.
   */
  private void resume() {
    this.pause = false;
  }

  /**
   * Start the workflow, this element must be the first element of the workflow.
   * @param args Arguments of the workflow
   * @throws PlatformException if an error occurs while running the
   *                 workflow
   * @throws ParameterException if bad arguments
   */
  final void startWorkflow(final String args) throws PlatformException,
      ParameterException {
    this.in.start(args);
  }

  //
  // Workflow Listener
  //

  /**
   * Throws an execption to a listener.
   * @param e Exception to throw.
   */
  public void workflowNewException(final PlatformException e) {
  }

  /**
   * Invoked when the target of the listener has changed its state.
   * @param event a WorkflowEvent object
   */
  public void workflowStateChanged(final WorkflowEvent event) {

    if (event == null)
      return;

    switch (event.getId()) {

    case WorkflowEvent.ELEMENTS_STOP_EVENT:
      stop();
      break;

    case WorkflowEvent.ELEMENTS_PAUSE_EVENT:
      pause();
      break;

    case WorkflowEvent.ELEMENTS_RESUME_EVENT:
      resume();
      break;

    default:
      break;
    }

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException if the name or the version of the element is
   *                 <b>null </b>
   */
  public Algorithm() throws PlatformException {
  }

}