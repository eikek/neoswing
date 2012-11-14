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

package org.eknet.neoswing;

import com.tinkerpop.blueprints.*;
import org.eknet.neoswing.utils.EmptyIndex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 14.11.12 19:29
 */
public class GraphDb {
  private final static ThreadLocal<TopTx> txContext = new ThreadLocal<TopTx>();

  private final Graph db;

  public GraphDb(Graph db) {
    this.db = db;
  }

  public String getName() {
    return db.toString();
  }

  public void shutdown() {
    db.shutdown();
  }

  public String[] nodeIndexNames() {
    return indexNames(Vertex.class);
  }

  public Iterable<Vertex> getVertices(String key, Object value) {
    return db.getVertices(key, value);
  }

  public Iterable<Vertex> getVertices() {
    return db.getVertices();
  }

  public Iterable<Edge> getEdges(String key, Object value) {
    return db.getEdges(key, value);
  }

  public Iterable<Edge> getEdges() {
    return db.getEdges();
  }

  public Index<Vertex> getNodeIndex(final String name) {
    if (db instanceof IndexableGraph) {
      IndexableGraph indexableGraph = (IndexableGraph) db;
      return indexableGraph.getIndex(name, Vertex.class);
    } else {
      return new EmptyIndex<Vertex>(name, Vertex.class);
    }
  }

  public Index<Edge> getEdgeIndex(String name) {
    if (db instanceof IndexableGraph) {
      IndexableGraph indexableGraph = (IndexableGraph) db;
      return indexableGraph.getIndex(name, Edge.class);
    } else {
      return new EmptyIndex<Edge>(name, Edge.class);
    }
  }
  public String[] indexNames(Class<? extends Element> type) {
    ArrayList<String> names = new ArrayList<String>();
    if (db instanceof IndexableGraph) {
      IndexableGraph indexableGraph = (IndexableGraph) db;
      for (Index<? extends Element> idx : indexableGraph.getIndices()) {
        if (idx.getIndexClass() == type) {
          names.add(idx.getIndexName());
        }
      }
    }
    return names.toArray(new String[names.size()]);
  }

  public String[] relationshipIndexNames() {
    return indexNames(Edge.class);
  }

  public Iterable<String> getRelationshipTypes() {
    //hardcore find impl
    Set<String> set = new HashSet<String>();
    for (Edge e : db.getEdges()) {
      set.add(e.getLabel());
    }
    return set;
  }

  public Vertex createNode() {
    return db.addVertex(null);
  }

  public Edge createEdge(Vertex out, Vertex in, String label) {
    return db.addEdge(null, out, in, label);
  }

  public void deleteEdge(Edge edge) {
    db.removeEdge(edge);
  }

  public void deleteVertex(Vertex vertex) {
    db.removeVertex(vertex);
  }

  public Tx beginTx() {
    TopTx toptx = txContext.get();
    if (toptx == null) {
      toptx = new TopTx();
      txContext.set(toptx);
      return toptx;
    } else {
      return new PlaceboTx();
    }
  }

  public static Vertex getOtherNode(Edge edge, Vertex one) {
    if (edge.getVertex(Direction.IN).equals(one)) {
      return edge.getVertex(Direction.OUT);
    }
    return edge.getVertex(Direction.IN);
  }

  public static interface Tx {
    void success();
    void finish();
  }

  private class TopTx implements Tx {
    private boolean committed = false;
    private boolean rollbackOnly = false;

    public void setRollbackOnly(boolean rollbackOnly) {
      this.rollbackOnly = rollbackOnly;
    }

    @Override
    public void success() {
      this.committed = true;
    }

    @Override
    public void finish() {
      txContext.remove();
      if (committed && !rollbackOnly) {
        if (db instanceof TransactionalGraph) {
          TransactionalGraph graph = (TransactionalGraph) db;
          graph.stopTransaction(TransactionalGraph.Conclusion.SUCCESS);
        }
      } else {
        if (db instanceof TransactionalGraph) {
          TransactionalGraph graph = (TransactionalGraph) db;
          graph.stopTransaction(TransactionalGraph.Conclusion.FAILURE);
        }
        throw new IllegalStateException("Transaction not successful.");
      }
    }
  }

  private class PlaceboTx implements Tx {
    private boolean comitted = false;

    @Override
    public void success() {
      this.comitted = true;
    }

    @Override
    public void finish() {
      TopTx toptx = txContext.get();
      if (toptx != null) {
        toptx.setRollbackOnly(!comitted);
      }
    }
  }

}
