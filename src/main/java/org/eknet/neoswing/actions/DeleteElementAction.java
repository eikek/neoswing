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

import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 21:17
 */
public class DeleteElementAction extends AbstractSwingAction {

  private final PropertyContainer element;
  private final GraphModel graphModel;

  public DeleteElementAction(PropertyContainer element, GraphModel graphModel) {
    this.element = element;
    this.graphModel = graphModel;

    putValue(NAME, "Delete " + (element instanceof Node ? "Node" : "Relationship"));
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
    GraphDatabaseService db = graphModel.getDatabase();
    Transaction tx = db.beginTx();
    try {
      if (element instanceof Node) {
        Node node = (Node) element;
        Iterable<Relationship> relationships = node.getRelationships();
        if (relationships.iterator().hasNext()) {
          option = Dialogs.confirm(graphModel.getViewer(), "The node has relationships associated. " +
              "Do you want to delete all those relationships, too?");
          if (option != Dialog.Option.OK) {
            return;
          } else {
            for (Relationship rel : relationships) {
              rel.delete();
              graphModel.getGraph().removeEdge(rel);
            }
          } 
        }
        node.delete();
        graphModel.getGraph().removeVertex(node);
      }
      if (element instanceof Relationship) {
        ((Relationship) element).delete();
        graphModel.getGraph().removeEdge((Relationship) element);
      }
      tx.success();
      graphModel.getViewer().repaint();
    } finally {
      tx.finish();
    }
  }
}
