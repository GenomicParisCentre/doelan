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

package fr.ens.transcriptome.doelan.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.nividic.om.BioAssay;
import fr.ens.transcriptome.nividic.om.io.BioAssayWriter;
import fr.ens.transcriptome.nividic.om.io.GALWriter;
import fr.ens.transcriptome.nividic.om.io.NividicIOException;
import fr.ens.transcriptome.nividic.util.SystemUtils;
import fr.ens.transcriptome.nividic.util.WebBrowser;

/**
 * Report widget.
 * @author Laurent Jourdren
 */
public class ReportTabWidget extends JPanel {

  // For log system
  private Logger log = Logger.getLogger(ReportTabWidget.class);

  private JEditorPane editor;
  private DoelanEditorKit kit;
  private BioAssay galData;
  private JButton saveReportButton = new JButton("Save report");
  private JButton printReportButton = new JButton("Print report");
  private JButton saveGALButton = new JButton("Save new Array List");
  private JLabel lablelLocation = new JLabel("Location :");
  private JLabel statusBar;
  private JTextField locationfield = new JTextField(System
      .getProperty("user.home"));
  private ReportTabWidget panel = this;
  private Map images;
  private String browserPath = DoelanRegistery.getBrowserPath();
  private String htmlText;
  private String prefixImage;

  //
  // Getters
  //

  /**
   * Get the editor widget.
   * @return Returns the editor
   */
  private JEditorPane getEditor() {
    return editor;
  }

  //
  // Setters
  //

  /**
   * Set the image for the HTML render.
   * @param mapImage Map of the image to set
   */
  public void setImages(final Map mapImage) {
    this.kit.setMapImages(mapImage);
    this.images = mapImage;

  }

  /**
   * Set the text of the report.
   * @param text The text of the report
   */
  public void setText(final String text) {

    getEditor().setText(text);
    this.htmlText = text;
    if (text == null) {
      this.saveReportButton.setEnabled(false);
      this.printReportButton.setEnabled(false);
    } else {
      this.saveReportButton.setEnabled(true);
      this.printReportButton.setEnabled(true);
    }

    // Replace the filename in html

    this.prefixImage = "" + System.currentTimeMillis();

    if (this.images == null || this.htmlText == null)
      return;
    Iterator it = this.images.keySet().iterator();
    while (it.hasNext()) {

      String name = (String) it.next();

      this.htmlText = this.htmlText.replaceFirst("<img src=\"" + name,
          "<IMG SRC=\"" + this.prefixImage + "_" + name);
    }

  }

  /**
   * Set the new Gal file.
   * @param gal The new gal data
   */
  public void setGalData(final BioAssay gal) {
    this.galData = gal;
    if (gal == null)
      this.saveGALButton.setEnabled(false);
    else
      this.saveGALButton.setEnabled(true);
  }

  /**
   * Get the new Gal data.
   * @return The new gal data
   */
  public BioAssay getGalData() {
    return this.galData;
  }

  //
  // Other methods
  //

  public void clear() {
    this.galData = null;
    this.images = null;
    setText(null);
  }

  private void saveImages(final String directory) {

    if (this.images == null || this.images.size() == 0 || directory == null)
      return;

    Iterator it = this.images.keySet().iterator();
    while (it.hasNext()) {

      String name = (String) it.next();
      Image img = (Image) this.images.get(name);
      if (img == null)
        continue;

      File file = new File(directory, this.prefixImage + "_" + name);

      try {
        ImageIO.write((RenderedImage) img, "jpeg", file);
      } catch (IOException e) {
        showMessage("Error while writing images files : " + e.getMessage(),
            true);
      }
    }
  }

  private void saveReport() {

    if (getEditor().getText() == null || "".equals(getEditor().getText())) {

      showMessage("Nothing to save.", true);
      return;
    }

    if (DoelanRegistery.isAppletMode())
      saveReportFile(this.locationfield.getText());
    else {

      JFileChooser jfc = new JFileChooser();
      jfc.setCurrentDirectory(new File(DoelanRegistery
          .getDoelanReportDirectory()));
      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

      FileFilter ff = new FileFilter() {
        public boolean accept(final File f) {

          if (f.isDirectory())
            return true;

          final String filename = f.getName().toLowerCase();
          return filename.endsWith(".html") || filename.endsWith(".htm");
        }

        public String getDescription() {
          return "HTML file";
        }
      };

      jfc.addChoosableFileFilter(ff);

      int result = jfc.showSaveDialog(this);
      if (result == JFileChooser.APPROVE_OPTION) {

        String filename = jfc.getSelectedFile().getAbsolutePath();
        saveReportFile(filename);

        // jfc.getSelectedFile()

      }
    }
  }

  private void saveReportFile(String filename) {

    if (filename == null || "".equals(filename)) {
      showMessage("Invalid file name.", true);
      return;
    }

    if (DoelanRegistery.isAppletMode()) {

      File f = new File(filename);
      if (f.isDirectory())
        filename = filename + File.separator + Defaults.APP_NAME.toLowerCase()
            + ".html";
    }

    try {

      if (filename.length() < 5)
        filename = filename + ".html";
      else {
        String end = filename.substring(filename.length() - 5);

        if (!end.toLowerCase().endsWith(".html"))
          filename = filename + ".html";
      }

      File f = new File(filename);
      OutputStream fos = new FileOutputStream(f);
      fos.write(this.htmlText.getBytes("ISO-8859-1"));
      fos.close();

      saveImages(f.getParent());

    } catch (FileNotFoundException e1) {
      showMessage("Error while writing the file : " + e1.getMessage(), true);

    } catch (MalformedURLException e1) {
      showMessage("Bad URL", true);
    } catch (IOException e) {
      showMessage("Error while writing the file : ", true);
    }

  }

  private void saveArrayList() {

    if (getGalData() == null) {

      showMessage("Nothing to save.", true);
      return;
    }

    if (DoelanRegistery.isAppletMode())
      saveReportFile(this.locationfield.getText());
    else {

      JFileChooser jfc = new JFileChooser();
      jfc
          .setCurrentDirectory(new File(DoelanRegistery
              .getDoelanDataDirectory()));
      jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      jfc.addChoosableFileFilter(new FileFilter() {
        public boolean accept(final File f) {

          if (f.isDirectory())
            return true;
          if (f.getName().length() < 4)
            return false;
          String end = f.getName().substring(f.getName().length() - 4);

          if (end.toLowerCase().endsWith(".gal"))
            return true;
          else
            return false;
        }

        public String getDescription() {
          return "Genepix Array List";
        }
      });

      int result = jfc.showSaveDialog(this);
      if (result == JFileChooser.APPROVE_OPTION) {

        String filename = jfc.getSelectedFile().getAbsolutePath();
        saveArrayListFile(filename);

      }
    }
  }

  private void saveArrayListFile(String filename) {

    if (filename == null || "".equals(filename)) {
      showMessage("Invalid file name.", true);
      return;
    }

    if (DoelanRegistery.isAppletMode()) {

      File f = new File(filename);
      if (f.isDirectory())
        filename = filename + File.separator + Defaults.APP_NAME.toLowerCase()
            + ".gal";
    }

    if (filename.length() < 4)
      filename = filename + ".gal";
    else {
      String end = filename.substring(filename.length() - 4);

      if (!end.toLowerCase().endsWith(".gal"))
        filename = filename + ".gal";
    }

    // write file

    try {
      FileOutputStream fos = new FileOutputStream(new File(filename));
      BioAssayWriter baw = new GALWriter(fos);
      // baw.addAllFieldsToWrite();
      baw.write(getGalData());
      fos.close();

    } catch (FileNotFoundException e) {
      showMessage("File not found", true);
    } catch (NividicIOException e) {
      showMessage("Unable to save the file", true);
    } catch (IOException e) {
      showMessage("Error while saving the file", true);
    }

  }

  private void printReport() {

    if (getEditor().getText() == null || "".equals(getEditor().getText())) {
      showMessage("Nothing to print.", true);
      return;
    }

    String outputFile = null;

    try {

      File f = File.createTempFile(Defaults.APP_NAME.toLowerCase(), ".html");
      PrintWriter out = new PrintWriter(new FileWriter(f));

      String text = this.htmlText;
      String newText1 = text
          .replaceFirst("\\<\\!\\-\\-\\ PRINT\\ COMMAND\\ HERE\\ \\-\\-\\>",
              "<script language=\"javascript\">if (window.print)  window.print();</script>");

      String newText2 = newText1.replaceFirst(
          "\\<\\!\\-\\-\\ PRINT\\ HEADER1\\ HERE\\ \\-\\-\\>",
          "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">\n");
      String newText3 = newText2
          .replaceFirst(
              "\\<\\!\\-\\-\\ PRINT\\ HEADER2\\ HERE\\ \\-\\-\\>",
              "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/>\n");

      out.write(newText3);
      out.close();

      saveImages(f.getParent());
      outputFile = f.getAbsolutePath();

    } catch (FileNotFoundException e1) {
      showMessage("Error while writing the file : " + e1.getMessage(), true);

    } catch (MalformedURLException e1) {
      showMessage("Bad URL", true);

    } catch (IOException e) {
      showMessage("Error while writing the file : ", true);

    }

    if (outputFile != null)
      try {

        WebBrowser.launch(this.browserPath, outputFile);
      } catch (IOException e) {
        showMessage("Error while launching navigator : " + e.getMessage(), true);
      }

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public ReportTabWidget(final JLabel statusBar) {

    saveReportButton.setToolTipText("Press this button to save the report");
    printReportButton.setToolTipText("Press this button to print the report");
    saveGALButton
        .setToolTipText("Press this button to save the output array list");

    if (!SystemUtils.isMacOsX()) {
      saveReportButton.setMnemonic(KeyEvent.VK_S);
      printReportButton.setMnemonic(KeyEvent.VK_P);
      saveGALButton.setMnemonic(KeyEvent.VK_L);
    }

    this.statusBar = statusBar;
    this.editor = new JEditorPane();
    this.editor.setContentType("text/html");
    this.kit = new DoelanEditorKit();
    editor.setEditorKit(this.kit);

    getEditor().setEditable(false);

    JScrollPane editorScrollPane = new JScrollPane(getEditor());
    editorScrollPane
        .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    setLayout(new BorderLayout());

    add(editorScrollPane, BorderLayout.CENTER);

    JPanel buttonsPane = new JPanel();
    add(buttonsPane, BorderLayout.SOUTH);

    if (DoelanRegistery.isAppletMode()) {
      buttonsPane.add(lablelLocation);
      buttonsPane.add(locationfield);
    }

    buttonsPane.add(saveReportButton);
    saveReportButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        panel.saveReport();
      }
    });

    if (this.browserPath != null) {
      buttonsPane.add(printReportButton);
      printReportButton.addActionListener(new ActionListener() {
        public void actionPerformed(final ActionEvent e) {
          panel.printReport();
        }
      });
    }

    buttonsPane.add(saveGALButton);
    saveGALButton.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {

        saveArrayList();
      }
    });

    setText(null);
    setGalData(null);

    // editorScrollPane.setPreferredSize(new Dimension(1280, 1024));
    // editorScrollPane.setMinimumSize(new Dimension(200, 600));

  }

  /**
   * Show a message to the user.
   * @param message Message to show
   * @param error true if message is an error
   */
  private void showMessage(final String message, final boolean error) {

    log.error("Message : " + message);
    if (DoelanRegistery.isAppletMode()) {
      if (error)
        this.statusBar.setForeground(Color.RED);
      else
        this.statusBar.setForeground(Color.BLACK);
      this.statusBar.setText(message);
      Toolkit.getDefaultToolkit().beep();
    } else
      JOptionPane.showMessageDialog(this, message, error ? "Error" : "Message",
          error ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE);
  }

}