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

import javax.swing.JApplet;

import fr.ens.transcriptome.doelan.gui.CommonWindow;

/**
 * Applet of the application.
 * @author Laurent Jourdren
 */
public class GenepixApplet extends JApplet {

  /**
   * Applet init method.
   */
  public void init() {

    // Initialize default values
    DoelanRegistery.init();
    DoelanRegistery.setAppletMode(true);

    // Display the window.
    setSize(Defaults.WINDOW_WIDTH, Defaults.WINDOW_HEIGHT);

    new CommonWindow(this, null).init();
  }

}