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

package org.eknet.neoswing.utils;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.DefaultComponentFactory;
import org.eknet.neoswing.ElementId;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.JideComponentFactory;
import org.eknet.neoswing.NeoSwing;
import org.eknet.neoswing.loader.Neo4JEmbeddedLoader;
import org.eknet.neoswing.loader.OrientDbLoader;
import org.eknet.neoswing.loader.TitanLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.EventObject;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:56
 */
public final class NeoSwingUtil {
  private static final Logger log = LoggerFactory.getLogger(NeoSwingUtil.class);
  private final static Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);

  private static boolean jideAvailable;
  private static Properties neoswingProperties = new Properties();
  
  static {
    jideAvailable = checkForJide();

    URL url = NeoSwingUtil.class.getResource("/org/eknet/neoswing/neoswing.properties");
    if (url != null) {
      try {
        neoswingProperties.load(url.openStream());
      } catch (IOException e) {
        log.warn("Error loading neoswing.properties.");
      }
    }
  }

  
  
  private NeoSwingUtil() {}

  public static String getApplicationName() {
    String name = neoswingProperties.getProperty("app.name");
    if (name == null || name.trim().isEmpty()) {
      return "neoswing";
    }
    if (name.equals("${project.name}")) {
      return "neoswing";
    }
    return name;
  }
  
  public static String getApplicationVersion() {
    String version = neoswingProperties.getProperty("app.version");
    if (version == null) {
      return "";
    }
    if (version.equals("${project.version}")) {
      return "dev";
    }
    if (version.endsWith("SNAPSHOT")) {
      String timestamp = neoswingProperties.getProperty("build.timestamp");
      if (timestamp != null) {
        version += " (" + timestamp + ")";
      }
    }
    return version;
  }
  
  public static ComponentFactory getFactory(boolean useJideIfAvailable) {
    if (useJideIfAvailable) {
      if (isJideAvailable()) {
        return new JideComponentFactory();
      }
    }
    return new DefaultComponentFactory();
  }

  public static boolean isJideAvailable() {
    return jideAvailable;
  }

  private static boolean checkForJide() {
    String jideClassname = "com.jidesoft.swing.JideButton";
    ClassLoader cl = NeoSwingUtil.class.getClassLoader();
    try {
      cl.loadClass(jideClassname);
      return true;
    } catch (ClassNotFoundException e) {
      cl = Thread.currentThread().getContextClassLoader();
      try {
        cl.loadClass(jideClassname);
        return true;
      } catch (ClassNotFoundException e1) {
        return false;
      }
    }
  }
  
  public static Image getFrameIcon() {
    URL url = NeoSwingUtil.class.getResource("../resources/chart_organisation.png");
    if (url == null) {
      return null;
    }
    return new ImageIcon(url).getImage();
  }

  public static GraphModel getGraphModel(Component component) {
    Component c = component;
    while (c != null) {
      if (c instanceof GraphModel) {
        return (GraphModel) c;
      }
      c = c.getParent();
    }
    throw new IllegalStateException("No holder found in hierarchy.");
  }

  public static Icon graphDbIcon(String name) {
    if (name.equals(Neo4JEmbeddedLoader.NAME)) {
      return icon("neo4j");
    }
    if (name.equals(TitanLoader.NAME)) {
      return icon("titan");
    }
    if (name.equals(OrientDbLoader.NAME)) {
      return icon("orientdb");
    }
    return icon("folder_database");
  }
  public static Icon icon(String name) {
    if (!name.endsWith("png")) {
      name = name + ".png";
    }
    URL url = NeoSwingUtil.class.getResource("/org/eknet/neoswing/resources/" + name);
    if (url == null) {
      log.warn("No icon found with name: " + name);
      return MissingIcon.tiny;
    }
    return new ImageIcon(url);
  }

  public static void addEdge(final Graph<Vertex, Edge> graph, final Edge relationship) {
    if (!graph.containsEdge(relationship)) {
      EdtExecutor.instance.execute(new Runnable() {
        @Override
        public void run() {
          graph.addEdge(relationship,
              relationship.getVertex(Direction.OUT),
              relationship.getVertex(Direction.IN),
              EdgeType.DIRECTED);
        }
      });
    }
  }


  public static Border topEtchBorder() {
    return new Border() {
      @Override
      public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.translate(x, y);

        g.setColor(c.getBackground().darker());
        g.drawLine(0, 0, width - 2, 0);

        g.setColor(c.getBackground().brighter());
        g.drawLine(1, 1, width -3, 1);
        g.translate(-x, -y);
      }

      @Override
      public Insets getBorderInsets(Component c) {
        return new Insets(3, 0, 0, 0);
      }

      @Override
      public boolean isBorderOpaque() {
        return true;
      }
    };
  }


  public static Window findOwner(Object component) {
    if (!(component instanceof Component)) {
      if (component instanceof EventObject) {
        return findOwner(((EventObject) component).getSource());
      } else {
        return null;
      }
    }
    Component c = (Component) component;
    while (c != null) {
      if (c instanceof Window) {
        return (Window) c;
      }
      c = c.getParent();
    }
    return null;
  }

  /**
   * Gets the id of either the node or relationship.
   *
   * @param pc
   * @return
   */
  public static String getId(Element pc) {
    return pc.getId().toString();
  }

  public static <A> boolean equals(ElementId<?> pc1, ElementId<?> pc2) {
    if (pc1 == null && pc2 == null) {
      return true;
    }
    if (pc1 == null) {
      return false;
    }
    if (pc2 == null) {
      return false;
    }
    if (pc1.getClass() != pc2.getClass()) {
      return false;
    }
    return pc1.equals(pc2);
  }

  /**
   * Copies the vertices and edges from new JUNG graph view into the given blueprints graph.
   * Note that this must be called inside a transaction.
   * @param view
   * @return
   */
  public static void copyView(Graph<Vertex, Edge> view, com.tinkerpop.blueprints.Graph to) {
    for (Vertex v : view.getVertices()) {
      Vertex tv = to.addVertex(v.getId());
      ElementHelper.copyProperties(v, tv);
    }
    for (final Edge fromEdge : view.getEdges()) {
      final Vertex outVertex = to.getVertex(fromEdge.getVertex(Direction.OUT).getId());
      final Vertex inVertex = to.getVertex(fromEdge.getVertex(Direction.IN).getId());
      final Edge toEdge = to.addEdge(fromEdge.getId(), outVertex, inVertex, fromEdge.getLabel());
      ElementHelper.copyProperties(fromEdge, toEdge);
    }
  }

  public static <B> B chooseSingleFile(Object ownerComp, String title, Function<File, B> fun) {
    final String key = "neoswing." + title.replaceAll("\\s+", "") + ".lastlocation";
    String lastLocation = prefs.get(key, null);
    JFileChooser fc = new JFileChooser(lastLocation);
    fc.setDialogTitle(title);
    fc.setAcceptAllFileFilterUsed(false);
    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fc.setMultiSelectionEnabled(false);
    Window owner = findOwner(ownerComp);
    int rc = fc.showOpenDialog(owner);
    if (rc == JFileChooser.APPROVE_OPTION) {
      final File f = fc.getSelectedFile();
      if (f == null) {
        return null;
      }
      return fun.apply(f);
    }
    return null;
  }

  // ~~ code below is copied from cru-swing class SwingUtil
  //    see (http://maven.raffael.ch/ch/raffael/util/cru-swing/)


  public static boolean invoke(Action action, Object source) {
    if ( action == null || !action.isEnabled() ) {
      return false;
    }
    ActionEvent evt = new ActionEvent(source, ActionEvent.ACTION_PERFORMED,
            (String)action.getValue(Action.ACTION_COMMAND_KEY), 0);
    action.actionPerformed(evt);
    return true;
  }

  public static void center(Component component, Component parent) {
    component.setLocation(parent.getX() + parent.getWidth() / 2 - component.getWidth() / 2,
            parent.getY() + parent.getHeight() / 2 - component.getWidth() / 2);
  }
}
