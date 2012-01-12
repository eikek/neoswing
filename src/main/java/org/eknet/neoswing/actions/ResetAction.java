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
import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.jung.graph.Graph;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.NeoSwingUtil;

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
    Graph<Node, Relationship> graph = graphModel.getGraph();
    List<Relationship> relationships = new ArrayList<Relationship>(graph.getEdges());
    for (Relationship rel : relationships) {
      graph.removeEdge(rel);
      graph.removeVertex(rel.getEndNode());
      graph.removeVertex(rel.getStartNode());
    }
    List<Node> nodes = new ArrayList<Node>(graph.getVertices());
    for (Node node : nodes) {
      graph.removeVertex(node);
    }
    if (showReferenceNode) {
      if (!graph.containsVertex(graphModel.getDatabase().getReferenceNode())) {
        graph.addVertex(graphModel.getDatabase().getReferenceNode());
      }
    }
    graphModel.getViewer().repaint();
  }
}
