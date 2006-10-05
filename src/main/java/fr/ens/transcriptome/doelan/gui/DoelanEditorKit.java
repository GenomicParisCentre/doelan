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

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.nividic.util.WebBrowser;

/**
 * HTML viewer which can display image.
 * @author Laurent Jourdren
 */
public class DoelanEditorKit extends HTMLEditorKit {

  private Map mapImages;

  /**
   * Get the factor viewer.
   * @return the viewer factory
   */
  public ViewFactory getViewFactory() {

    return new HTMLFactoryX(getMapImages());
  }

  /**
   * Get the map of the images.
   * @return Returns the mapImages
   */
  public Map getMapImages() {
    return mapImages;
  }

  /**
   * Set the map of the images.
   * @param mapImages The mapImages to set
   */
  public void setMapImages(final Map mapImages) {
    this.mapImages = mapImages;
  }

  //
  //
  // Clickodrome
  //
  //

  // Since we only have two mouse events to listen to, we'll use the same
  // method to generate the appropriate hyperlinks and distinguish
  // between them when we react to the mouse events.
  private static final int JUMP = 0;
  private static final int MOVE = 1;

  private LinkController myController = new LinkController();

  /**
   * Overide the install method of the EditorKit.
   * @param c The editor pane to listen
   */
  public void install(final JEditorPane c) {
    c.addMouseListener(myController);
    c.addMouseMotionListener(myController);
  }

  /**
   * This class implements a HTML factory which can display image.
   * @author Laurent Jourdren
   */
  public static class HTMLFactoryX extends HTMLFactory implements ViewFactory {

    private Map mapImages;

    /**
     * Display an element.
     * @param elem to display
     * @return a view object
     */
    public View create(final Element elem) {
      Object o = elem.getAttributes()
          .getAttribute(StyleConstants.NameAttribute);
      if (o instanceof HTML.Tag) {
        HTML.Tag kind = (HTML.Tag) o;
        if (kind == HTML.Tag.IMG) {
          MyImageView v = new MyImageView(elem, this.mapImages);
          return v;
        }

      }
      return super.create(elem);
    }

    HTMLFactoryX(final Map mapImages) {

      this.mapImages = mapImages;
    }

  }

  private static class LinkController extends MouseInputAdapter implements
      Serializable {

    private URL currentUrl;

    // here's the mouseClicked event similar to the one in
    // the regular HTMLEditorKit, updated to indicate this is
    // a "jump" event
    public void mouseClicked(final MouseEvent e) {
      JEditorPane editor = (JEditorPane) e.getSource();

      if (!editor.isEditable()) {
        Point pt = new Point(e.getX(), e.getY());
        int pos = editor.viewToModel(pt);
        if (pos >= 0) {
          activateLink(pos, editor, JUMP);
        }
      }
    }

    // And here's our addition. Now the mouseMove events will
    // also call activateLink, but with a "move" type
    public void mouseMoved(final MouseEvent e) {

      JEditorPane editor = (JEditorPane) e.getSource();
      editor.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      // editor.setCursor(Cursor.getDefaultCursor());

      if (!editor.isEditable()) {
        Point pt = new Point(e.getX(), e.getY());
        int pos = editor.viewToModel(pt);
        if (pos >= 0) {
          activateLink(pos, editor, MOVE);
        }

      }
    }

    // activateLink has now been updated to decide which hyperlink
    // event to generate, based on the event type and status of the
    // currentUrl field. Rather than have two handlers (one for
    // enter/exit, one for active) we do all the work here. This
    // saves us the effort of duplicating the href location code.
    // But that's really minor point. You could certainly provide
    // two handlers if that makes more sense to you.
    protected void activateLink(final int pos, final JEditorPane html,
        final int type) {
      Document doc = html.getDocument();
      if (doc instanceof HTMLDocument) {
        HTMLDocument hdoc = (HTMLDocument) doc;
        Element e = hdoc.getCharacterElement(pos);
        AttributeSet a = e.getAttributes();
        AttributeSet anchor = (AttributeSet) a.getAttribute(HTML.Tag.A);
        String href = (anchor != null) ? (String) anchor
            .getAttribute(HTML.Attribute.HREF) : null;
        boolean shouldExit = false;

        HyperlinkEvent linkEvent = null;
        if (href != null) {
          URL u;
          try {
            u = new URL(hdoc.getBase(), href);
          } catch (MalformedURLException m) {
            u = null;
            return;
          }

          html.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

          if ((type == MOVE) && (!u.equals(currentUrl))) {
            linkEvent = new HyperlinkEvent(html,
                HyperlinkEvent.EventType.ENTERED, u, href);
            currentUrl = u;

          } else if (type == JUMP) {
            linkEvent = new HyperlinkEvent(html,
                HyperlinkEvent.EventType.ACTIVATED, u, href);
            shouldExit = true;

            try {

              String alternativeBrowserPath = DoelanRegistery
                  .getAlternativeBrowserPath();

              WebBrowser.launch(alternativeBrowserPath, u);
            } catch (IOException exception) {
              JOptionPane.showInternalMessageDialog(html,
                  "Error while launching navigator : ", "Error",
                  JOptionPane.ERROR_MESSAGE);
            }
          } else
            return;

          html.fireHyperlinkUpdate(linkEvent);
        } else if (currentUrl != null) {
          shouldExit = true;
        }
        if (shouldExit) {
          linkEvent = new HyperlinkEvent(html, HyperlinkEvent.EventType.EXITED,
              currentUrl, null);
          html.fireHyperlinkUpdate(linkEvent);
          currentUrl = null;
        }
      }
    }
  }

}