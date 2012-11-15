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

package org.eknet.neoswing.view;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;
import org.apache.commons.collections15.Transformer;
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.NeoSwing;
import org.eknet.neoswing.VisualizationViewFactory;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.SimpleGraphModel;
import org.eknet.neoswing.view.control.NavigateNodeMousePlugin;
import org.eknet.neoswing.view.control.NodePopupMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:38
 */
public class DefaultVisualizationViewFactory implements VisualizationViewFactory {
  private static final Logger log = LoggerFactory.getLogger(DefaultVisualizationViewFactory.class);

  private final static Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);
  private final static String labelKeyFormat = "defaultlabel.%s.%s.%s";

  public static String createDefaultLabelPrefKey(Element container, GraphDb db) {
    String type = container instanceof Vertex ? "node" : "relationship";
    String id = NeoSwingUtil.getId(container);
    String dbstr = db.getName();
    if (dbstr.length() > 15) {
      dbstr = dbstr.substring(dbstr.length() - 15);
    }
    return String.format(labelKeyFormat, dbstr, type, id);
  }
  
  @Override
  public VisualizationViewer<Vertex, Edge> createViewer(Graph<Vertex, Edge> graph, GraphDb db) {
    final VisualizationViewer<Vertex, Edge> vv =
        new VisualizationViewer<Vertex, Edge>(new FRLayout2<Vertex, Edge>(graph));
    vv.getModel().getRelaxer().setSleepTime(0);
    DefaultModalGraphMouse<Vertex, Edge> mouseSupport = new DefaultModalGraphMouse<Vertex,Edge>();
    final GraphModel model = new SimpleGraphModel(graph, vv, db);
    addMousePlugins(mouseSupport, model);
    vv.setGraphMouse(mouseSupport);
    vv.addKeyListener(mouseSupport.getModeKeyListener());
    vv.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode");

    VertexLabelAsShapeRenderer<Vertex, Edge> vertexShape = new VertexLabelAsShapeRenderer<Vertex, Edge>(vv.getRenderContext()) {
      @Override
      public Shape transform(Vertex v) {
        Rectangle rect = (Rectangle) super.transform(v);
        return new RoundRectangle2D.Double(rect.x - 3, rect.y - 3, rect.width + 7, rect.height + 7, 10, 10);
      }
    };

    vertexShape.setPosition(Renderer.VertexLabel.Position.CNTR);
    vv.setBackground(Color.WHITE);
    vv.getRenderContext().setVertexShapeTransformer(vertexShape);
    vv.getRenderContext().setVertexLabelTransformer(new VertexTransformer(model));
    vv.getRenderer().setVertexLabelRenderer(vertexShape);
    vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Vertex, Paint>() {
      private Color nodefill = Color.getHSBColor(207, 19, 97);

      @Override
      public Paint transform(Vertex v) {
//        if (v.getId() == 0) {
//          return Color.green;
//        }
        return nodefill;
      }
    });

    vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Edge, String>() {

      private final WeakHashMap<Object, String> cache = new WeakHashMap<Object, String>();

      @Override
      public String transform(final Edge e) {
        String s = cache.get(e.getId());
        if (s == null) {
          s = "<null>";
          GraphDb.Tx tx = model.getDatabase().beginTx();
          try {
            Edge edge = model.getDatabase().lookupEdge(e.getId());
            s = edge.getLabel();
            tx.success();
            cache.put(edge.getId(), s);
          } catch (Exception e1) {
            log.error("Error obtaining edge label", e1);
          } finally {
            tx.finish();
          }
        }
        return s;
      }
    });

    return vv;
  }

  protected void addMousePlugins(DefaultModalGraphMouse<Vertex, Edge> plugin, GraphModel graphModel) {
    plugin.add(new NodePopupMenu(MouseEvent.BUTTON3));
    plugin.add(new NavigateNodeMousePlugin(graphModel));
  }

  static class VertexTransformer implements Transformer<Vertex, String> {
    private final GraphModel model;
    private final WeakHashMap<Object, String> cache = new WeakHashMap<Object, String>();

    VertexTransformer(GraphModel model) {
      this.model = model;
    }

    @Override
    public synchronized String transform(final Vertex vertex) {
      String s = cache.get(vertex.getId());
      if (s == null) {
        s = "<null>";
        log.info(">> cache is null: " + vertex);
        DbAction<String, Object> action = new DbAction<String, Object>() {
          @Override
          protected String doInTx(GraphModel model) {
            Vertex v = model.getDatabase().lookupVertex(vertex.getId());
            final String key = createDefaultLabelPrefKey(v, model.getDatabase());
            String label = prefs.get(key, null);
            if (label != null) {
              if (v.getProperty(label) != null) {
                return v.getId() + ": " + v.getProperty(label).toString();
              }
            }
            if (v.getProperty("name") != null) {
              return v.getId() + ": " + v.getProperty("name");
            }
            return v.toString();
          }
        };
        model.execute(action);
        try {
          s = action.get(5, TimeUnit.SECONDS);
          cache.put(vertex.getId(), s);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } catch (Exception e) {
          log.error("Error obtaining vertex label", e);
        }
      }
      return s;
    }
  }
}
