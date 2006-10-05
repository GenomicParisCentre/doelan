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

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.nividic.util.WebBrowser;

/**
 * About widget
 * @author Laurent Jourdren
 */
public class AboutWidget extends JPanel {

  private class WebSiteMouseListener implements MouseListener {

    private String browserPath = DoelanRegistery.getBrowserPath();
    private String url;

    private void setURL(final String url) {
      this.url = url;
    }

    public void mouseClicked(final MouseEvent e) {

      try {

        WebBrowser.launch(browserPath, this.url);
      } catch (IOException exception) {
        showMessage("Error while launching navigator : "
            + exception.getMessage(), true);
      }

    }

    public void mouseEntered(final MouseEvent e) {
    }

    public void mouseExited(final MouseEvent e) {
    }

    public void mousePressed(final MouseEvent e) {
    }

    public void mouseReleased(final MouseEvent e) {
    }

    public WebSiteMouseListener(final String url) {
      setURL(url);
    }

  }

  /**
   * Show a message to the user.
   * @param message Message to show
   * @param error true if message is an error
   */
  private void showMessage(final String message, final boolean error) {

    JOptionPane.showMessageDialog(this, message, error ? "Error" : "Message",
        error ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE);
  }

  private void init() {

    setLayout(new java.awt.GridLayout(1, 2));

    URL url = CommonWindow.class.getResource("/files/doelan-300.png");
    ImageIcon ii = new ImageIcon(url);

    JLabel logoLabel = new JLabel(ii);
    logoLabel.setToolTipText("Click to view " + DoelanRegistery.getAppName()
        + " website");

    add(logoLabel);
    // add(new JLabel(" "));

    JTextPane copyrightLabel = new javax.swing.JTextPane();
    // copyrightLabel.setLineWrap(true);
    // jLabel4.setFocusable(false);
    copyrightLabel.setEditable(false);

    copyrightLabel.setBackground(logoLabel.getBackground());
    // copyrightLabel.setWrapStyleWord(true);
    // copyrightLabel.setForeground(Color.MAGENTA);
    copyrightLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

    copyrightLabel.setText(DoelanRegistery.about());
    // copyrightLabel.setDragEnabled(false);
    // copyrightLabel.setFocusable(false);
    copyrightLabel.setToolTipText("Click to view "
        + DoelanRegistery.getOrganizationName() + " website");

    add(copyrightLabel);

    // add link to the webs site of the project
    if (!DoelanRegistery.isAppletMode()) {

      WebSiteMouseListener goProjectWebsiteListener = new WebSiteMouseListener(
          DoelanRegistery.getAppURL());
      WebSiteMouseListener goOrganizationWebsiteListener = new WebSiteMouseListener(
          DoelanRegistery.getOrganizationURL());

      logoLabel.addMouseListener(goProjectWebsiteListener);
      copyrightLabel.addMouseListener(goOrganizationWebsiteListener);
    }

  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public AboutWidget() {
    init();
  }

}