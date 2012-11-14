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

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import edu.uci.ics.jung.graph.Graph;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.view.SearchView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 16:23
 */
public class SearchAction extends AbstractSwingAction {
  private static final Logger log = LoggerFactory.getLogger(SearchAction.class);
  
  private GraphDb db;
  private Graph<Vertex, Edge> graph;
  private SearchView searchView;
  
  public SearchAction(GraphDb db, Graph<Vertex, Edge> graph) {
    this.db = db;
    this.graph = graph;
    this.searchView = new SearchView();
    putValue(NAME, "Search nodes or relationships");
    putValue(SHORT_DESCRIPTION, "Search nodes or relationships");
    putValue(SMALL_ICON, NeoSwingUtil.icon("find"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Dialog dialog = new Dialog("Select index");
    dialog.setContent(searchView.getRoot());
    Dialog.Option option = dialog.show(getWindow(e), java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    if (option != Dialog.Option.OK) {
      return;
    }
    String type = searchView.getType();
    String key = searchView.getKey();
    Object value = searchView.getValue();
    GraphDb.Tx tx = db.beginTx();
    try {
      if (type.equals("Vertex")) {
        if (key == null || key.isEmpty() || value == null) {
          for (Vertex v : db.getVertices()) {
            graph.addVertex(v);
          }
        } else {
          for (Vertex v : db.getVertices(key, value)) {
            graph.addVertex(v);
          }
        }
      } else {
        if (key == null || key.isEmpty() || value == null) {
          for (Edge edge : db.getEdges()) {
            NeoSwingUtil.addEdge(graph,  edge);
          }
        } else {
          for (Edge edge : db.getEdges(searchView.getKey(), searchView.getValue())) {
            NeoSwingUtil.addEdge(graph, edge);
          }
        }
      }
      tx.success();
    } catch (Exception error) {
      log.error("Error searching index", error);
      Dialogs.error(getWindow(e), "Error searching. Make sure you used the right syntax.\n\nMessage: " + error.getLocalizedMessage());
    } finally {
      tx.finish();
    }
  }
}
