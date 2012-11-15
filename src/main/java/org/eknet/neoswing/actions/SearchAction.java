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
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.view.SearchView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 16:23
 */
public class SearchAction extends AbstractSwingAction {
  private static final Logger log = LoggerFactory.getLogger(SearchAction.class);
  
  private GraphModel model;
  private SearchView searchView;
  
  public SearchAction(GraphModel model) {
    this.model = model;
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
    final String type = searchView.getType();
    final String key = searchView.getKey();
    final Object value = searchView.getValue();

    model.execute(new DbAction<Object, Element>() {
      @Override
      protected Object doInTx(GraphModel model) {
        if (type.equals("Vertex")) {
          if (key == null || key.isEmpty() || value == null) {
            for (Vertex v : model.getDatabase().getVertices()) {
              publish(v);
            }
          } else {
            for (Vertex v : model.getDatabase().getVertices(key, value)) {
              publish(v);
            }
          }
        } else {
          if (key == null || key.isEmpty() || value == null) {
            for (Edge edge : model.getDatabase().getEdges()) {
              publish(edge);
            }
          } else {
            for (Edge edge : model.getDatabase().getEdges(searchView.getKey(), searchView.getValue())) {
              publish(edge);
            }
          }
        }
        return null;
      }

      @Override
      protected void process(List<Element> chunks) {
        for (Element el : chunks) {
          if (el instanceof Vertex) {
            Vertex vertex = (Vertex) el;
            getModel().getGraph().addVertex(vertex);
          }
          if (el instanceof Edge) {
            Edge edge = (Edge) el;
            NeoSwingUtil.addEdge(getModel().getGraph(), edge);
          }
        }
        getModel().getViewer().repaint();
      }

    });
  }
}
