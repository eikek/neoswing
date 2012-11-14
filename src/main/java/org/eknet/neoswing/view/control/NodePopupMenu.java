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

package org.eknet.neoswing.view.control;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.actions.*;
import org.eknet.neoswing.utils.NeoSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 19:22
 */
public class NodePopupMenu extends AbstractMousePlugin {

  private final ComponentFactory factory;

  public NodePopupMenu(int modifiers, ComponentFactory factory) {
    super(modifiers);
    this.factory = factory;
  }

  public NodePopupMenu(int modifiers) {
    super(modifiers);
    this.factory = NeoSwingUtil.getFactory(true);
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getButton() != modifiers || !e.isPopupTrigger()) {
      return;
    }
    VisualizationViewer<Vertex, Edge> viewer = getViewer(e);

    down = e.getPoint();
    Vertex node = viewer.getPickSupport().getVertex(viewer.getGraphLayout(), down.getX(), down.getY());
    if (node != null) {
      JPopupMenu menu = getPopup(node, viewer);
      menu.show(viewer, down.x, down.y);
    }
    Edge relationship = viewer.getPickSupport().getEdge(viewer.getGraphLayout(), down.getX(), down.getY());
    if (relationship != null) {
      JPopupMenu menu = getPopup(relationship, viewer);
      menu.show(viewer, down.x, down.y);
    }
  }
  
  protected JPopupMenu getPopup(Vertex node, VisualizationViewer<Vertex, Edge> viewer) {
    JPopupMenu menu = factory.createPopupMenu();
    Window owner = NeoSwingUtil.findOwner(viewer);

    JLabel label = new JLabel("Node Actions");
    label.setFont(label.getFont().deriveFont(Font.BOLD));
    label.setHorizontalTextPosition(SwingConstants.CENTER);
    menu.add(label);
    
    menu.addSeparator();
    GraphModel graphModel = NeoSwingUtil.getGraphModel(viewer);
    factory.addMenuItemGroup(menu, getAllExpandActions(node, graphModel));
    
    menu.add(new JPopupMenu.Separator());
    factory.addMenuItemGroup(menu, getAllCollapseActions(node, graphModel));
    menu.add(new JMenuItem(new HideElementAction(graphModel, node)));

    menu.addSeparator();
    List<Action> createRelationshipActions = new ArrayList<Action>();
    CreateRelationshipAction createRel = new CreateRelationshipAction(graphModel, node, Direction.IN);
    createRel.setWindow(owner);
    createRelationshipActions.add(createRel);
    
    createRel = new CreateRelationshipAction(graphModel, node, Direction.OUT);
    createRel.setWindow(owner);
    createRelationshipActions.add(createRel);
    
    Set<Vertex> picked = viewer.getPickedVertexState().getPicked();
    if (picked.size() == 1) {
      Vertex other = new ArrayList<Vertex>(picked).get(0);
      createRel = new CreateRelationshipAction(graphModel, node, Direction.IN);
      createRel.setOther(other);
      createRel.setWindow(owner);
      createRelationshipActions.add(createRel);

      createRel = new CreateRelationshipAction(graphModel, node, Direction.OUT);
      createRel.setOther(other);
      createRel.setWindow(owner);
      createRelationshipActions.add(createRel);
    }
    factory.addMenuItemGroup(menu, createRelationshipActions);

    menu.add(new JPopupMenu.Separator());
    EditPropertyAction editAction = new EditPropertyAction(node, graphModel.getDatabase());
    editAction.setWindow(owner);
    menu.add(new JMenuItem(editAction));
    
    menu.add(new JPopupMenu.Separator());
    DeleteElementAction deleteAction = new DeleteElementAction(node, graphModel);
    deleteAction.setWindow(owner);
    menu.add(new JMenuItem(deleteAction));

    menu.addSeparator();
    menu.add(new JMenuItem(new RemoveDefaultLabelAction(graphModel.getDatabase(), node)));
    return menu;
  }

  protected JPopupMenu getPopup(Edge relationship, VisualizationViewer<Vertex, Edge> viewer) {
    JPopupMenu menu = factory.createPopupMenu();
    JLabel label = new JLabel("Relationship Actions");
    label.setFont(label.getFont().deriveFont(Font.BOLD));
    label.setHorizontalTextPosition(SwingConstants.CENTER);
    menu.add(label);
    Window owner = NeoSwingUtil.findOwner(viewer);
    GraphModel graphModel = NeoSwingUtil.getGraphModel(viewer);

    menu.add(new JPopupMenu.Separator());
    EditPropertyAction editAction = new EditPropertyAction(relationship, graphModel.getDatabase());
    editAction.setWindow(owner);
    menu.add(new JMenuItem(editAction));

    menu.add(new JPopupMenu.Separator());
    DeleteElementAction deleteAction = new DeleteElementAction(relationship, graphModel);
    deleteAction.setWindow(owner);
    menu.add(new JMenuItem(deleteAction));

    return menu;
  }

  private Iterable<Action> getAllExpandActions(Vertex node, GraphModel model) {
    List<Action> list = new ArrayList<Action>();
    list.add(new ExpandNodeAction(node, model, Direction.BOTH));
    list.add(new ExpandNodeAction(node, model, Direction.OUT));
    list.add(new ExpandNodeAction(node, model, Direction.IN));
    return list;
  }

  private Iterable<Action> getAllCollapseActions(Vertex node, GraphModel graphModel) {
    List<Action> list = new ArrayList<Action>();
    list.add(new CollapseNodeAction(node, graphModel, Direction.BOTH));
    list.add(new CollapseNodeAction(node, graphModel, Direction.OUT));
    list.add(new CollapseNodeAction(node, graphModel, Direction.IN));
    return list;
  }
}
