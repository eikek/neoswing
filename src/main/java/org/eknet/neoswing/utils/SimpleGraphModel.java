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

package org.eknet.neoswing.utils;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.VisualizationViewFactory;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 12:35
 */
public class SimpleGraphModel implements GraphModel {

  private Graph<Node, Relationship> graph;
  private VisualizationViewer<Node, Relationship> viewer;
  private GraphDatabaseService database;

  public SimpleGraphModel(Graph<Node, Relationship> graph, VisualizationViewer<Node, Relationship> viewer, GraphDatabaseService database) {
    this.graph = graph;
    this.viewer = viewer;
    this.database = database;
  }

  public SimpleGraphModel(GraphDatabaseService db, VisualizationViewFactory factory) {
    this.graph = new DirectedSparseMultigraph<Node, Relationship>();
    this.viewer = factory.createViewer(this.graph, db);
    this.database = db;
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
    return database;
  }

  public void setDatabase(@NotNull GraphDatabaseService database) {
    this.database = database;
  }

  public void setViewer(VisualizationViewer<Node, Relationship> viewer) {
    this.viewer = viewer;
  }

  public void setGraph(Graph<Node, Relationship> graph) {
    this.graph = graph;
  }
}
