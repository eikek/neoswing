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

package org.eknet.neoswing.actions;

import java.awt.event.ActionEvent;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.NeoSwingUtil;
import static org.eknet.neoswing.utils.NeoSwingUtil.addEdge;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 20:23
 */
public class ExpandNodeAction extends AbstractSwingAction {

  private final Node node;
  private final VisualizationViewer<Node, Relationship> viewer;
  private final Graph<Node, Relationship> graph;
  private final Direction direction;

  public ExpandNodeAction(Node node, GraphModel graphModel, Direction direction) {
    this.node = node;
    this.direction = direction;
    this.viewer = graphModel.getViewer();
    this.graph = graphModel.getGraph();

    putValue(NAME, "Expand " + direction.name());
    putValue(SMALL_ICON, NeoSwingUtil.icon("arrow_out"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    for (Relationship relationship : node.getRelationships(direction)) {
      addEdge(graph, relationship);
    }
    viewer.repaint();
  }
}
