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

package fr.ens.transcriptome.doelan.algorithms;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import fr.ens.transcriptome.doelan.data.DoelanDataUtils;
import fr.ens.transcriptome.doelan.data.QualityTestResult;
import fr.ens.transcriptome.doelan.data.TestSuiteResult;
import fr.ens.transcriptome.doelan.gui.CommonWindow;
import fr.ens.transcriptome.doelan.gui.ReportTabWidget;
import fr.ens.transcriptome.doelan.gui.StatusWidget;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This algorithm show in doelan GUI the status and the report of the test
 * suite.
 * @author Laurent Jourdren
 */
public class DoelanShowReport extends Algorithm implements Module {

  private ReportTabWidget report;
  private StatusWidget status;

  //
  // Getters
  //

  /**
   * Get the report widget.
   * @return Returns the report widget
   */
  public ReportTabWidget getReport() {
    return report;
  }

  /**
   * Get the status widget.
   * @return Returns the status widget
   */
  public StatusWidget getStatus() {
    return status;
  }

  //
  // Setters
  //

  /**
   * Set the report widget.
   * @param report The report widget to set
   */
  public void setReport(final ReportTabWidget report) {
    this.report = report;
  }

  /**
   * Set the status widget.
   * @param status The status widget
   */
  public void setStatus(final StatusWidget status) {
    this.status = status;
  }

  //
  // Other methods
  //

  /**
   * Get the description of the module.
   * @return The description of the module
   */
  public AboutModule aboutModule() {
    ModuleDescription md = null;
    try {
      md = new ModuleDescription("DoelanShowReport",
          "Show report of a test suite");
      md.setStability(AboutModule.STATE_STABLE);
    } catch (PlatformException e) {
      getLogger().error("Unable to create the module description");
    }
    return md;
  }

  /**
   * Set the parameters of the element.
   */
  protected Parameters defineParameters() {
    return null;
  }

  /**
   * This method contains all the code to manipulate the container <b>c </b> in
   * this element.
   * @param c The container to be manipulated
   * @param parameters Parameters of the elements
   * @throws PlatformException if an error occurs while showing the results
   */
  protected void doIt(final Container c, final Parameters parameters)
      throws PlatformException {

    if (getReport() == null || getStatus() == null)
      return;

    // Get test suite result
    TestSuiteResult testSuiteResult = DoelanDataUtils.getTestSuiteResult(c);

    if (testSuiteResult == null)
      return;

    // New array list
    getReport().setGalData(testSuiteResult.getNewArrayList());

    Map mapImages = new HashMap();

    if (testSuiteResult.getResult())
      getStatus().setStatus(StatusWidget.STATUS_PASS);
    else
      getStatus().setStatus(StatusWidget.STATUS_FAILLED);

    // Set images

    QualityTestResult[] results = DoelanDataUtils.getQualityTestResults(c);
    for (int i = 0; i < results.length; i++) {
      if (results[i].getImage() != null)
        mapImages.put(results[i].getTestId() + ".jpeg", results[i].getImage());
    }

    // HTML text

    mapImages.put("arrayplot.jpeg", testSuiteResult.getArrayPlot());

    //  Add the logo
    URL url = CommonWindow.class.getResource("/files/doelan-200.png");
    ImageIcon ii = new ImageIcon(url);
    BufferedImage buffuredImage = new BufferedImage(ii.getIconWidth(), ii
        .getIconHeight(), BufferedImage.TYPE_INT_BGR);
    Graphics g = buffuredImage.createGraphics();
    g.drawImage(ii.getImage(), 0, 0, null);

    mapImages.put("logo.jpeg", buffuredImage);

    if (mapImages.size() == 0)
      getReport().setImages(null);
    else
      getReport().setImages(mapImages);

    getReport().setText(testSuiteResult.getHtmlReport());

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *                 <b>null </b>.
   */
  public DoelanShowReport() throws PlatformException {
    // MUST BE EMPTY
  }

}