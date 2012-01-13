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

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.eknet.neoswing.*;
import org.eknet.neoswing.actions.AddNodeAction;
import org.eknet.neoswing.actions.ResetAction;
import org.eknet.neoswing.actions.SearchIndexAction;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A view of one database.
 *<p/>
 * It uses {@link VisualizationViewer} to draw the graph and displays
 * a toolbar that contains actions to
 * <ul>
 *   <li>add new nodes</li>
 *   <li>reset the graph</li>
 *   <li>change layout</li>
 *   <li>find nodes/relationships via index search</li>
 * </ul>
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:31
 */
public class GraphPanel extends JPanel implements GraphModel {

  private final GraphDatabaseService db;
  private final Graph<Node, Relationship> graph;
  private final VisualizationViewer<Node, Relationship> viewer;
  private final ComponentFactory factory;

  public GraphPanel(GraphDatabaseService db) {
    this(db, new DefaultVisualizationViewFactory(), NeoSwingUtil.getFactory(true));
  }

  public GraphPanel(GraphDatabaseService db, VisualizationViewFactory factory, ComponentFactory componentFactory) {
    super(new BorderLayout());
    this.factory = componentFactory;
    this.db = db;
    this.graph = new DirectedSparseGraph<Node, Relationship>();
    this.graph.addVertex(db.getReferenceNode());
    this.viewer = factory.createViewer(graph, db);

    initComponents();
  }

  protected void initComponents() {
    JToolBar bar = createToolbar();
    add(bar, BorderLayout.NORTH);

    GraphZoomScrollPane container = new GraphZoomScrollPane(viewer);
    container.setBorder(BorderFactory.createEtchedBorder());
    add(container, BorderLayout.CENTER);
  }

  protected JToolBar createToolbar() {
    JToolBar bar = factory.createToolbar();

    //layout chooser
    JComboBox box = factory.createComboBox();
    final LayoutComboModel layoutModel = new LayoutComboModel();
    box.setModel(layoutModel);
    box.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        viewer.setGraphLayout(layoutModel.getSelectedItem().createLayout(graph));
      }
    });
    box.setSelectedItem(Layouts.FRLayout);
    box.setMaximumSize(new Dimension(110, 25));
    bar.add(box);
    bar.add(Box.createHorizontalStrut(15));

    // add new node
    JButton addNodeButton = factory.createToolbarButton();
    addNodeButton.setAction(new AddNodeAction(this));
    bar.add(addNodeButton);
    
    // reset graph view
    JButton resetButton = factory.createToolbarButton();
    resetButton.setAction(new ResetAction(this));
    bar.add(resetButton);

    // search index
    JButton findButton = factory.createToolbarButton();
    findButton.setAction(new SearchIndexAction(db, graph));
    bar.add(findButton);
    return bar;
  }

  @NotNull
  @Override
  public Graph<Node, Relationship> getGraph() {
    return graph;
  }

  @NotNull
  @Override
  public VisualizationViewer<Node, Relationship> getViewer() {
    return viewer;
  }

  @NotNull
  @Override
  public GraphDatabaseService getDatabase() {
    return db;
  }

}
