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

package org.eknet.neoswing.view.control;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import org.eknet.neoswing.ElementId;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.actions.ExpandNodeAction;
import org.eknet.neoswing.actions.ResetAction;
import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

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
      VisualizationViewer<Vertex, Edge> viewer = getViewer(e);
      assert viewer == graphModel.getViewer();
      down = e.getPoint();
      Vertex node = viewer.getPickSupport().getVertex(viewer.getGraphLayout(), down.getX(), down.getY());
      if (node != null) {
        showNeighborhood(node);
      }
    }
  }

  public void showNeighborhood(final Vertex node) {
    Runnable animator = new Runnable() {
      public void run() {
        ResetAction resetAction = new ResetAction(graphModel, false);
        NeoSwingUtil.invoke(resetAction, graphModel.getViewer());

        Graph<Vertex, Edge> graph = graphModel.getGraph();
        graph.addVertex(node);

        ExpandNodeAction expandAction = new ExpandNodeAction(ElementId.vertexId(node), graphModel, Direction.BOTH);
        NeoSwingUtil.invoke(expandAction, graphModel.getViewer());
        graphModel.getViewer().setGraphLayout(new FRLayout2<Vertex, Edge> (graph));
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
  public void centerNode(Vertex vertex) {
    final VisualizationViewer<Vertex, Edge> viewer = graphModel.getViewer();
    Layout<Vertex, Edge> layout = viewer.getGraphLayout();
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
