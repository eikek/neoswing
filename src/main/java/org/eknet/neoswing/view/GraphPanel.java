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
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.eknet.neoswing.*;
import org.eknet.neoswing.actions.AddNodeAction;
import org.eknet.neoswing.actions.ResetAction;
import org.eknet.neoswing.actions.SearchAction;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.SimpleGraphModel;

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

  private final GraphModel graphModel;
  private final ComponentFactory factory;

  public GraphPanel(GraphDb db) {
    this(db, new DefaultVisualizationViewFactory(), NeoSwingUtil.getFactory(true));
  }

  public GraphPanel(GraphDb db, VisualizationViewFactory factory, ComponentFactory componentFactory) {
    this(new SimpleGraphModel(db, factory), componentFactory);
  }

  public GraphPanel(GraphModel model, ComponentFactory componentFactory) {
    super(new BorderLayout(), true);
    this.graphModel = model;
    this.factory = componentFactory;
    initComponents();
  }

  protected void initComponents() {
    JToolBar bar = createToolbar();
    add(bar, BorderLayout.NORTH);

    GraphZoomScrollPane container = new GraphZoomScrollPane(getViewer());
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
        getViewer().setGraphLayout(layoutModel.getSelectedItem().createLayout(getGraph()));
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
    findButton.setAction(new SearchAction(graphModel));
    bar.add(findButton);
    return bar;
  }

  @Override
  public Graph<Vertex, Edge> getGraph() {
    return graphModel.getGraph();
  }

  @Override
  public VisualizationViewer<Vertex, Edge> getViewer() {
    return graphModel.getViewer();
  }

  @Override
  public GraphDb getDatabase() {
    return graphModel.getDatabase();
  }

  @Override
  public <A, B> void execute(DbAction<A, B> action) {
    graphModel.execute(action);
  }
}
