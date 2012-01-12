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
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 21:14
 */
public class AddNodeAction extends AbstractSwingAction {

  private final GraphModel graphModel;

  public AddNodeAction(GraphModel graphModel) {
    this.graphModel = graphModel;

    putValue(NAME, "New Node");
    putValue(SHORT_DESCRIPTION, "Create a new node");
    putValue(SMALL_ICON, NeoSwingUtil.icon("pill_add"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    GraphDatabaseService db = graphModel.getDatabase();
    Transaction tx = db.beginTx();
    try {
      Node node = db.createNode();
      tx.success();
      graphModel.getGraph().addVertex(node);
      graphModel.getViewer().repaint();
    } finally {
      tx.finish();
    }
  }
}
