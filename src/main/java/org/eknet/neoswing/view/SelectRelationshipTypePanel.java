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

import org.eknet.neoswing.ComponentFactory;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 15:04
 */
public class SelectRelationshipTypePanel extends JPanel {

  private JComboBox typeCombo;

  public SelectRelationshipTypePanel(GraphDatabaseService db, ComponentFactory factory) {
    BoxLayout boxl = new BoxLayout(this, BoxLayout.Y_AXIS);
    setLayout(boxl);

    JPanel top = factory.createPanel();
    top.setLayout(new FlowLayout(FlowLayout.LEADING));
    add(top);
    top.add(new JLabel("Please choose or enter relationship type"));

    JPanel input = factory.createPanel();
    input.setLayout(new FlowLayout(FlowLayout.CENTER));
    add(input);

    Iterable<RelationshipType> types = db.getRelationshipTypes();
    List<String> items = new ArrayList<String>();
    for (RelationshipType type : types) {
      items.add(type.name());
    }
    typeCombo = factory.createComboBox(items.toArray(new String[items.size()]), true, true);
    input.add(typeCombo);
  }
  
  public RelationshipType getType() {
    String value = (String) typeCombo.getSelectedItem();
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    return DynamicRelationshipType.withName(value);
  }
}
