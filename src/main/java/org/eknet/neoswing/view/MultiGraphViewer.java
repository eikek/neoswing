/*
 * Copyright 2012 Eike Kettner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eknet.neoswing.view;

import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.loader.GraphLoaderManager;
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Organizes {@link GraphViewer}s in a tabbed pane.
 * 
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 17:44
 */
public class MultiGraphViewer extends JPanel {
  private final static Logger log = LoggerFactory.getLogger(MultiGraphViewer.class);

  private final GraphLoaderManager loaderManager;
  private final ComponentFactory factory;
  private JTabbedPane graphs;

  public MultiGraphViewer(ComponentFactory factory, GraphLoaderManager loaderManager) {
    super(new BorderLayout(), true);
    this.factory = factory;
    this.loaderManager = loaderManager;
    initComponents();
  }

  protected void initComponents() {
    JPanel container = factory.createPanel();
    container.setLayout(new BorderLayout());
    this.graphs = factory.createTabbedPane(new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Component c = (Component) e.getSource();
        if (c instanceof GraphViewer) {
          GraphViewer viewer = (GraphViewer) c;
          viewer.getDatabase().shutdown();
        }
        graphs.remove(c);
      }
    });
    container.add(this.graphs, BorderLayout.CENTER);
    add(container, BorderLayout.CENTER);

    JToolBar bar = createToolbar(factory);
    if (bar != null) {
      add(bar, BorderLayout.NORTH);
    }
  }
  
  protected JToolBar createToolbar(ComponentFactory factory) {
    JToolBar bar = factory.createToolbar();
    for (String name : loaderManager.getRegisteredLoaders()) {
      JButton openButton = factory.createToolbarButton();
      openButton.setAction(new OpenDatabaseAction(name));
      bar.add(openButton);
    }

    bar.add(Box.createHorizontalGlue());
    JComboBox lafbox = new LaFComboBox(factory).getComponent();
    lafbox.setMaximumSize(new Dimension(250, lafbox.getMaximumSize().height));
    bar.add(lafbox);

    return bar;
  }

  public void close() {
    for (int i = 0; i < graphs.getTabCount(); i++) {
      Component c = graphs.getTabComponentAt(i);
      if (!(c instanceof GraphViewer)) {
        c = graphs.getComponentAt(i);
      }
      if (c instanceof GraphViewer) {
        GraphViewer viewer = (GraphViewer) c;
        log.info("Closing viewer " + i + " ...");
        viewer.close();
      }
    }
  }

  public void openDatabase(GraphDb db, String tabName, final Icon icon) {
    final String name;
    if (tabName == null) {
      name = "Db " + graphs.getTabCount();
    } else {
      name = tabName;
    }
    GraphViewer panel = new GraphViewer(db, factory);
    graphs.addTab(name, icon, panel);
  }

  public void openDatabase(String type, File directory) {
    final GraphDb db = new GraphDb(loaderManager.loadGraph(type, directory.getAbsolutePath()));
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        db.shutdown();
      }
    }));
    openDatabase(db, directory.getName(), NeoSwingUtil.graphDbIcon(type));
  }


  private final class OpenDatabaseAction extends AbstractAction {

    private final String name;
    private OpenDatabaseAction(String name) {
      this.name = name;
      putValue(NAME, "Open Database...");
      putValue(SHORT_DESCRIPTION, "Open a new database from the file system");
      putValue(SMALL_ICON, NeoSwingUtil.graphDbIcon(name));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      final String key = "neoswing.multigraphview.lastlocation";
      String lastLocation = NeoSwingUtil.getPrefs().get(key, null);
      JFileChooser fc = new JFileChooser(lastLocation);
      fc.setDialogTitle("Open Database");
      fc.setAcceptAllFileFilterUsed(false);
      fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      fc.setMultiSelectionEnabled(false);
      JLabel info = new JLabel("<html><p>Select a valid database<br>" +
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
          NeoSwingUtil.getPrefs().put(key, fc.getSelectedFile().getAbsolutePath());
          openDatabase(name, fc.getSelectedFile());
        } catch (IllegalArgumentException error) {
          Dialogs.error(owner, error.getMessage());
        }
      }
    }
  }
}
