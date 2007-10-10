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

package fr.ens.transcriptome.nividic.platform.gui;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;

import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.util.gui.SpringUtilities;

/**
 * This class define a widget to show information about a module
 * @author Laurent Jourdren
 */
public class AboutModuleWidget extends JPanel {

  private AboutModule aboutModule;

  //
  // Getters
  //

  /**
   * Get the aboutModule data.
   * @return the aboutModule data
   */
  private AboutModule getAboutModule() {
    return aboutModule;
  }

  //
  // Setters
  //

  /**
   * Set the aboutModule information.
   * @param aboutModule Data to set
   */
  private void setAboutModule(final AboutModule aboutModule) {
    this.aboutModule = aboutModule;
  }

  //
  // Other methods
  //

  private int addInfo(final JPanel panel, final int count, final String name,
      final String value) {

    if (name == null || value == null) {
      /*
       * panel.add(new JLabel(" ")); panel.add(new JLabel(" ")); return count +
       * 1;
       */
      return count;
    }

    JLabel fieldLabel = new JLabel(name);

    fieldLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
    fieldLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    panel.add(fieldLabel);

    JTextPane valueField = new JTextPane();
    valueField.setFont(new Font("SansSerif", Font.PLAIN, valueField.getFont()
        .getSize()));
    valueField.setText(value);
    valueField.setEditable(false);

    panel.add(valueField);

    // panel.add(new JLabel(" "));
    // panel.add(new JLabel(" "));

    return count + 1;
  }

  private void init() {

    JPanel infoPanel = new JPanel();
    infoPanel.setLayout(new SpringLayout());

    final AboutModule m = getAboutModule();

    int c = 0;

    c = addInfo(infoPanel, c, "Name", m.getName());
    c = addInfo(infoPanel, c, "Short description", m.getShortDescription());
    c = addInfo(infoPanel, c, "Version", m.getVersion() == null ? null : m
        .getVersion().toString());

    int stability = m.getStability();
    String stabilityString;

    switch (stability) {
    case AboutModule.STATE_EXPERIMENTAL:
      stabilityString = "experimental";
      break;
    case AboutModule.STATE_STABLE:
      stabilityString = "stable";
      break;
    case AboutModule.STATE_TESTING:
      stabilityString = "testing";
      break;
    case AboutModule.STATE_UNSTABLE:
      stabilityString = "unstable";
      break;

    default:
      stabilityString = null;
      break;
    }

    if (stabilityString != null)
      c = addInfo(infoPanel, c, "Stability", "" + stabilityString);

    c = addInfo(infoPanel, c, "Date", m.getVersionDate() == null ? null : m
        .getVersionDate().toString());
    c = addInfo(infoPanel, c, null, null);
    c = addInfo(infoPanel, c, "Long description", m.getLongDescription());
    c = addInfo(infoPanel, c, "Licence", m.getLicence());
    c = addInfo(infoPanel, c, "Copyright", m.getCopyright());
    c = addInfo(infoPanel, c, "Email", m.getEmail());
    c = addInfo(infoPanel, c, "Website", m.getWebsite());
    c = addInfo(infoPanel, c, "Organization", m.getOrganisation());

    SpringUtilities.makeCompactGrid(infoPanel, c, 2, // rows,
        // cols
        6, 6, // initX, initY
        6, 6); // xPad, yPad

    this.add(infoPanel);

    this.setVisible(true);
  }

  //
  // Constructors
  //
  /**
   * Public constructor.
   * @param aboutModule information about a module to display
   */
  public AboutModuleWidget(final AboutModule aboutModule) {

    setAboutModule(aboutModule);
    init();

  }

}
