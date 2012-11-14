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
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 21:17
 */
public class DeleteElementAction extends AbstractSwingAction {

  private final Element element;
  private final GraphModel graphModel;

  public DeleteElementAction(Element element, GraphModel graphModel) {
    this.element = element;
    this.graphModel = graphModel;

    putValue(NAME, "Delete " + (element instanceof Vertex ? "Vertex" : "Edge"));
    putValue(SMALL_ICON, NeoSwingUtil.icon("bin"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (element == null) {
      return;
    }

    Dialog.Option option = Dialogs.confirm(graphModel.getViewer(), "Really delete '" + element + "'?");
    if (option != Dialog.Option.OK) {
      return;
    }
    GraphDb db = graphModel.getDatabase();
    GraphDb.Tx tx = db.beginTx();
    try {
      if (element instanceof Vertex) {
        Vertex node = (Vertex) element;
        Iterable<Edge> relationships = node.getEdges(Direction.BOTH);
        if (relationships.iterator().hasNext()) {
          option = Dialogs.confirm(graphModel.getViewer(), "The Vertex has Edges associated. " +
              "Do you want to delete all those Edges, too?");
          if (option != Dialog.Option.OK) {
            return;
          } else {
            for (Edge rel : relationships) {
              db.deleteEdge(rel);
              graphModel.getGraph().removeEdge(rel);
            }
          } 
        }
        db.deleteVertex(node);
        graphModel.getGraph().removeVertex(node);
      }
      if (element instanceof Edge) {
        db.deleteEdge(((Edge) element));
        graphModel.getGraph().removeEdge((Edge) element);
      }
      tx.success();
      graphModel.getViewer().repaint();
    } finally {
      tx.finish();
    }
  }
}
