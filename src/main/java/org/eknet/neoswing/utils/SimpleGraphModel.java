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

package org.eknet.neoswing.utils;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.VisualizationViewFactory;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 12:35
 */
public class SimpleGraphModel implements GraphModel {

  private Graph<Vertex, Edge> graph;
  private VisualizationViewer<Vertex, Edge> viewer;
  private GraphDb database;

  public SimpleGraphModel(Graph<Vertex, Edge> graph, VisualizationViewer<Vertex, Edge> viewer, GraphDb database) {
    this.graph = graph;
    this.viewer = viewer;
    this.database = database;
  }

  public SimpleGraphModel(GraphDb db, VisualizationViewFactory factory) {
    this.graph = new DirectedSparseMultigraph<Vertex, Edge>();
    this.viewer = factory.createViewer(this.graph, db);
    this.database = db;
  }

  @Override
  public Graph<Vertex, Edge> getGraph() {
    return graph;
  }

  @Override
  public VisualizationViewer<Vertex, Edge> getViewer() {
    return viewer;
  }

  @Override
  public GraphDb getDatabase() {
    return database;
  }

  public void setDatabase(GraphDb database) {
    this.database = database;
  }

  public void setViewer(VisualizationViewer<Vertex, Edge> viewer) {
    this.viewer = viewer;
  }

  public void setGraph(Graph<Vertex, Edge> graph) {
    this.graph = graph;
  }

  @Override
  public <A, B> void execute(DbAction<A, B> action) {
    action.setModel(this);
    action.execute();
  }
}
