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
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 20:35
 */
public class CollapseNodeAction extends AbstractSwingAction {

  private final GraphModel model;
  private final ElementId<Vertex> node;
  private final Direction direction;
  
  public CollapseNodeAction(ElementId<Vertex> node, GraphModel graphModel, Direction direction) {
    this.model = graphModel;
    this.node = node;
    this.direction = direction;

    putValue(NAME, "Collapse " + direction);
    putValue(SMALL_ICON, NeoSwingUtil.icon("arrow_in"));
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    model.execute(new DbAction<Object, Element>() {
      @Override
      protected Object doInTx(GraphModel model) {
        Vertex v = model.getDatabase().lookup(node);
        for (Edge relationship : v.getEdges(direction)) {
          Vertex other = GraphDb.getOtherNode(relationship, v);
          publish(relationship);
          publish(other);
        }
        return null;
      }

      @Override
      protected void process(List<Element> chunks) {
        for (Element el : chunks) {
          if (el instanceof Vertex) {
            Vertex vertex = (Vertex) el;
            getModel().getGraph().removeVertex(vertex);
          }
          if (el instanceof Edge) {
            Edge edge = (Edge) el;
            getModel().getGraph().removeEdge(edge);
          }
        }
      }

      @Override
      protected void done() {
        getModel().getViewer().repaint();
      }
    });
  }
}
