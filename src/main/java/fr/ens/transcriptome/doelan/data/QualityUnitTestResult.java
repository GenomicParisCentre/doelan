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

import fr.ens.transcriptome.doelan.algorithms.QualityUnitTest;
import fr.ens.transcriptome.nividic.om.BioAssay;

/**
 * This class define a result from a QualityUnitTest
 * @author Laurent Jourdren
 */
public class QualityUnitTestResult extends QualityTestResult {

  private int realFeatures;
  private boolean[] newFlags;
  private boolean filterFlags;
  private SummaryResult ch635;
  private SummaryResult ch532;
  private SummaryResult chAll;

  /**
   * This class define the result for a channel.
   * @author Laurent Jourdren
   */
  public final class SummaryResult {

    private String channel;
    private double value;
    private double threshold;
    private String unit;
    private String thresholdEqualityType = "<";
    private boolean percent;
    private boolean pass;

    //
    // Getters
    //

    /**
     * Get the channel.
     * @return Returns the channel
     */
    public String getChannel() {
      return channel;
    }

    /**
     * Test if the test pass.
     * @return Returns the pass
     */
    public boolean isPass() {
      return pass;
    }

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
     * Get the type of equality between the threshold and this value. For
     * example: "=", " <", ">", " <=", ">=", "!="... Default value is " <".
     * @return the type of equality of the parameter.
     */
    public String getThresholdEqualityType() {
      return this.thresholdEqualityType;
    }

    //
    // Setters
    //

    /**
     * Set the channel.
     * @param channel The channel to set
     */
    private void setChannel(final String channel) {
      this.channel = channel;
    }

    /**
     * Set if the test pass.
     * @param pass The pass to set
     */
    public void setPass(final boolean pass) {
      this.pass = pass;
    }

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

    private SummaryResult(final String channel) {
      setChannel(channel);
    }

  }

  //
  // Getters
  //

  /**
   * Get the new flags of the bioAssay after the test.
   * @return The new flags after the test
   */
  public boolean[] getNewFlags() {
    return newFlags;
  }

  /**
   * Get the result for 532 channel.
   * @return Returns the ch532
   */
  public SummaryResult getResultChannel532() {
    return ch532;
  }

  /**
   * Get the result for 635 channel.
   * @return Returns the ch635
   */
  public SummaryResult getResultChannel635() {
    return ch635;
  }

  /**
   * Get the result for all channel.
   * @return Returns the chAll
   */
  public SummaryResult getResultAllChannels() {
    return chAll;
  }

  /**
   * Get the count of the realFeatures (not abscent).
   * @return Returns the realFeatures
   */
  public int getRealFeatures() {
    return realFeatures;
  }

  /**
   * Test new flags must be filtered.
   * @return Returns the filterFlags
   */
  public boolean isFilterFlags() {
    return filterFlags;
  }

  //
  // Setters
  //

  /**
   * Set the new flags values after the test.
   * @param flagsValues The new flags values
   */
  public void setNewFlags(final boolean[] flagsValues) {
    newFlags = flagsValues;
  }

  /**
   * Set the count of the realFeatures (not abscent).
   * @param realFeatures The realFeatures to set
   */
  public void setRealFeatures(final int realFeatures) {
    this.realFeatures = realFeatures;
  }

  //
  // Other methods
  //

  /**
   * Set if there is a result for each channel.
   * @param global true if there is no result for each channel
   */
  public void setGlobalResultType(final boolean global) {
    if (global) {
      this.ch532 = null;
      this.ch635 = null;
      this.chAll = new SummaryResult("all");
    } else {
      this.ch532 = new SummaryResult("532");
      this.ch635 = new SummaryResult("635");
      this.chAll = null;
    }
  }

  /**
   * Test if there is a result for each channel.
   * @return true is there only a global result
   */
  public boolean isGlobalResult() {
    return this.chAll != null;
  }

  /**
   * test is the test passed.
   * @return true if the test passed
   */
  public boolean getResult() {
    if (isGlobalResult())
      return getResultAllChannels().isPass();

    return getResultChannel532().isPass() && getResultChannel635().isPass();
  }

  /**
   * Set if the new flags must be filtered.
   * @param filterFlags The filterFlags to set
   */
  public void setFilterFlags(final boolean filterFlags) {
    this.filterFlags = filterFlags;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param bioassay The bioassay used for the test
   * @param test The test used
   */
  public QualityUnitTestResult(final BioAssay bioassay,
      final QualityUnitTest test) {
    setBioAssay(bioassay);
    setParameters(test.getParameters());
    setTestId(test.getId());
    setTestType(test.aboutModule().getName());
    setTestDescription(test.aboutModule().getShortDescription());
  }

}