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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.doelan.data.DoelanDataUtils;
import fr.ens.transcriptome.doelan.data.QualityGlobalTestResult;
import fr.ens.transcriptome.doelan.data.QualityTestResult;
import fr.ens.transcriptome.doelan.data.QualityUnitTestResult;
import fr.ens.transcriptome.doelan.data.TestSuiteResult;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.BioAssayUtils;
import fr.ens.transcriptome.nividic.om.GenepixArrayList;
import fr.ens.transcriptome.nividic.om.GenepixResults;
import fr.ens.transcriptome.nividic.om.ImaGeneResult;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleDescription;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.Container;
import fr.ens.transcriptome.nividic.util.NividicUtils;
import fr.ens.transcriptome.nividic.util.parameter.Parameter;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class define an algorithm to show test suite report.
 * @author Laurent Jourdren
 */
public class DoelanGenerateReport extends Algorithm implements Module {

  private boolean galResult;
  private String galResultMessage;

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
      md = new ModuleDescription("DoelanGenerateReport",
          "Show report of a test suite");
      md.setStability(AboutModule.STATE_STABLE);
    } catch (PlatformException e) {
      getLogger().error("Unable to create the module description");
    }
    return md;
  }

  /**
   * Set the parameters of the element.
   * @return The defaults parameters to set.
   */
  protected Parameters defineParameters() {
    return null;
  }

  /**
   * This method contains all the code to manipulate the container <b>c </b> in
   * this element.
   * @param c The container to be manipulated
   * @param parameters Parameters of the elements
   * @throws PlatformException if an error occurs while executing the test.
   */
  protected void doIt(final Container c, final Parameters parameters)
      throws PlatformException {

    StringBuffer sb = new StringBuffer();

    // Get GPR data
    BioAssay gpr = DoelanDataUtils.getBioAssay(c);

    // Get test suitel result
    TestSuiteResult testSuiteResult = DoelanDataUtils.getTestSuiteResult(c);

    // Get GAL data
    BioAssay gal = DoelanDataUtils.getArrayList(c);

    // Get the test results
    QualityTestResult[] results = DoelanDataUtils
        .getOrderedQualityTestResults(c);

    // Get the new array list
    BioAssay newGal = testSuiteResult.getNewArrayList();

    if (gal != null) {

      this.galResult = newGal != null;

      final String msg;
      if (newGal == null)
        msg = "No array list data.";
      else
        msg = "The output array contains " + newGal.size()
            + " features of the " + gal.size()
            + " features of the original file.";

      this.galResultMessage = msg;
    }

    sb.append(header());
    sb.append(writeInfo(testSuiteResult.getChipTypeName(), testSuiteResult
        .getTestSuiteName(), gal, gpr, testSuiteResult.getDescription()));

    sb.append(writeSummary(results, testSuiteResult, gpr));

    if (gal != null)
      sb.append(writeEmptySpot(gal));
    sb.append(writeTests(results));

    if (testSuiteResult.getArrayPlot() != null)
      sb.append(writeArrayPlot());

    sb.append(footer());

    testSuiteResult.setHtmlReport(sb.toString());
  }

  private String writeArrayPlot() {

    StringBuffer sb = new StringBuffer();

    sb
        .append("<h2>Visualisation of removed features in ouput array list</h2>\n<hr>\n");

    sb.append("<img src=\"arrayplot.jpeg\" alt=\"arraplot image\"><br>\n");

    sb.append("<b><u>Legend:</u></b>");
    sb
        .append("<li>Circle spot in <font color=\"#C0C0C0\">Light gray</font>: Empty feature"
            + " or absent feature in the original Gal file.</li>\n");
    sb.append("<li>Spot in <font color=\"green\">Green</font>: Feature"
        + " accepted in the new Gal file.</li>\n");
    sb.append("<li>Spot in <font color=\"red\">Red</font>: Feature rejected"
        + " in the new Gal file.</li>\n");
    sb.append("<li>Spot in <font color=\"#C0C0C0\">White</font>: Feature "
        + "fail to one or more test but not rejected"
        + " in the new Gal file.</li>\n");
    sb.append("</ul>");

    return sb.toString();

  }

  private String writeTests(final QualityTestResult[] results) {
    StringBuffer sb = new StringBuffer();

    sb.append("<h2>Tests</h2>\n<hr>\n");

    for (int i = 0; i < results.length; i++) {

      final QualityTestResult r = results[i];

      sb.append("<a name=\"");
      sb.append(r.getTestId());
      sb.append("\" id=\"test_");
      sb.append(i);
      sb.append("\"></a>");
      sb.append("<h3>Test: ");
      sb.append(r.getTestId());
      sb.append("</h3>\n");
      sb.append("<ul>\n<li><b>Description:</b> ");
      sb.append(r.getTestDescription());
      sb.append("</li>\n");
      sb.append("<li><b>Test module:</b> ");
      sb.append(r.getTestType());
      sb.append("</li>\n");

      sb.append("<li><b>Parameters:</b> \n");
      // sb.append("<table border=\"2\">\n");
      // sb.append("<tr><th>Name</th><th>Value</th><th>Unit</th></tr>");
      sb.append("<ul>\n");

      String[] pNames = r.getParameters().getParametersNames();
      for (int j = 0; j < pNames.length; j++) {
        Parameter p = r.getParameters().getParameter(pNames[j]);
        // sb.append("<tr><td>");
        sb.append("<li>");

        if (p.getLongName() != null)
          sb.append(p.getLongName());
        else
          sb.append(p.getName());

        sb.append(": <i>");

        try {
          if (p.getType() == Parameter.DATATYPE_INTEGER)
            sb.append(p.getIntValue());
          else if (p.getType() == Parameter.DATATYPE_DOUBLE)
            sb.append(p.getDoubleValue());
          else if (p.getType() == Parameter.DATATYPE_BOOLEAN)
            sb.append(p.getBooleanValue());
          else if (p.getType() == Parameter.DATATYPE_STRING)
            sb.append(p.getStringValue());
          else if (p.getType() == Parameter.DATATYPE_YESNO) {
            if (p.getBooleanValue())
              sb.append("Yes");
            else
              sb.append("No");
          }
        } catch (ParameterException e) {
          NividicUtils.nop();
        }

        // sb.append("</td><td>");

        if (p.getUnit() != null) {
          sb.append(' ');
          sb.append(p.getUnit());
        }

        // sb.append("</td></tr>");
        sb.append("</i></li>\n");
      }

      // sb.append("</table><br></li>\n");
      sb.append("</ul>\n");

      if (r.getImage() != null) {
        sb.append("<li><b>Result graph:</b><br><br>");
        sb.append("<img src=\"");
        sb.append(r.getTestId());
        sb.append(".jpeg");
        sb.append("\" alt=\"result graph image\"><br><br></li>\n");
      }

      if (r.isError()) {

        sb.append("<li><b>Error:</b><b class=\"fail\">");
        sb.append(r.getErrorMessage());
        sb.append("</b></li>\n");

      } else if (r instanceof QualityUnitTestResult) {
        // unit result
        QualityUnitTestResult unitResultTest = (QualityUnitTestResult) r;

        if (unitResultTest.isGlobalResult()) {
          sb.append("<li><b>Result:</b> ");
          if (unitResultTest.getResult())
            sb.append("<b class=\"pass\">Pass");
          else
            sb.append("<b class=\"fail\">Fail");

          sb.append("</b></li>\n");
        } else {

          sb.append("<li><b>Result 635 channel : </b>");
          if (unitResultTest.getResultChannel635().isPass())
            sb.append("<b class=\"pass\">Pass");
          else
            sb.append("<b class=\"fail\">Fail");
          sb.append("</b></li>\n");

          sb.append("<li><b>Result 532 channel:</b> ");
          if (unitResultTest.getResultChannel532().isPass())
            sb.append("<b class=\"pass\">Pass");
          else
            sb.append("<b class=\"fail\">Fail");
          sb.append("</b></li>\n");

        }
      } else {
        // Global result
        sb.append("<li><b>Result:</b> ");
        if (r.getResult())
          sb.append("<b class=\"pass\">Pass");
        else
          sb.append("<b class=\"fail\">Fail");

        sb.append("</b></li>\n");
      }

      sb.append("<li><b>Comment:</b> <br>");

      sb.append(r.getMessage());
      sb.append("</li>\n");
      sb.append("</ul>\n");

    }

    return sb.toString();
  }

  private static String getHTMLSign(final String sign) {

    String result = null;

    if (sign == null)
      return result;

    if ("<".equals(sign))
      result = "&lt;";
    else if ("<=".equals(sign))
      result = "&lt;=";
    else if (">".equals(sign))
      result = "&gt;";
    else if (">=".equals(sign))
      result = "&gt;=";

    return result;
  }

  private String writeInfo(final String chipTypeName,
      final String testSuiteName, final BioAssay gal, final BioAssay gpr,
      final String arrayDescription) {

    StringBuffer sb = new StringBuffer();

    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL,
        DateFormat.MEDIUM, Locale.UK);

    GenepixArrayList gArrayList = null;

    if (gal != null)
      gArrayList = new GenepixArrayList(gal);

    sb.append("<a href=\"");
    sb.append(DoelanRegistery.getAppURL());
    sb.append("\"><img src=\"logo.jpeg\" alt=\"");
    sb.append(DoelanRegistery.getAppName());
    sb.append(" logo\"></a>");
    sb.append("<h1 align=\"center\"><u>");
    // sb.append(DoelanRegistery.getAppName());
    sb.append(" Quality Test report");
    sb.append("</u></h1>\n");

    sb.append("<h2><b>Chip type: </b><i>");
    sb.append(chipTypeName);
    sb.append("</i><br><b>Test suite: </b><i>");
    sb.append(testSuiteName);
    sb.append("</i></h2>\n<br>");

    sb.append("<b>");
    sb.append(DoelanRegistery.getAppName());
    sb.append(" version: </b>");
    sb.append(DoelanRegistery.getAppVersion());
    Date compileDate = DoelanRegistery.getAppCompileDate();
    if (compileDate != null) {
      sb.append(" (compiled on ");
      sb.append(df.format(compileDate));
      sb.append(")");
    }

    sb.append("<br><b>Report date: </b>");
    sb.append(df.format(new Date(System.currentTimeMillis())));
    sb.append("<br>");

    String scannedBy = null;

    switch (BioAssayUtils.getBioAssayType(gpr)) {

    case BioAssayUtils.GPR_BIOASSAY_TYPE:

      GenepixResults gResult = new GenepixResults(gpr);

      Date gprDate = gResult.getDateTime();
      if (gprDate != null) {
        sb.append("<b>Array scanned on:</b> ");
        sb.append(df.format(gprDate));
        sb.append("<br>\n");
      }
      scannedBy = gResult.getScanner();
      if (scannedBy != null) {
        sb.append("<b>Array scanned by:</b> ");
        sb.append(scannedBy);
        sb.append("<br>\n");
      }

      break;

    case BioAssayUtils.IMAGENE_RESULTS_BIOASSAY_TYPE:

      ImaGeneResult igResult = new ImaGeneResult(gpr);

      Date imgDate = igResult.getDateTime();
      if (imgDate != null) {
        sb.append("<b>Array scanned on:</b> ");
        sb.append(df.format(imgDate));
        sb.append("<br>\n");
      }

      break;

    default:
      break;
    }

    if (gal != null) {
      sb.append("<b>Array name:</b> ");
      String arrayName = gArrayList.getArrayName();
      if (arrayName != null)
        sb.append(scannedBy);
      else
        sb.append("unknown");
      sb.append("<br>\n");

      sb.append("<b>Array revision:</b> ");
      String revision = gArrayList.getArrayRevision();
      if (revision != null)
        sb.append(revision);
      else
        sb.append("unknown");
      sb.append("<br>\n");

      sb.append("<b>Array manufacturer:</b> ");
      String manufacturer = gArrayList.getManufacturer();
      if (manufacturer != null)
        sb.append(manufacturer);
      else
        sb.append("unknown");
      sb.append("<br>\n");
    }

    if (arrayDescription != null) {
      String desc = arrayDescription.trim();
      if (!"".equals(desc)) {

        sb.append("<b>Array description:</b> ");
        sb.append(desc);
        sb.append("<br>\n");
      }
    }

    return sb.toString();
  }

  private String writeSummary(final QualityTestResult[] results,
      final TestSuiteResult testSuiteResult, final BioAssay gpr) {

    StringBuffer sb = new StringBuffer();

    sb.append("<h2>Summary</h2>\n<hr>\n");

    sb.append("<table border=\"1\">\n<tr>"
        + "<th>Test name</th><th>Description</th>"
        + "<th>635</th><th>532</th><th>Threshold</th>"
        + "<th>Result 635</th><th>Result 532</th><th>Global Result</th></tr>");

    NumberFormat nf = NumberFormat.getNumberInstance(Locale.UK);
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);

    for (int i = 0; i < results.length; i++) {

      final QualityTestResult r = results[i];

      sb.append("<tr><td><a href=\"#test_");
      sb.append(i);
      sb.append("\">");
      sb.append(r.getTestId());
      sb.append("</a></td>");

      sb.append("<td>");
      sb.append(r.getTestDescription());
      sb.append("</td>");

      if (r instanceof QualityUnitTestResult) {

        QualityUnitTestResult unitResult = (QualityUnitTestResult) r;

        if (unitResult.isGlobalResult()) {
          sb.append("<td colspan=\"2\">");
          sb.append(nf.format(unitResult.getResultAllChannels().getValue()));
          sb.append(" ");
          sb.append(unitResult.getResultAllChannels().getUnit());
          sb.append("</td>");

          sb.append("<td>");
          sb.append(getHTMLSign(unitResult.getResultAllChannels()
              .getThresholdEqualityType()));
          sb.append(" ");
          sb
              .append(nf.format(unitResult.getResultAllChannels()
                  .getThreshold()));
          sb.append(" ");
          sb.append(unitResult.getResultAllChannels().getUnit());
          sb.append("</td>");

          sb.append("<td colspan=\"2\"></td>");

        } else {
          sb.append("<td>");
          sb.append(nf.format(unitResult.getResultChannel635().getValue()));
          sb.append(" ");
          sb.append(unitResult.getResultChannel635().getUnit());
          sb.append("</td>");

          sb.append("<td>");
          sb.append(nf.format(unitResult.getResultChannel532().getValue()));
          sb.append(" ");
          sb.append(unitResult.getResultChannel532().getUnit());
          sb.append("</td>");

          sb.append("<td>");

          sb.append(getHTMLSign(unitResult.getResultChannel532()
              .getThresholdEqualityType()));
          sb.append(" ");
          sb.append(nf.format(unitResult.getResultChannel635().getThreshold()));
          sb.append(" ");
          sb.append(unitResult.getResultChannel635().getUnit());
          sb.append("</td>");

          if (unitResult.getResultChannel635().isPass())
            sb.append("<td class=\"pass\">Pass");
          else
            sb.append("<td class=\"fail\">Fail");
          sb.append("</td>");

          sb.append("");
          if (unitResult.getResultChannel532().isPass())
            sb.append("<td class=\"pass\">Pass");
          else
            sb.append("<td class=\"fail\">Fail");
          sb.append("</td>");

        }
      } else {

        QualityGlobalTestResult globalResult = (QualityGlobalTestResult) r;

        sb.append("<td colspan=\"2\">");
        sb.append(nf.format(globalResult.getValue()));
        sb.append(" ");
        sb.append(globalResult.getUnit());
        sb.append("</td>");

        sb.append("<td>");
        sb.append(getHTMLSign(globalResult.getThresholdEqualityType()));
        sb.append(" ");
        sb.append(nf.format(globalResult.getThreshold()));
        sb.append(" ");
        sb.append(globalResult.getUnit());
        sb.append("</td>");

        sb.append("<td colspan=\"2\"></td>");
      }

      sb.append("");
      if (r.getResult())
        sb.append("<td class=\"pass\">Pass");
      else
        sb.append("<td class=\"fail\">Fail");
      sb.append("</td></tr>\n");

    }
    sb.append("</table><br>");

    sb.append("<b>Test suite result:</b> ");
    if (testSuiteResult.getResult())
      sb.append("<b class=\"pass\">Pass</b><br><br>\n");
    else
      sb.append("<b class=\"fail\">Fail</b><br><br>\n");

    sb.append("<b>Empty features on the chip:</b> ");
    sb.append(BioAssayUtils.countEmptySpots(gpr));
    sb.append("/");
    sb.append(gpr.size());
    sb.append(".<br>\n");

    final String[] emptySpotIds = testSuiteResult.getEmptySpotIds();
    if (emptySpotIds != null || emptySpotIds.length != 0) {
      sb.append("<b>Empty feature identifiers: </b>\n<ul>\n");

      for (int i = 0; i < emptySpotIds.length; i++) {
        sb.append("<li>");
        sb.append(emptySpotIds[i]);
        sb.append("</li>");
      }

      sb.append("</ul>\n");
    }

    sb.append("<b>New output Array list:</b> ");
    if (this.galResult)
      sb.append("<b class=\"pass\">Yes</b><br>\n");
    else
      sb.append("<b class=\"fail\">No</b><br>\n");
    sb.append("<b>New Array list generator message:</b> ");
    sb.append(this.galResultMessage);
    sb.append("<br>");

    return sb.toString();
  }

  private String writeEmptySpot(final BioAssay gal) {

    if (gal == null)
      return null;

    final int emptyFeatures = BioAssayUtils.countEmptySpots(gal);
    final int totalFeatures = gal.size();
    final int realFeatures = totalFeatures - emptyFeatures;

    StringBuffer sb = new StringBuffer();

    sb.append("<b>Empty features in array list file:</b> ");
    sb.append(emptyFeatures);
    sb.append("<br>\n<b>Real features in array list file:</b> ");
    sb.append(realFeatures);
    sb.append("<br>\n<b>Total features in array list file:</b> ");
    sb.append(totalFeatures);
    sb.append("<br>\n");

    return sb.toString();
  }

  /**
   * Create a string with the header of the HTML page.
   * @return A string with the header of the HTML page
   */
  private String header() {

    StringBuffer sb = new StringBuffer();
    // sb.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01
    // Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">\n");
    sb.append("<!-- PRINT HEADER1 HERE -->");
    //
    sb.append("<html>\n<head>\n");

    sb.append("<title>");
    sb.append(Defaults.APP_NAME);
    sb.append(" Quality Test report");
    sb.append(" </title>\n");
    sb.append("<!-- PRINT HEADER2 HERE -->");

    sb
        .append("<style>body {font-size: 11px; font-family: sans-serif;}</style>\n");
    sb.append("<style>.fail {color: red;}</style>\n");
    sb.append("<style>.pass {color: green;}</style>\n");
    sb
        .append("<style>td {font-size: 11px; font-family: sans-serif;}</style>\n");
    sb
        .append("<style>th {font-size: 11px; font-family: sans-serif;}</style>\n");
    sb.append("<style>h1 {font-size: 20px; font-family: serif;}</style>\n");
    sb
        .append("<style>h2 {font-size: 14px; font-family: sans-serif;}</style>\n");

    sb.append("</head>\n<body>\n");
    return sb.toString();
  }

  /**
   * Create a string with the footer of the HTML page.
   * @return A string with the footer of the HTML page
   */
  private String footer() {

    StringBuffer sb = new StringBuffer();
    sb.append("<br><hr><a href='");
    sb.append(DoelanRegistery.getAppURL());
    sb.append("'>");
    sb.append(Defaults.APP_NAME);
    sb.append("</a> ");
    sb.append(DoelanRegistery.getAppVersion());
    sb.append(", Copyright " + DoelanRegistery.getCopyrightDate() + " "
        + "<a href=\"" + DoelanRegistery.getOrganizationURL() + "\">"
        + DoelanRegistery.getOrganizationName() + "</a>.\n");
    sb.append("<!-- PRINT COMMAND HERE -->");
    sb.append("</body></html>");

    return sb.toString();
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @throws PlatformException If the name or the version of the element is
   *           <b>null </b>.
   */
  public DoelanGenerateReport() throws PlatformException {
    // MUST BE EMPTY
  }

}