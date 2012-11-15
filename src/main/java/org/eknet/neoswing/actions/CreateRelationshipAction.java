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
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.ElementId;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.view.SelectRelationshipTypePanel;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 14:56
 */
public class CreateRelationshipAction extends AbstractSwingAction {

  private ElementId<Vertex> vertex;
  private ElementId<Vertex> other;
  private Direction direction;
  private final GraphModel model;

  public CreateRelationshipAction(GraphModel model, ElementId<Vertex> vertex, Direction direction) {
    this.model = model;
    this.vertex = vertex;
    this.direction = direction;
    setVertex(vertex);
    setDirection(direction);
    updateName();

    putValue(SMALL_ICON, NeoSwingUtil.icon("connect"));
    putValue(SHORT_DESCRIPTION, "Create a new Edge");

  }

  private void updateName() {
    if (direction == Direction.IN) {
      if (this.other == null) {
        putValue(NAME, "Create Edge to this");
      } else {
        putValue(NAME, "Create Edge from picked Vertex to this");
      }
    }
    if (direction == Direction.OUT) {
      if (this.other == null) {
        putValue(NAME, "Create Edge from this");
      } else {
        putValue(NAME, "Create Edge from this to picked Vertex");
      }
    }
  }

  public void setVertex(ElementId<Vertex> Vertex) {
    this.vertex = Vertex;
    setEnabled(this.vertex != null && this.direction != null);
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    if (direction == Direction.BOTH) {
      throw new IllegalArgumentException(direction + " not supported");
    }
    this.direction = direction;
    setEnabled(this.vertex != null && this.direction != null);
    updateName();
  }

  public void setOther(ElementId<Vertex> other) {
    this.other = other;
    updateName();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (vertex == null || direction == null) {
      return;
    }
    Dialog dialog = new Dialog("Edge Type");
    SelectRelationshipTypePanel selectPanel = new SelectRelationshipTypePanel(model, NeoSwingUtil.getFactory(true));
    dialog.setContent(selectPanel);
    Dialog.Option option = dialog.show(getWindow(e), java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    if (option != Dialog.Option.OK) {
      return;
    }
    final String type = selectPanel.getType();
    if (type == null) {
      return;
    }
    model.execute(new DbAction<Object, Edge>() {
      @Override
      protected Object doInTx(GraphModel model) {
        Vertex v = model.getDatabase().lookup(vertex);
        Vertex o;
        if (other == null) {
          o = model.getDatabase().createNode();
        } else {
          o = model.getDatabase().lookup(other);
        }
        Edge edge0 = null;
        Edge edge1 = null;
        if (direction == Direction.IN) {
          edge0 = model.getDatabase().createEdge(o, v, type);
        }
        if (direction == Direction.OUT) {
          edge1 = model.getDatabase().createEdge(v, o, type);
        }
        Graph<Vertex, Edge> graph = model.getGraph();
        if (edge0 != null) {
          publish(edge0);
        }
        if (edge1 != null) {
          publish(edge1);
        }
        return null;
      }

      @Override
      protected void process(List<Edge> chunks) {
        for (Edge e : chunks) {
          NeoSwingUtil.addEdge(getModel().getGraph(), e);
        }
      }
    });
  }
}
