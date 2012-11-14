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

package org.eknet.neoswing.actions;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import edu.uci.ics.jung.graph.Graph;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 21:22
 */
public class ResetAction extends AbstractSwingAction {

  private final GraphModel graphModel;
  private final boolean showReferenceNode;

  public ResetAction(GraphModel graphModel) {
    this(graphModel, true);
  }
  public ResetAction(GraphModel graphModel, boolean showReferenceNode) {
    this.graphModel = graphModel;
    this.showReferenceNode = showReferenceNode;

    putValue(SHORT_DESCRIPTION, "Reset view");
    putValue(NAME, "Reset view");
    putValue(SMALL_ICON, NeoSwingUtil.icon("arrow_refresh"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Graph<Vertex, Edge> graph = graphModel.getGraph();
    List<Edge> relationships = new ArrayList<Edge>(graph.getEdges());
    for (Edge rel : relationships) {
      graph.removeEdge(rel);
      graph.removeVertex(rel.getVertex(Direction.IN));
      graph.removeVertex(rel.getVertex(Direction.OUT));
    }
    List<Vertex> nodes = new ArrayList<Vertex>(graph.getVertices());
    for (Vertex node : nodes) {
      graph.removeVertex(node);
    }
    if (showReferenceNode) {
//      if (!graph.containsVertex(graphModel.getDatabase().getReferenceNode())) {
//        graph.addVertex(graphModel.getDatabase().getReferenceNode());
//      }
    }
    graphModel.getViewer().repaint();
  }
}
