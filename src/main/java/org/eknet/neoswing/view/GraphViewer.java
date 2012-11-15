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

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.ElementId;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.NeoSwing;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.WindowUtil;
import org.eknet.neoswing.view.control.SelectElementMousePlugin;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

/**
 * Combines the {@link GraphPanel} view with a property editor.
 * 
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 20:32
 */
public class GraphViewer extends JPanel implements GraphModel {

  private static final Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);

  private final GraphPanel graphPanel;
  private final ComponentFactory componentFactory;
  
  private PropertiesPanel propertiesPanel;
  private RelationTypesPanel relationTypesPanel;

  private final PropertyChangeListener panelUpdateListener = new PropertyChangeListener() {
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals(SelectElementMousePlugin.PROPERTY_ELEMENT)) {
        ElementId<?> selected = (ElementId<?>) evt.getNewValue();
        if (selected.isVertex()) {
          //noinspection unchecked
          relationTypesPanel.setNodeId((ElementId<Vertex>) selected);
        } else {
          relationTypesPanel.setNodeId(null);
        }
        propertiesPanel.setElement(selected);
      }
    }
  };

  public GraphViewer(GraphDb db, ComponentFactory componentFactory) {
    DefaultVisualizationViewFactory factory = new DefaultVisualizationViewFactory() {
      @Override
      protected void addMousePlugins(DefaultModalGraphMouse<Vertex, Edge> plugin, GraphModel graphModel) {
        super.addMousePlugins(plugin, graphModel);
        SelectElementMousePlugin selectPlugin = new SelectElementMousePlugin(MouseEvent.BUTTON1);
        selectPlugin.addPropertyChangeListener(panelUpdateListener);
        plugin.add(selectPlugin);
      }
    };
    this.graphPanel = new GraphPanel(db, factory, componentFactory);
    this.componentFactory = componentFactory;
    initComponents();
  }

  public GraphViewer(GraphDb db) {
    this(db, NeoSwingUtil.getFactory(true));
  }

  private void initComponents() {
    setLayout(new BorderLayout(5, 5));

    JSplitPane vsplit = componentFactory.createSplitPane();
    vsplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
    propertiesPanel = new PropertiesPanel(graphPanel, componentFactory, null);
    vsplit.setLeftComponent(new JScrollPane(propertiesPanel));
    propertiesPanel.setPreferredSize(new Dimension(200, 50));
    relationTypesPanel = new RelationTypesPanel(graphPanel, componentFactory);
    vsplit.setRightComponent(new JScrollPane(relationTypesPanel));
    relationTypesPanel.setPreferredSize(new Dimension(200, 50));
    vsplit.setOneTouchExpandable(true);
    vsplit.setContinuousLayout(true);
    vsplit.setDividerLocation(-1);
    WindowUtil.bindDividerLocationToPrefs(vsplit, prefs, "graphviewer.vsplit.%s");

    JPanel right = componentFactory.createPanel();
    right.setLayout(new BorderLayout());
    right.add(vsplit, BorderLayout.CENTER);
    right.setPreferredSize(new Dimension(150, right.getPreferredSize().height));

    JSplitPane msplit = componentFactory.createSplitPane();
    msplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    msplit.setLeftComponent(graphPanel);
    msplit.setRightComponent(right);
    msplit.setContinuousLayout(true);
    msplit.setOneTouchExpandable(true);
    msplit.setDividerLocation(-1);
    WindowUtil.bindDividerLocationToPrefs(vsplit, prefs, "graphviewer.msplit.%s");
    graphPanel.setPreferredSize(new Dimension(900, 900));
    add(msplit, BorderLayout.CENTER);
  }

  public void close() {
    graphPanel.getDatabase().shutdown();
  }

  @Override
  public Graph<Vertex, Edge> getGraph() {
    return graphPanel.getGraph();
  }

  @Override
  public VisualizationViewer<Vertex, Edge> getViewer() {
    return graphPanel.getViewer();
  }

  @Override
  public GraphDb getDatabase() {
    return graphPanel.getDatabase();
  }

  @Override
  public <A, B> void execute(DbAction<A, B> action) {
    graphPanel.execute(action);
  }
}
