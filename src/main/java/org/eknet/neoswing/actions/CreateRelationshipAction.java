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
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.view.SelectRelationshipTypePanel;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 14:56
 */
public class CreateRelationshipAction extends AbstractSwingAction {

  private Vertex vertex;
  private Vertex other;
  private Direction direction;
  private final GraphModel model;

  public CreateRelationshipAction(GraphModel model, Vertex Vertex, Direction direction) {
    this.model = model;
    this.vertex = Vertex;
    this.direction = direction;
    setVertex(Vertex);
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

  public Vertex getVertex() {
    return vertex;
  }

  public void setVertex(Vertex Vertex) {
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

  public Vertex getOther() {
    return other;
  }

  public void setOther(Vertex other) {
    this.other = other;
    updateName();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (vertex == null || direction == null) {
      return;
    }
    GraphDb database = model.getDatabase();

    Dialog dialog = new Dialog("Edge Type");
    SelectRelationshipTypePanel selectPanel = new SelectRelationshipTypePanel(database, NeoSwingUtil.getFactory(true));
    dialog.setContent(selectPanel);
    Dialog.Option option = dialog.show(getWindow(e), java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    if (option != Dialog.Option.OK) {
      return;
    }
    String type = selectPanel.getType();
    if (type == null) {
      return;
    }

    GraphDb.Tx tx = database.beginTx();
    try {
      if (other == null) {
        other = database.createNode();
      }

      Edge Edge0 = null;
      Edge Edge1 = null;
      if (direction == Direction.IN) {
        Edge0 = database.createEdge(other, vertex, type);
      }
      if (direction == Direction.OUT) {
        Edge1 = database.createEdge(vertex, other, type);
      }
      tx.success();
      Graph<Vertex, Edge> graph = model.getGraph();
      if (Edge0 != null) {
        NeoSwingUtil.addEdge(graph, Edge0);
      }
      if (Edge1 != null) {
        NeoSwingUtil.addEdge(graph, Edge1);
      }
    } finally {
      tx.finish();
    }


  }
}
