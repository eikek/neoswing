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

package org.eknet.neoswing.utils;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.DefaultComponentFactory;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.JideComponentFactory;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URL;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:56
 */
public final class NeoSwingUtil {
  private static final Logger log = LoggerFactory.getLogger(NeoSwingUtil.class);
  private static boolean jideAvailable;

  static {
    jideAvailable = checkForJide();
  }

  private NeoSwingUtil() {}

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

  public static Icon icon(@NotNull String name) {
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

  public static void addEdge(Graph<Node, Relationship> graph, Relationship relationship) {
    if (!graph.containsEdge(relationship)) {
      graph.addEdge(relationship,
              relationship.getStartNode(),
              relationship.getEndNode(),
              EdgeType.DIRECTED);
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
      return null;
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
  public static long getId(PropertyContainer pc) {
    if (pc instanceof Node) {
      return ((Node) pc).getId();
    }
    if (pc instanceof Relationship) {
      return ((Relationship) pc).getId();
    }
    throw new RuntimeException("Unknown " + PropertyContainer.class + ": " + pc);
  }

  public static boolean equals(PropertyContainer pc1, PropertyContainer pc2) {
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
    return NeoSwingUtil.getId(pc1) == NeoSwingUtil.getId(pc2);
  }

  public static void checkDatabaseDirectory(@NotNull File db) throws IllegalArgumentException {
    if (!db.isDirectory()) {
      throw new IllegalArgumentException("'" + db + "' is not a directory");
    }
    File[] content = db.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.isFile();
      }
    });
    if (content.length > 0) {
      content = db.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.startsWith("neostore");
        }
      });
      if (content.length == 0) {
        throw new IllegalArgumentException("'" + db + "' doesn't seem to be a neo4j database.");
      }
    }
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
