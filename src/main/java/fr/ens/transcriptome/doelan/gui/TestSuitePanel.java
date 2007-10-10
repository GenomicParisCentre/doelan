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
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import fr.ens.transcriptome.doelan.Core;
import fr.ens.transcriptome.doelan.Defaults;
import fr.ens.transcriptome.doelan.DoelanException;
import fr.ens.transcriptome.doelan.DoelanRegistery;
import fr.ens.transcriptome.doelan.algorithms.QualityTest;
import fr.ens.transcriptome.doelan.data.QualityTestSuiteURL;
import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.platform.gui.AboutModuleWidget;
import fr.ens.transcriptome.nividic.platform.module.AboutModule;
import fr.ens.transcriptome.nividic.platform.module.Module;
import fr.ens.transcriptome.nividic.platform.module.ModuleLocation;
import fr.ens.transcriptome.nividic.platform.module.ModuleManager;
import fr.ens.transcriptome.nividic.platform.module.ModuleQuery;
import fr.ens.transcriptome.nividic.platform.workflow.Algorithm;
import fr.ens.transcriptome.nividic.platform.workflow.WorkflowElement;
import fr.ens.transcriptome.nividic.util.SystemUtils;
import fr.ens.transcriptome.nividic.util.gui.BrowserWidget;
import fr.ens.transcriptome.nividic.util.gui.ExternalBrowserLinkListener;

/**
 * @author Laurent Jourdren
 * @author Christian Kaufhold swing@chka.de
 */
public class TestSuitePanel extends JPanel implements TableModelListener {

  // For log system
  // private static Logger log = Logger.getLogger(TestSuitePanel.class);

  private QualityTestSuiteTableModel model;
  private JTable table;
  private QualityTestSuiteURL url;
  private JButton saveButton = new JButton("Save Test suite");
  private JButton startButton = new JButton("Start Test suite");
  private static final String tableTipText = "Right click or double click to modify the test suite.";
  private JLabel label = new JLabel(tableTipText, SwingConstants.RIGHT);

  private static final int DOCUMENTATION_DIALOG_WIDTH = 700;
  private static final int DOCUMENTATION_DIALOG_HEIGHT = 500;

  private void saveTestSuite() {

    int response = JOptionPane.showConfirmDialog(table,
        new String[] {"Save changes ?"}, "Save", JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (response == JOptionPane.YES_OPTION) {

      try {
        Core.getCore().saveWorkflow(getUrl());
      } catch (DoelanException e2) {
        JOptionPane.showMessageDialog(table, "Error while writing the file : "
            + e2.getMessage());
      }

    }
  }

  private ModuleLocation[] getAvailableModules() {

    ModuleLocation[] modules = model.getAvailableModules();

    Set instanciedModules = new HashSet();

    WorkflowElement[] elements = model.getWorkflow().getElements();

    for (int i = 0; i < elements.length; i++) {

      Algorithm algo = elements[i].getAlgorithm();

      if (algo != null && algo instanceof QualityTest && algo instanceof Module)
        instanciedModules.add(algo.getClass().getName());

    }

    ArrayList availableModules = new ArrayList();

    for (int i = 0; i < modules.length; i++) {

      Module m = ModuleManager.getManager().loadModule(modules[i]);

      if (m instanceof QualityTest) {
        QualityTest qt = (QualityTest) m;

        if (qt.isAddable()
            && qt.isShowable()
            && (qt.isUniqueInstance() ? !instanciedModules.contains(qt
                .getClass().getName()) : true))
          availableModules.add(modules[i]);

      }
    }

    ModuleLocation[] result = new ModuleLocation[availableModules.size()];
    for (int i = 0; i < availableModules.size(); i++) {
      result[i] = (ModuleLocation) availableModules.get(i);
    }

    return result;
  }

  /**
   * This class is used to handle mouse events on a TableWidgetObject.
   * @author Laurent Jourdren
   */
  class JTableButtonMouseListener implements MouseListener {

    private JTable table;
    private EditParametersWidget editParameter;
    private NewWorkflowElementWidget addAlgo;
    final QualityTestSuiteTableModel ts = model;

    private void forwardEventToButton(final MouseEvent e) {

      if (DoelanRegistery.isAppletMode()
          || (e.getButton() != MouseEvent.BUTTON3 && !(e.getClickCount() == 2 && e
              .getButton() == MouseEvent.BUTTON1)))

        return;

      final TableColumnModel columnModel = this.table.getColumnModel();
      final int column = columnModel.getColumnIndexAtX(e.getX());
      final int row = e.getY() / this.table.getRowHeight();

      // MouseEvent buttonEvent;

      if (row >= this.table.getRowCount() || row < 0
          || column >= this.table.getColumnCount() || column < 0)
        return;

      final QualityTest t;

      t = (QualityTest) model.getAlgorithmAt(row);

      final int posX = e.getX();
      final int posY = e.getY();

      final JPopupMenu menu = new JPopupMenu();

      // Modify a parameter
      AbstractAction modifyAction = new AbstractAction("Modify the parameters") {
        public void actionPerformed(final ActionEvent e) {
          if (editParameter != null) {
            editParameter.close();
          }
          editParameter = new EditParametersWidget(t);
          editParameter.setLocation(posX, posY);

          editParameter.edit();
        }
      };

      menu.add(modifyAction);
      if (!t.isModifiable())
        modifyAction.setEnabled(false);

      menu.addSeparator();

      // Add a test
      menu.add(new AbstractAction("Add a test") {
        public void actionPerformed(final ActionEvent e) {
          if (addAlgo != null) {
            addAlgo.close();
          }
          addAlgo = new NewWorkflowElementWidget(getAvailableModules());
          addAlgo.setLocation(posX, posY);
          addAlgo.setTitle("Add a new test");
          addAlgo.setModal(true);
          addAlgo.show();

          if (addAlgo.isOk()) {

            if (addAlgo.getIdentifier() == null
                || "".equals(addAlgo.getIdentifier())
                || ts.getWorkflow().contains(addAlgo.getIdentifier())) {
              JOptionPane.showMessageDialog(table, "Invalid dentifier");
            } else {
              WorkflowElement wfe = ts.getWorkflowElementAt(row);
              WorkflowElement newWfe = new WorkflowElement(addAlgo
                  .getIdentifier());
              newWfe.setModuleQuery(new ModuleQuery(addAlgo
                  .getModuleLocationSelected()));
              try {

                ts.getWorkflow().insertAfter(wfe, newWfe);
                ts.getWorkflow().activate();

              } catch (PlatformException e1) {
                JOptionPane.showMessageDialog(table,
                    "Error while adding the new element: " + e1.getMessage());
                e1.printStackTrace();

              }

            }

          }

        }
      });

      // Remove a parameter
      AbstractAction removeAction = new AbstractAction("Remove this test") {
        public void actionPerformed(final ActionEvent e) {
          final int response = JOptionPane.showConfirmDialog(table,
              "Remove this test ?");

          if (response == JOptionPane.YES_OPTION) {
            WorkflowElement wfe = ts.getWorkflowElementAt(row);
            if (wfe != null)
              try {
                ts.getWorkflow().removeElementAndJoinPreviousAndNextElements(
                    wfe);
              } catch (PlatformException e1) {
                JOptionPane.showMessageDialog(table, e1.getMessage());
              }
          }

        }
      };

      menu.add(removeAction);
      if (!t.isDeletable())
        removeAction.setEnabled(false);

      /*
       * menu.addSeparator(); menu.add(new AbstractAction("Save test suite") {
       * public void actionPerformed(final ActionEvent e) { saveTestSuite(); }
       * });
       */

      menu.addSeparator();

      AbstractAction documentationAction = new AbstractAction(
          "Test documentation") {

        public void actionPerformed(final ActionEvent e) {

          WorkflowElement wfe = ts.getWorkflowElementAt(row);
          Algorithm a = wfe.getAlgorithm();

          if (a instanceof Module) {

            AboutModule am = ((Module) a).aboutModule();

            final JDialog dialog = new JDialog();
            dialog.setTitle(am.getName() + " documentation");

            String doc;
            if (am.getHTMLDocumentation() == null) {
              StringBuffer sb = new StringBuffer();
              sb.append("<html><body>");
              sb.append("<h1>Information</h1>");
              sb.append("<p>No documentation is available for this module</p>");

              if (am.getWebsite() != null) {
                sb
                    .append("<p>Perhaps more information is available on the <a href=\"");
                sb.append(am.getWebsite());
                sb.append("\">website</a> of the test.</p>");
              }

              sb.append("<br><hr><a href='");
              sb.append(DoelanRegistery.getAppURL());
              sb.append("'>");
              sb.append(Defaults.APP_NAME);
              sb.append("</a> ");
              sb.append(DoelanRegistery.getAppVersion());
              sb.append(", Copyright " + DoelanRegistery.getCopyrightDate()
                  + " " + "<a href=\"" + DoelanRegistery.getOrganizationURL()
                  + "\">" + DoelanRegistery.getOrganizationName() + "</a>.\n");
              sb.append("<!-- PRINT COMMAND HERE -->");
              sb.append("</body></html>");

              doc = sb.toString();

            } else
              doc = am.getHTMLDocumentation();

            final Container pane = dialog.getContentPane();
            pane.setLayout(new BorderLayout());

            BrowserWidget browser = new BrowserWidget(doc);

            browser.setHyperLinkListener(new ExternalBrowserLinkListener());

            JScrollPane jsp = new JScrollPane(browser.getComponent());
            jsp
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            pane.add(jsp, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {

              public void actionPerformed(final ActionEvent e) {
                dialog.setVisible(false);
              }
            });

            pane.add(closeButton, BorderLayout.SOUTH);

            dialog.setSize(DOCUMENTATION_DIALOG_WIDTH,
                DOCUMENTATION_DIALOG_HEIGHT);
            dialog.setModal(true);
            dialog.setVisible(true);

          } else
            JOptionPane.showMessageDialog(table, "This test is not a module");

        }

      };

      menu.add(documentationAction);

      AbstractAction aboutAction = new AbstractAction("About test") {

        public void actionPerformed(final ActionEvent e) {

          WorkflowElement wfe = ts.getWorkflowElementAt(row);
          Algorithm a = wfe.getAlgorithm();

          if (a instanceof Module) {

            AboutModule am = ((Module) a).aboutModule();

            final JDialog dialog = new JDialog();
            dialog.setTitle("About " + am.getName());

            Container infoPanel = dialog.getContentPane();

            infoPanel.setLayout(new BorderLayout());
            infoPanel.add(new AboutModuleWidget(am), BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            infoPanel.add(closeButton, BorderLayout.SOUTH);
            closeButton.addActionListener(new ActionListener() {

              public void actionPerformed(final ActionEvent e) {
                dialog.setVisible(false);
              }
            });

            dialog.pack();
            dialog.setModal(true);
            dialog.setResizable(true);
            dialog.setVisible(true);

          } else
            JOptionPane.showMessageDialog(table, "This test is not a module");

        }

      };

      menu.add(aboutAction);

      menu.show(table.getComponentAt(e.getX(), e.getY()), e.getX(), e.getY());

    }

    public JTableButtonMouseListener(final JTable table) {
      this.table = table;
    }

    public void mouseClicked(final MouseEvent e) {
      forwardEventToButton(e);
    }

    public void mouseEntered(final MouseEvent e) {
      // forwardEventToButton(e);
    }

    public void mouseExited(final MouseEvent e) {
      // forwardEventToButton(e);
    }

    public void mousePressed(final MouseEvent e) {
      // forwardEventToButton(e);
    }

    public void mouseReleased(final MouseEvent e) {
      // forwardEventToButton(e);
    }

  }

  //
  // Getters
  //

  /**
   * Get the url of the test suite
   * @return Returns the url
   */
  public QualityTestSuiteURL getUrl() {
    return url;
  }

  //
  // Setters
  //

  /**
   * Set the url of the test suite
   * @param url The url to set
   */
  public void setUrl(final QualityTestSuiteURL url) {
    this.url = url;
  }

  /**
   * Enable the start button.
   * @param enable true if the start button must be enabled
   */
  public void setStartButtonEnable(final boolean enable) {
    this.startButton.setEnabled(enable);
  }

  //
  // Other methods
  //

  /**
   * Clear the result column.
   */
  public void clearResults() {
    if (getModel() == null)
      return;
    getModel().clearResults();

    int size = getModel().getRowCount();
    for (int i = 0; i < size; i++) {
      getModel().fireTableCellUpdated(i, 3);
      getModel().fireTableCellUpdated(i, 2);
    }
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public TestSuitePanel() {
    super();

    this.saveButton.setToolTipText(tableTipText);
    this.startButton.setToolTipText(tableTipText);

    if (!SystemUtils.isMacOsX()) {
      this.saveButton.setMnemonic(KeyEvent.VK_V);
      this.startButton.setMnemonic(KeyEvent.VK_S);
    }

    // add(new JScrollPane(this.table));
    setLayout(new BorderLayout());
    this.model = new QualityTestSuiteTableModel();

    this.model.addTableModelListener(this);

    this.table = new JTable(model) {

      // Implement table cell tool tips.
      public String getToolTipText(MouseEvent e) {
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);

        if (realColumnIndex == 1) {

          QualityTestSuiteTableModel model = (QualityTestSuiteTableModel) getModel();

          return model.getTip(rowIndex);
        }

        return null;
      }
    };
    this.table
        .setToolTipText("Right click or double click to modify the test suite.");
    this.table.addMouseListener(new JTableButtonMouseListener(this.table));
    JScrollPane sPane = new JScrollPane(this.table);

    // sPane.setPreferredSize(new Dimension(750, 500));
    add(sPane, BorderLayout.CENTER);
    JPanel southPanel = new JPanel(new BorderLayout());
    JPanel buttonsPanel = new JPanel();

    if (!DoelanRegistery.isAppletMode()) {

      buttonsPanel.add(startButton);
      startButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          Core.getCore().getMainTab().start();
        }

      });

      saveButton.setEnabled(false);
      saveButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          saveTestSuite();
        }

      });

      buttonsPanel.add(saveButton);

    }

    southPanel.add(buttonsPanel, BorderLayout.WEST);
    southPanel.add(label, BorderLayout.EAST);

    add(southPanel, BorderLayout.SOUTH);

    if (!DoelanRegistery.isAppletMode())
      sPane.addMouseListener(new MouseListener() {

        // Private fields
        private NewWorkflowElementWidget addAlgo;
        final QualityTestSuiteTableModel ts = model;

        private void forwardEventToButton(final MouseEvent e) {

          if (ts.getWorkflow() == null) {
            JOptionPane.showMessageDialog(table,
                "You must create or load a test suite");
            return;
          }
          if (model.getRowCount() != 0)
            return;

          final JPopupMenu menu = new JPopupMenu();
          final int posX = e.getX();
          final int posY = e.getY();

          menu.add(new AbstractAction("Add a test") {
            public void actionPerformed(final ActionEvent e) {

              menu.setVisible(false);
              if (addAlgo != null) {
                addAlgo.close();
              }

              addAlgo = new NewWorkflowElementWidget(getAvailableModules());
              addAlgo.setTitle("Add a new test");
              addAlgo.setModal(true);
              addAlgo.setLocation(posX, posY);
              addAlgo.show();

              if (addAlgo.isOk()) {

                if (addAlgo.getIdentifier() == null
                    || "".equals(addAlgo.getIdentifier())
                    || ts.getWorkflow().contains(addAlgo.getIdentifier())) {
                  JOptionPane.showMessageDialog(table, "Invalid dentifier");
                } else {
                  WorkflowElement wfe = ts.getWorkflow().getElement(
                      Core.LOAD_ALGORITHM);
                  if (wfe == null) {
                    JOptionPane.showMessageDialog(table,
                        "Can't find load data algorithm");
                    return;
                  }

                  WorkflowElement newWfe = new WorkflowElement(addAlgo
                      .getIdentifier());
                  newWfe.setModuleQuery(new ModuleQuery(addAlgo
                      .getModuleLocationSelected()));
                  try {

                    ts.getWorkflow().insertAfter(wfe, newWfe);
                    ts.getWorkflow().activate();

                  } catch (PlatformException e1) {
                    JOptionPane.showMessageDialog(table,
                        "Error while adding the new element: "
                            + e1.getMessage());
                  }

                }
              }
            }
          });
          menu.show(table, posX, posY);
        }

        public void mouseClicked(final MouseEvent e) {
          forwardEventToButton(e);
        }

        public void mouseEntered(final MouseEvent e) {
          // forwardEventToButton(e);
        }

        public void mouseExited(final MouseEvent e) {
          // forwardEventToButton(e);
        }

        public void mousePressed(final MouseEvent e) {
          // forwardEventToButton(e);
        }

        public void mouseReleased(final MouseEvent e) {
          // forwardEventToButton(e);
        }

      });

  }

  /**
   * Calculate the number of column to display.
   * @param table Table object
   */
  public static void calcColumnWidths(final JTable table) {

    JTableHeader header = table.getTableHeader();

    TableCellRenderer defaultHeaderRenderer = null;

    if (header != null)
      defaultHeaderRenderer = header.getDefaultRenderer();

    TableColumnModel columns = table.getColumnModel();
    TableModel data = table.getModel();

    int margin = columns.getColumnMargin(); // only
    // JDK1.3

    int rowCount = data.getRowCount();

    int totalWidth = 0;

    for (int i = columns.getColumnCount() - 1; i >= 0; --i) {
      TableColumn column = columns.getColumn(i);

      int columnIndex = column.getModelIndex();

      int width = -1;

      TableCellRenderer h = column.getHeaderRenderer();

      if (h == null)
        h = defaultHeaderRenderer;

      // Not explicitly impossible
      if (h != null) {
        Component c = h.getTableCellRendererComponent(table, column
            .getHeaderValue(), false, false, -1, i);

        width = c.getPreferredSize().width;
      }

      for (int row = rowCount - 1; row >= 0; --row) {
        TableCellRenderer r = table.getCellRenderer(row, i);

        Component c = r.getTableCellRendererComponent(table, data.getValueAt(
            row, columnIndex), false, false, row, i);

        width = Math.max(width, c.getPreferredSize().width);
      }

      if (width >= 0)
        column.setPreferredWidth(width + margin); // <1.3:
      // without
      // margin
      // else
      // ; // ???

      totalWidth += column.getPreferredWidth();
    }

    // only <1.3: totalWidth += columns.getColumnCount() *
    // columns.getColumnMargin();

    /*
     * If you like; This does not make sense for two many columns! Dimension
     * size = table.getPreferredScrollableViewportSize(); size.width =
     * totalWidth; table.setPreferredScrollableViewportSize(size);
     */

    // table.sizeColumnsToFit(-1); <1.3; possibly even table.revalidate()
    // if (header != null)
    // header.repaint(); only makes sense when the header is visible (only <1.3)
  }

  /**
   * Get the model of the table
   * @return Returns the model
   */
  public QualityTestSuiteTableModel getModel() {
    return model;
  }

  /**
   * This method is call when the table has been changed.
   * @param e the event
   */
  public void tableChanged(final TableModelEvent e) {
    calcColumnWidths(this.table);
    if (getModel().getRowCount() == 0)
      this.saveButton.setEnabled(false);
    else
      this.saveButton.setEnabled(true);
  }

}