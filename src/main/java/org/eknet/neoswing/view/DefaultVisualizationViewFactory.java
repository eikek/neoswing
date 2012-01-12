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

package org.eknet.neoswing.view;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;
import org.apache.commons.collections15.Transformer;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.NeoSwing;
import org.eknet.neoswing.VisualizationViewFactory;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.SimpleGraphModel;
import org.eknet.neoswing.view.control.NavigateNodeMousePlugin;
import org.eknet.neoswing.view.control.NodePopupMenu;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:38
 */
public class DefaultVisualizationViewFactory implements VisualizationViewFactory {

  private final static Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);
  private final static String labelKeyFormat = "defaultlabel.%s.%s.%s";

  public static String createDefaultLabelPrefKey(PropertyContainer container) {
    String type = container instanceof Node ? "node" : "relationship";
    long id = NeoSwingUtil.getId(container);
    String dbstr = container.getGraphDatabase().toString();
    if (dbstr.length() > 15) {
      dbstr = dbstr.substring(dbstr.length() - 15);
    }
    return String.format(labelKeyFormat, dbstr, type, id);
  }
  
  @NotNull
  @Override
  public VisualizationViewer<Node, Relationship> createViewer(@NotNull final Graph<Node, Relationship> graph, @NotNull final GraphDatabaseService db) {
    final VisualizationViewer<Node, Relationship> vv =
        new VisualizationViewer<Node, Relationship>(new FRLayout2<Node, Relationship>(graph));
    vv.getModel().getRelaxer().setSleepTime(0);
    DefaultModalGraphMouse<Node, Relationship> mouseSupport = new DefaultModalGraphMouse<Node,Relationship>();
    addMousePlugins(mouseSupport, new SimpleGraphModel(graph, vv, db));
    vv.setGraphMouse(mouseSupport);
    vv.addKeyListener(mouseSupport.getModeKeyListener());
    vv.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode");

    VertexLabelAsShapeRenderer<Node, Relationship> vertexShape = new VertexLabelAsShapeRenderer<Node, Relationship>(vv.getRenderContext()) {
      @Override
      public Shape transform(Node v) {
        Rectangle rect = (Rectangle) super.transform(v);
        return new RoundRectangle2D.Double(rect.x - 3, rect.y - 3, rect.width + 7, rect.height + 7, 10, 10);
      }
    };

    vertexShape.setPosition(Renderer.VertexLabel.Position.CNTR);
    vv.setBackground(Color.WHITE);
    vv.getRenderContext().setVertexShapeTransformer(vertexShape);
    vv.getRenderContext().setVertexLabelTransformer(new Transformer<Node, String>() {
      @Override
      public String transform(Node node) {
        final String key = createDefaultLabelPrefKey(node);
        String label = prefs.get(key, null);
        if (label != null) {
          if (node.hasProperty(label)) {
            return node.getId() + ": " + node.getProperty(label).toString();
          }
        }
        if (node.hasProperty("name")) {
          return node.getId() + ": " + node.getProperty("name");
        }
        if (node.getId() == 0) {
          return "Reference";
        }
        return node.toString();
      }
    });
    vv.getRenderer().setVertexLabelRenderer(vertexShape);
    vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Node, Paint>() {
      private Color nodefill = Color.getHSBColor(207, 19, 97);

      @Override
      public Paint transform(Node v) {
        if (v.getId() == 0) {
          return Color.green;
        }
        return nodefill;
      }
    });

    vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Relationship, String>() {
      @Override
      public String transform(Relationship e) {
        return e.getType().name();
      }
    });

    return vv;
  }

  protected void addMousePlugins(DefaultModalGraphMouse<Node, Relationship> plugin, GraphModel graphModel) {
    plugin.add(new NodePopupMenu(MouseEvent.BUTTON3));
    plugin.add(new NavigateNodeMousePlugin(graphModel));
  }
  
}
