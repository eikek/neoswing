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

package org.eknet.neoswing.view.control;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.actions.ExpandNodeAction;
import org.eknet.neoswing.actions.ResetAction;
import org.eknet.neoswing.utils.NeoSwingUtil;

/**
 * Centers the view around the selected node and shows its neighborhood.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 09:54
 */
public class NavigateNodeMousePlugin extends AbstractMousePlugin {

  private final GraphModel graphModel;
  
  public NavigateNodeMousePlugin(GraphModel graphModel) {
    super(0);
    this.graphModel = graphModel;
  }

  @Override
  public boolean checkModifiers(MouseEvent e) {
    return e.getButton()==MouseEvent.BUTTON1 && e.getClickCount() == 2;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (checkModifiers(e)) {
      VisualizationViewer<Node, Relationship> viewer = getViewer(e);
      assert viewer == graphModel.getViewer();
      down = e.getPoint();
      Node node = viewer.getPickSupport().getVertex(viewer.getGraphLayout(), down.getX(), down.getY());
      if (node != null) {
        showNeighborhood(node);
      }
    }
  }

  public void showNeighborhood(final Node node) {
    Runnable animator = new Runnable() {
      public void run() {
        ResetAction resetAction = new ResetAction(graphModel, false);
        NeoSwingUtil.invoke(resetAction, graphModel.getViewer());

        Graph<Node, Relationship> graph = graphModel.getGraph();
        graph.addVertex(node);

        ExpandNodeAction expandAction = new ExpandNodeAction(node, graphModel, Direction.BOTH);
        NeoSwingUtil.invoke(expandAction, graphModel.getViewer());
        graphModel.getViewer().setGraphLayout(new FRLayout2<Node, Relationship> (graph));
        centerNode(node);
      }
    };
    Thread thread = new Thread(animator);
    thread.start();
  }
  
  /**
   * This is copied from {@link AnimatedPickingGraphMousePlugin}
   *
   * @param vertex
   */
  public void centerNode(Node vertex) {
    final VisualizationViewer<Node, Relationship> viewer = graphModel.getViewer();
    Layout<Node, Relationship> layout = viewer.getGraphLayout();
    Point2D q = layout.transform(vertex);
    Point2D lvc = viewer.getRenderContext().getMultiLayerTransformer().inverseTransform(viewer.getCenter());
    final double dx = (lvc.getX() - q.getX()) / 10;
    final double dy = (lvc.getY() - q.getY()) / 10;

    for (int i = 0; i < 10; i++) {
      viewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
      try {
        Thread.sleep(50);
      } catch (InterruptedException ex) {
      }
    }
  }
}
