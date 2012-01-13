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

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.NeoSwing;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.WindowUtil;
import org.eknet.neoswing.view.control.SelectElementMousePlugin;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

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
      if (evt.getPropertyName().equals(SelectElementMousePlugin.PROPERTY_NODE)) {
        Node node = (Node) evt.getNewValue();
        relationTypesPanel.setNode(node);
        propertiesPanel.setElement(node);
      }
      if (evt.getPropertyName().equals(SelectElementMousePlugin.PROPERTY_RELATIONSHIP)) {
        PropertyContainer el = (PropertyContainer) evt.getNewValue();
        propertiesPanel.setElement(el);
        relationTypesPanel.setNode(null);
      }
    }
  };

  public GraphViewer(GraphDatabaseService db, ComponentFactory componentFactory) {
    DefaultVisualizationViewFactory factory = new DefaultVisualizationViewFactory() {
      @Override
      protected void addMousePlugins(DefaultModalGraphMouse<Node, Relationship> plugin, GraphModel graphModel) {
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

  public GraphViewer(GraphDatabaseService db) {
    this(db, NeoSwingUtil.getFactory(true));
  }

  private void initComponents() {
    setLayout(new BorderLayout(5, 5));

    JSplitPane vsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    propertiesPanel = new PropertiesPanel(componentFactory, null);
    vsplit.setLeftComponent(new JScrollPane(propertiesPanel));
    propertiesPanel.setPreferredSize(new Dimension(200, 50));
    relationTypesPanel = new RelationTypesPanel(componentFactory);
    vsplit.setRightComponent(new JScrollPane(relationTypesPanel));
    relationTypesPanel.setPreferredSize(new Dimension(200, 50));
    vsplit.setOneTouchExpandable(true);
    vsplit.setContinuousLayout(true);
    vsplit.setDividerLocation(-1);
    WindowUtil.bindDividerLocationToPrefs(vsplit, prefs, "graphviewer.vsplit.%s");

    JPanel right = new JPanel(new BorderLayout());
    right.add(vsplit, BorderLayout.CENTER);
    right.setPreferredSize(new Dimension(150, right.getPreferredSize().height));

    JSplitPane msplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
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
  @NotNull
  public Graph<Node, Relationship> getGraph() {
    return graphPanel.getGraph();
  }

  @Override
  @NotNull
  public VisualizationViewer<Node, Relationship> getViewer() {
    return graphPanel.getViewer();
  }

  @Override
  @NotNull
  public GraphDatabaseService getDatabase() {
    return graphPanel.getDatabase();
  }
}
