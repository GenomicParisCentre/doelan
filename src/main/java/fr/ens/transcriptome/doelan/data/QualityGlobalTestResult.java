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

import fr.ens.transcriptome.doelan.algorithms.QualityGlobalTest;
import fr.ens.transcriptome.nividic.om.BioAssay;

/**
 * This class define a result from a QualityUnitTest
 * @author Laurent Jourdren
 */
public class QualityGlobalTestResult extends QualityTestResult {

  private double threshold;
  private double value;
  private String unit;
  private String thresholdEqualityType = "<";
  private boolean percent;

  //
  // Getters
  //

  /**
   * Get the Threshold.
   * @return Returns the threshold in percent
   */
  public double getThreshold() {
    return threshold;
  }

  /**
   * Get the value.
   * @return Returns the value
   */
  public double getValue() {
    return value;
  }

  /**
   * Test if the value is a percentage.
   * @return Returns the percent
   */
  public boolean isPercent() {
    return percent;
  }

  /**
   * Get the unit of the value and the threshold.
   * @return The unit of the value and the threshold
   */
  public String getUnit() {
    return this.unit;
  }

  /**
   * Get the type of equality between the threshold and this value. For example:
   * "=", " <", ">", " <=", ">=", "!="... Default value is " <".
   * @return the type of equality of the parameter.
   */
  public String getThresholdEqualityType() {
    return this.thresholdEqualityType;
  }

  //
  // Setters
  //

  /**
   * Set the threshold.
   * @param threshold The threshold to set
   */
  public void setThreshold(final double threshold) {
    this.threshold = threshold;
  }

  /**
   * Set the value.
   * @param value The value to set
   */
  public void setValue(final double value) {
    this.value = value;
  }

  /**
   * Set if the value is a percentage.
   * @param percent The percent to set
   */
  public void setPercent(final boolean percent) {
    this.percent = percent;
  }

  /**
   * Set the unit of the value and the threshold.
   * @param unit Unit to set
   */
  public void setUnit(final String unit) {
    this.unit = unit;
  }

  /**
   * Set the equality type between the the threshold and its value.
   * @param equality The equality to set.
   */
  public void setThresholdEqualityType(final String equality) {

    if ("=".equals(equality) || "!=".equals(equality) || "<".equals(equality)
        || "<=".equals(equality) || ">".equals(equality)
        || ">=".equals(equality)) {
      this.thresholdEqualityType = equality;
    }
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param bioassay The bioassay used for the test
   * @param test The test used
   */
  public QualityGlobalTestResult(final BioAssay bioassay,
      final QualityGlobalTest test) {
    setBioAssay(bioassay);
    setParameters(test.getParameters());
    setTestId(test.getId());
    setTestType(test.aboutModule().getName());
    setTestDescription(test.aboutModule().getShortDescription());
  }

}
