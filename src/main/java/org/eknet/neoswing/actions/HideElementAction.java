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

import java.awt.event.ActionEvent;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

import org.eknet.neoswing.GraphModel;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 20:55
 */
public class HideElementAction extends AbstractSwingAction {

  private final PropertyContainer element;
  private final GraphModel graphModel;
  
  public HideElementAction(GraphModel graphModel, PropertyContainer element) {
    this.element = element;
    this.graphModel = graphModel;
    
    putValue(NAME, "Hide Element");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (element instanceof Node) {
      graphModel.getGraph().removeVertex((Node) element);
    }
    if (element instanceof Relationship) {
      graphModel.getGraph().removeEdge((Relationship) element);
    }
  }
}
