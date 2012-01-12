/*
 * Copyright (c) 2012 Eike Kettner
 *
 * This file is part of NeoSwing.
 *
 * NeoSwing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NeoSwing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NeoSwing.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eknet.neoswing.view;

import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.NeoSwing;
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 17:44
 */
public class MultiGraphViewer extends JPanel {

  private final static Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);
  
  private final ComponentFactory factory;
  private JTabbedPane graphs;

  public MultiGraphViewer(ComponentFactory factory) {
    super(new BorderLayout(), true);
    this.factory = factory;
    initComponents();
  }

  protected void initComponents() {
    JPanel container = new JPanel(new BorderLayout());
    this.graphs = factory.createTabbedPane();
    container.add(this.graphs, BorderLayout.CENTER);
    add(container, BorderLayout.CENTER);

    JToolBar bar = createToolbar(factory);
    if (bar != null) {
      add(bar, BorderLayout.NORTH);
    }
  }
  
  protected JToolBar createToolbar(ComponentFactory factory) {
    JToolBar bar = factory.createToolbar();

    JButton openButton = factory.createToolbarButton();
    openButton.setAction(new OpenDatabaseAction());
    bar.add(openButton);

    return bar;
  }

  public void openDatabase(@NotNull GraphDatabaseService db, @Nullable String name) {
    if (name == null) {
      name = "NeoDb " + graphs.getTabCount();
    }
    GraphViewer panel = new GraphViewer(db, factory);
    graphs.addTab(name, panel);
  }

  public void openDatabase(@NotNull File directory) {
    final GraphDatabaseService db = new EmbeddedGraphDatabase(directory.getAbsolutePath());
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        db.shutdown();
      }
    }));
    openDatabase(db, directory.getName());
  }

  public void openDatabase(@NotNull GraphDatabaseService db) {
    openDatabase(db, null);
  }

  private final class OpenDatabaseAction extends AbstractAction {

    private OpenDatabaseAction() {
      putValue(NAME, "Open Database...");
      putValue(SHORT_DESCRIPTION, "Open a new database from the file system");
      putValue(SMALL_ICON, NeoSwingUtil.icon("folder_database"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      final String key = "neoswing.multigraphview.lastlocation";
      String lastLocation = prefs.get(key, null);
      JFileChooser fc = new JFileChooser(lastLocation);
      fc.setDialogTitle("Open Neo4j Database");
      fc.setAcceptAllFileFilterUsed(false);
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fc.setMultiSelectionEnabled(false);
      JLabel info = new JLabel("<html><p>Select a valid Neo4J database<br>" +
          "directory or an empty one, in<br>which case a new database is<br>" +
          "created.</p></html>");
      info.setVerticalAlignment(SwingConstants.TOP);
      info.setVerticalTextPosition(SwingConstants.TOP);
      fc.setAccessory(info);
      Window owner = NeoSwingUtil.findOwner(e.getSource());
      int rc = fc.showOpenDialog(owner);
      if (rc == JFileChooser.APPROVE_OPTION) {
        File f = fc.getSelectedFile();
        if (f == null) {
          return;
        }
        try {
          NeoSwingUtil.checkDatabaseDirectory(f);
          prefs.put(key, fc.getSelectedFile().getAbsolutePath());
          openDatabase(fc.getSelectedFile());
        } catch (IllegalArgumentException error) {
          Dialogs.error(owner, error.getMessage());
        }
      }
    }
  }
}
