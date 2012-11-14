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
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.event.ActionEvent;

import static org.eknet.neoswing.utils.NeoSwingUtil.addEdge;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 20:23
 */
public class ExpandNodeAction extends AbstractSwingAction {

  private final Vertex node;
  private final VisualizationViewer<Vertex, Edge> viewer;
  private final Graph<Vertex, Edge> graph;
  private final Direction direction;

  public ExpandNodeAction(Vertex node, GraphModel graphModel, Direction direction) {
    this.node = node;
    this.direction = direction;
    this.viewer = graphModel.getViewer();
    this.graph = graphModel.getGraph();

    putValue(NAME, "Expand " + direction.name());
    putValue(SMALL_ICON, NeoSwingUtil.icon("arrow_out"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    for (Edge relationship : node.getEdges(direction)) {
      addEdge(graph, relationship);
    }
    viewer.repaint();
  }
}
