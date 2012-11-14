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

import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.GraphDb;

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

  public SelectRelationshipTypePanel(GraphDb db, ComponentFactory factory) {
    BoxLayout boxl = new BoxLayout(this, BoxLayout.Y_AXIS);
    setLayout(boxl);

    JPanel top = factory.createPanel();
    top.setLayout(new FlowLayout(FlowLayout.LEADING));
    add(top);
    top.add(new JLabel("Please choose or enter relationship type"));

    JPanel input = factory.createPanel();
    input.setLayout(new FlowLayout(FlowLayout.CENTER));
    add(input);

    Iterable<String> types = db.getRelationshipTypes();
    List<String> items = new ArrayList<String>();
    for (String type : types) {
      items.add(type);
    }
    typeCombo = factory.createComboBox(items.toArray(new String[items.size()]), true, true);
    input.add(typeCombo);
  }
  
  public String getType() {
    String value = (String) typeCombo.getSelectedItem();
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    return value;
  }
}
