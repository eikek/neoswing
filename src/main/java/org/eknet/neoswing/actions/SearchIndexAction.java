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

import edu.uci.ics.jung.graph.Graph;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.view.SelectIndexPanel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 16:23
 */
public class SearchIndexAction extends AbstractSwingAction {
  private static final Logger log = LoggerFactory.getLogger(SearchIndexAction.class);
  
  private GraphDatabaseService db;
  private Graph<Node, Relationship> graph;
  private SelectIndexPanel selectIndexPanel;
  
  public SearchIndexAction(GraphDatabaseService db, Graph<Node, Relationship> graph) {
    this.db = db;
    this.graph = graph;
    this.selectIndexPanel = new SelectIndexPanel(db);
    putValue(NAME, "Search nodes or relationships via index");
    putValue(SHORT_DESCRIPTION, "Search nodes or relationships via index");
    putValue(SMALL_ICON, NeoSwingUtil.icon("find"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Dialog dialog = new Dialog("Select index");
    dialog.setContent(selectIndexPanel);
    Dialog.Option option = dialog.show(getWindow(e), java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    if (option != Dialog.Option.OK) {
      return;
    }
    String indexName = selectIndexPanel.getIndexName();
    if (indexName == null) {
      return;
    }
    String query = selectIndexPanel.getQuery();
    if (query == null) {
      return;
    }
    Transaction tx = db.beginTx();
    try {
      if (selectIndexPanel.isNodeIndexSelected()) {
        IndexHits<Node> hits = db.index().forNodes(indexName).query(query);
        for (Node node : hits) {
          graph.addVertex(node);
        }
        hits.close();
      } else if (selectIndexPanel.isRelationshipIndexSelected()) {
        IndexHits<Relationship> hits = db.index().forRelationships(indexName).query(query);
        for (Relationship rel : hits) {
          NeoSwingUtil.addEdge(graph, rel);
        }
        hits.close();
      } else {
        throw new RuntimeException("Unreachable code");
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
