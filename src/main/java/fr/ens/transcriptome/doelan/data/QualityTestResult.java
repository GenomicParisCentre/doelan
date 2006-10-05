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

import java.awt.Image;

import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This clas is the abstract superclass of QualityUnitTest and QualityGlobalUnit
 * @author Laurent Jourdren
 */
public abstract class QualityTestResult {

  private static int count;

  private int id = count++;
  private Parameters parameters;
  private String message;
  private String testType;
  private String testId;
  private String testDescription;
  private Image image;
  private boolean result;
  private String bioassay;
  private boolean error;
  private String errorMessage;

  //
  // Getters
  //

  /**
   * Get identifier of the result.
   * @return the identifier of the result
   */
  public int getId() {
    return id;
  }

  /**
   * Get message of the test.
   * @return The message from the test
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get Test description.
   * @return The description of the test
   */
  public String getTestDescription() {
    return testDescription;
  }

  /**
   * Get the parameters
   * @return The parameters of the test
   */
  public Parameters getParameters() {
    return parameters;
  }

  /**
   * Get the image.
   * @return Returns the image
   */
  public Image getImage() {
    return image;
  }

  /**
   * Get the result
   * @return Returns the result
   */
  public boolean getResult() {
    return result;
  }

  /**
   * The identifier of the test.
   * @return The identifier of the test
   */
  public String getTestId() {
    return testId;
  }

  /**
   * Get the type of the test
   * @return The type of the test
   */
  public String getTestType() {
    return testType;
  }

  /**
   * Get the bioassay reference.
   * @return The bioassay reference
   */
  public String getBioAssay() {
    return bioassay;
  }

  /**
   * Return true if there is an error while executing the test.
   * @return true if there is an error while executing the test
   */
  public boolean isError() {
    return error;
  }

  /**
   * Get the message of the error.
   * @return Returns the error message
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  //
  // Setters
  //

  /**
   * Set the message of the test.
   * @param message Message to set
   */
  public void setMessage(final String message) {
    this.message = message;
  }

  /**
   * Set Test description.
   * @param description The description of the test
   */
  public void setTestDescription(final String description) {
    this.testDescription = description;
  }

  /**
   * Set the parameters of the test.
   * @param parameters The parameters of the test
   */
  public void setParameters(final Parameters parameters) {
    this.parameters = parameters;
  }

  /**
   * Set the image.
   * @param image The image to set
   */
  public void setImage(final Image image) {
    this.image = image;
  }

  /**
   * Set the result
   * @param result The result to set
   */
  public void setResult(final boolean result) {
    this.result = result;
  }

  /**
   * Set the identifier of the test.
   * @param id The identifier of the test
   */
  public void setTestId(final String id) {
    testId = id;
  }

  /**
   * Set the type of the test.
   * @param type The type of the test
   */
  public void setTestType(final String type) {
    testType = type;
  }

  /**
   * Set if there is an error while executing the test
   * @param error The error
   */
  public void setError(final boolean error) {
    this.error = error;
  }

  /**
   * Set the error message.
   * @param errorMessage The errorMessage to set
   */
  public void setErrorMessage(final String errorMessage) {
    this.errorMessage = errorMessage;
  }

  /**
   * Set the bioassay reference.
   * @param reference The reference of the bioassay
   */
  public void setBioAssay(final String reference) {
    bioassay = reference;
  }

  /**
   * Set the bioassay reference.
   * @param bioassay The bioassay
   */
  public void setBioAssay(final BioAssay bioassay) {

    if (bioassay == null)
      return;
    setBioAssay(bioassay.getName());
  }

}
