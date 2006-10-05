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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.util.SystemUtils;
import fr.ens.transcriptome.nividic.util.gui.ParameterWidget;
import fr.ens.transcriptome.nividic.util.gui.SpringUtilities;
import fr.ens.transcriptome.nividic.util.parameter.ParameterException;
import fr.ens.transcriptome.nividic.util.parameter.Parameters;

/**
 * This class describe a widhet used to edit parameters of an algorithm.
 * @author Laurent Jourdren
 */
public class EditParametersWidget {

  //For log system
  //private static Logger log = Logger.getLogger(EditParametersWidget.class);

  private Algorithm algorithm;
  private JDialog dialog = new JDialog();

  /**
   * Get the algorithm.
   * @return The algorithm
   */
  public Algorithm getAlgorithm() {
    return algorithm;
  }

  /**
   * Set the algorithm.
   * @param algorithm The test to set
   */
  private void setAlgorithm(final Algorithm algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * Show the widget for editing parameters.
   */
  public void edit() {

    final Parameters params = getAlgorithm().getParameters();
    if (params == null)
      return;

    Parameters parameters = getAlgorithm().getParameters();

    String[] paramNames = parameters.getParametersNames();
    dialog.getContentPane().setLayout(new BorderLayout());

    JPanel parametersPanel = new JPanel();
    parametersPanel.setLayout(new SpringLayout());

    final Set paramSet = new HashSet();

    for (int i = 0; i < paramNames.length; i++) {

      ParameterWidget pw = new ParameterWidget(parametersPanel, parameters
          .getParameter(paramNames[i]));

      parametersPanel.add(new JLabel(" "));
      parametersPanel.add(new JLabel(" "));

      paramSet.add(pw);
    }

    SpringUtilities.makeCompactGrid(parametersPanel, paramSet.size() * 3, 2, //rows,
        // cols
        6, 6, //initX, initY
        6, 6); //xPad, yPad

    /*
     * String[] labels = {"Name: ", "Fax: ", "Email: ", "Address: "}; int
     * numPairs = labels.length; // Create and populate the panel. //JPanel p =
     * new JPanel(new SpringLayout()); Container p = parametersPanel; for (int i =
     * 0; i < numPairs; i++) { JLabel l = new JLabel(labels[i],
     * JLabel.TRAILING); p.add(l); JTextField textField = new JTextField(10);
     * l.setLabelFor(textField); p.add(textField); } // Lay out the panel.
     * SpringUtilities.makeCompactGrid(p, numPairs, 2, //rows, cols 6, 6,
     * //initX, initY 6, 6); //xPad, yPad
     */

    dialog.getContentPane().add(parametersPanel, BorderLayout.CENTER);
    JPanel buttonsPanel = new JPanel();

    JButton okButton = new JButton("OK");
    JButton cancelButton = new JButton("Cancel");

    if (!SystemUtils.isMacOsX()) {
      okButton.setMnemonic(KeyEvent.VK_O);
      cancelButton.setMnemonic(KeyEvent.VK_C);
    }

    buttonsPanel.add(okButton);
    buttonsPanel.add(cancelButton);
    dialog.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

    okButton.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent e) {

        Iterator it = paramSet.iterator();
        boolean ok = true;
        while (it.hasNext()) {
          ParameterWidget pw = (ParameterWidget) it.next();
          try {

            pw.putValueInParameter();

          } catch (ParameterException e1) {
            ok = false;
            JOptionPane.showMessageDialog(dialog, "Bad parameter : "
                + pw.getParameter().getName());
          }

        }

        if (ok)
          dialog.hide();
      }

    });

    cancelButton.addActionListener(new ActionListener() {

      public void actionPerformed(final ActionEvent e) {

        dialog.hide();
      }

    });

    dialog.setTitle("Edit parameters for "
        + ((Module) getAlgorithm()).aboutModule().getName());

    dialog.setResizable(false);
    dialog.setModal(true);
    dialog.pack();

    /*
     * double width = 0; double height = 0; Iterator it = paramSet.iterator();
     * int maxSizeLabels = 0; int maxSizeValues = 0; while (it.hasNext()) {
     * ParameterWidget pw = (ParameterWidget) it.next(); int lenLabels =
     * pw.getMaxSizeLabels(); int lenValues = pw.getMaxSizeValues(); if
     * (lenLabels>maxSizeLabels) maxSizeLabels = lenLabels; if
     * (lenValues>maxSizeValues) maxSizeValues = lenValues; } it =
     * paramSet.iterator(); while (it.hasNext()) { ParameterWidget pw =
     * (ParameterWidget) it.next(); //pw.setSizeValues(maxSizeLabels,
     * maxSizeValues); //pw.setSizeValues(400, 400); Dimension d = pw.getSize();
     * width = d.getWidth() * 1.3; height += d.getHeight(); } Dimension d =
     * buttonsPanel.getSize(); height += d.getHeight(); dialog.setSize((int)
     * width, (int) height*2);
     */
    dialog.show();

  }

  /**
   * Set the location of the widget.
   * @param posX x position
   * @param posY y position
   */
  public void setLocation(final int posX, final int posY) {
    this.dialog.setLocation(posX, posY);
  }

  /**
   * Hide the widget.
   */
  public void close() {
    this.dialog.hide();
  }

  //
  // Constructor
  //

  /**
   * Public consructor.
   * @param algorithm Test to edit
   */
  public EditParametersWidget(final Algorithm algorithm) {
    setAlgorithm(algorithm);
  }

}