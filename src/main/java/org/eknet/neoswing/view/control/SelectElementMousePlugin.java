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

package org.eknet.neoswing.view.control;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 21:15
 */
public class SelectElementMousePlugin extends AbstractMousePlugin {

  public static final String PROPERTY_NODE = "selectedNode";
  public static final String PROPERTY_RELATIONSHIP = "selectedRelationship";
  public static final String PROPERTY_CONTAINER = "selectedPropertyContainer";

  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private Node node;
  private Relationship relationship;
  
  public SelectElementMousePlugin(int button) {
    super(button);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  @Override
  public boolean checkModifiers(MouseEvent e) {
    return e.getButton() == modifiers;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (checkModifiers(e)) {
      VisualizationViewer<Node, Relationship> viewer = getViewer(e);
      down = e.getPoint();
      Node node = viewer.getPickSupport().getVertex(viewer.getGraphLayout(), down.getX(), down.getY());
      if (node != null) {
        propertyChangeSupport.firePropertyChange(PROPERTY_NODE, this.node, node);
        propertyChangeSupport.firePropertyChange(PROPERTY_CONTAINER, this.node, node);
        this.node = node;
        this.relationship = null;
      }
      Relationship relationship = viewer.getPickSupport().getEdge(viewer.getGraphLayout(), down.getX(), down.getY());
      if (relationship != null) {
        propertyChangeSupport.firePropertyChange(PROPERTY_RELATIONSHIP, this.relationship, relationship);
        propertyChangeSupport.firePropertyChange(PROPERTY_CONTAINER, this.relationship, relationship);
        this.relationship = relationship;
        this.node = null;
      }
    }
  }
}
