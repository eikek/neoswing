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
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.ElementId;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 21:17
 */
public class DeleteElementAction extends AbstractSwingAction {

  private final ElementId<?> element;
  private final GraphModel graphModel;

  public DeleteElementAction(ElementId<?> element, GraphModel graphModel) {
    this.element = element;
    this.graphModel = graphModel;

    putValue(NAME, "Delete " + (element.isVertex() ? "Vertex" : "Edge"));
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
    graphModel.execute(new DbAction<Object, Runnable>() {
      @Override
      protected Object doInTx(GraphModel model) {
        Element el = model.getDatabase().lookup(element);
        if (el instanceof Vertex) {
          final Vertex node = (Vertex) el;
          final Iterable<Edge> relationships = node.getEdges(Direction.BOTH);
          if (relationships.iterator().hasNext()) {
            Dialog.Option option = Dialogs.confirm(graphModel.getViewer(), "The Vertex has Edges associated. " +
                "Do you want to delete all those Edges, too?");
            if (option != Dialog.Option.OK) {
              return null;
            } else {
              for (Edge rel : relationships) {
                model.getDatabase().deleteEdge(rel);
              }
              publish(new Runnable() {
                @Override
                public void run() {
                  for (Edge rel : relationships) {
                    graphModel.getGraph().removeEdge(rel);
                  }
                }
              });
            }
          }
          model.getDatabase().deleteVertex(node);
          publish(new Runnable() {
            @Override
            public void run() {
              graphModel.getGraph().removeVertex(node);
            }
          });
        }
        if (el instanceof Edge) {
          final Edge e = (Edge) el;
          model.getDatabase().deleteEdge(e);
          publish(new Runnable() {
            @Override
            public void run() {
              graphModel.getGraph().removeEdge(e);
            }
          });
        }
        return null;
      }

      @Override
      protected void process(List<Runnable> chunks) {
        for (Runnable r : chunks) {
          r.run();
        }
      }

      @Override
      protected void done() {
        graphModel.getViewer().repaint();
      }
    });
  }
}
