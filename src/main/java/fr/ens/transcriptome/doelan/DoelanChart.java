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

package fr.ens.transcriptome.doelan;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

/**
 * This class allow to generate a plot.
 * @author Laurent Jourdren
 */
public class DoelanChart {

  // For log system
  private static Logger log = Logger.getLogger(DoelanChart.class);

  private int width;
  private int height;

  private double[] data;
  private String title;
  private String xAxisLegend;
  private String yAxisLegend;
  private String histCaption;
  private boolean yLogAxis;

  private int bins = 1;
  private double threshold = -1;
  private boolean thresholdCaptionRight;
  private static final int THRESHOLD_SIZE = 11;

  //
  // Getters
  //

  /**
   * Set the number of bins
   * @return Returns the bins
   */
  public int getBins() {
    return bins;
  }

  /**
   * Get the data to plot
   * @return Returns the data
   */
  public double[] getData() {
    return data;
  }

  /**
   * Set the title of the plot.
   * @return Returns the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get the value of the threshold.
   * @return Returns the threshold
   */
  public double getThreshold() {
    return threshold;
  }

  /**
   * Test if the position of the caption for the threshold is on the right.
   * @return Returns the thresholdCaptionRight
   */
  public boolean isThresholdCaptionRight() {
    return thresholdCaptionRight;
  }

  /**
   * Get the caption of the histogram
   * @return Returns the histCaption
   */
  public String getHistCaption() {
    return histCaption;
  }

  /**
   * Get the x axis legend
   * @return Returns the xAxisLegend
   */
  public String getXAxisLegend() {
    return xAxisLegend;
  }

  /**
   * Get the y axis legend
   * @return Returns the yAxisLegend
   */
  public String getYAxisLegend() {
    return yAxisLegend;
  }

  /**
   * Get the height of the image.
   * @return Returns the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Get the width of the image.
   * @return Returns the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Set the height of the image.
   * @param height The height to set
   */
  public void setHeight(final int height) {
    this.height = height;
  }

  /**
   * Set the width of the image.
   * @param width The width to set
   */
  public void setWidth(final int width) {
    this.width = width;
  }

  /**
   * Test if the Y axis is in log scale
   * @return Returns the yLogAxis
   */
  public boolean isYLogAxis() {
    return yLogAxis;
  }

  /**
   * Set the log scale for the y axis
   * @param logAxis The yLogAxis to set
   */
  public void setYLogAxis(final boolean logAxis) {
    yLogAxis = logAxis;
  }

  //
  // Setters
  //

  /**
   * Set the number of bins
   * @param bins The bins to set
   */
  public void setBins(final int bins) {
    this.bins = bins;
  }

  /**
   * set the data
   * @param data The data to set
   */
  public void setData(final double[] data) {
    this.data = data;
  }

  /**
   * Set the title of the plot.
   * @param title The title to set
   */
  public void setTitle(final String title) {
    this.title = title;
  }

  /**
   * Set the threshold
   * @param threshold The threshold to set
   */
  public void setThreshold(final double threshold) {
    this.threshold = threshold;
  }

  /**
   * Set the position of the caption for the threshold.
   * @param thresholdCaptionRight The thresholdCaptionRight to set
   */
  public void setThresholdCaptionRight(final boolean thresholdCaptionRight) {
    this.thresholdCaptionRight = thresholdCaptionRight;
  }

  /**
   * Set the histogram caption
   * @param histCaption The histCaption to set
   */
  public void setHistCaption(final String histCaption) {
    this.histCaption = histCaption;
  }

  /**
   * Set the x axis legend
   * @param axisLegend The xAxisLegend to set
   */
  public void setXAxisLegend(final String axisLegend) {
    xAxisLegend = axisLegend;
  }

  /**
   * Get the y axis legend
   * @param axisLegend The yAxisLegend to set
   */
  public void setYAxisLegend(final String axisLegend) {
    yAxisLegend = axisLegend;
  }

  //
  // Other methods
  //

  private static double getMax(final double[] array) {

    double max = Double.MIN_VALUE;

    if (array == null)
      return max;

    for (int i = 0; i < array.length; i++)
      if (array[i] > max)
        max = array[i];

    return max;
  }

  private HistogramDataset getHistogramDataset() {

    HistogramDataset histogramdataset = new HistogramDataset();

    double max = getMax(getData());

    histogramdataset.addSeries(getHistCaption(), getData(), getBins(), 0,
        ((int) (max / 10)) * 10 + 1);

    return histogramdataset;

  }

  /**
   * Get the image
   * @return an image of the plot
   */
  public Image getImage() {

    HistogramDataset histogramdataset = getHistogramDataset();

    // create the chart...
    JFreeChart chart = ChartFactory.createHistogram(getTitle(),
    // title
        getXAxisLegend(), // domain axis label
        getYAxisLegend(), // range axis label
        histogramdataset, // data

        PlotOrientation.VERTICAL, // orientation
        true, // include legend
        true, // tooltips?
        false // URLs?
        );

    // get a reference to the plot for further customisation...
    XYPlot plot = chart.getXYPlot();

    XYItemRenderer histRenderer = plot.getRenderer();

    histRenderer.setSeriesPaint(0, ChartColor.VERY_LIGHT_BLUE);

    if (getThreshold() != -1) {

      ValueMarker valueMarker = new ValueMarker(getThreshold());

      valueMarker.setPaint(Color.red);
      valueMarker.setLabel("Threshold");
      valueMarker.setLabelFont(new Font("SansSerif", 2, THRESHOLD_SIZE));
      valueMarker.setLabelPaint(ChartColor.DARK_RED);
      valueMarker.setLabelAnchor(RectangleAnchor.TOP);

      if (isThresholdCaptionRight())
        valueMarker.setLabelTextAnchor(TextAnchor.TOP_RIGHT);
      else
        valueMarker.setLabelTextAnchor(TextAnchor.TOP_LEFT);

      valueMarker.setPaint(ChartColor.RED);
      plot.addDomainMarker(valueMarker, Layer.FOREGROUND);

      IntervalMarker intervalMarker = new IntervalMarker(0, getThreshold());

      intervalMarker.setPaint(new Color(222, 222, 255, 64));

      plot.addDomainMarker(intervalMarker, Layer.BACKGROUND);

      chart.setAntiAlias(false);

      // set the background color for the chart...
      chart.setBackgroundPaint(Color.white);

    }

    Image result = null;
    try {

      if (isYLogAxis()) {
        LogarithmicAxis yAxis = new LogarithmicAxis(getYAxisLegend());
        yAxis.setAllowNegativesFlag(true);
        plot.setRangeAxis(yAxis);
      }

      result = chart.createBufferedImage(getWidth(), getHeight());
    } catch (Error e) {
      log.error("Catch error of JFreeChart: " + e.getMessage());
    } catch (RuntimeException e) {
      log.error("Catch runtime exception of JFreeChart: " + e.getMessage());
    }

    return result;
  }
}
