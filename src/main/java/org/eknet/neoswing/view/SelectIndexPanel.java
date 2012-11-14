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
import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.utils.NeoSwingUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 16:16
 */
public class SelectIndexPanel extends JPanel {

  private final ComponentFactory factory;
  private final GraphDb db;

  private JComboBox nodeIndexCombo;
  private JTextField nodeQueryField;

  private JComboBox relationIndexCombo;
  private JTextField relationQueryField;

  private JTabbedPane tabs;

  public SelectIndexPanel(GraphDb db, ComponentFactory factory) {
    this.db = db;
    this.factory = factory;
    initComponents();
  }

  public SelectIndexPanel(GraphDb db) {
    this(db, NeoSwingUtil.getFactory(true));
  }

  private void initComponents() {
    setLayout(new BorderLayout(5, 5));

    JPanel top = factory.createPanel();
    top.setLayout(new FlowLayout(FlowLayout.LEADING));
    add(top, BorderLayout.NORTH);
    top.add(new JLabel("Please select an index and query to execute against that index."));

    tabs = factory.createTabbedPane();
    add(tabs, BorderLayout.CENTER);
    JPanel nodeTab = createIndexPanel(Vertex.class);
    tabs.addTab("Nodes", nodeTab);
    JPanel relationTab = createIndexPanel(Edge.class);
    tabs.addTab("Relationships", relationTab);
  }

  private JPanel createIndexPanel(Class<? extends Element> type) {
    JPanel input = factory.createPanel();
    input.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel indexLabel = factory.createLabel();
    indexLabel.setText("Index");
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 0.2;
    input.add(indexLabel, gbc);

    String[] names = isNodeIndex(type) ? db.nodeIndexNames() : db.relationshipIndexNames();
    JComboBox indexCombo = factory.createComboBox(names, true, false);
    gbc.gridx = 1;
    gbc.weightx = 0.8;
    input.add(indexCombo, gbc);
    if (isNodeIndex(type)) {
      this.nodeIndexCombo = indexCombo;
    } else {
      this.relationIndexCombo = indexCombo;
    }

    JLabel queryLabel = factory.createLabel();
    queryLabel.setText("Query");
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 0.2;
    input.add(queryLabel, gbc);

    JTextField queryField = factory.createTextField();
    gbc.gridx = 1;
    gbc.weightx = 0.8;
    input.add(queryField, gbc);
    if (isNodeIndex(type)) {
      this.nodeQueryField = queryField;
    } else {
      this.relationQueryField = queryField;
    }

    return input;
  }
  
  public String getIndexName() {
    String value = (String) (isNodeIndexSelected() ? nodeIndexCombo.getSelectedItem() : relationIndexCombo.getSelectedItem());
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    return value;
  }
  
  public String getQuery() {
    String value = isNodeIndexSelected() ? nodeQueryField.getText() : relationQueryField.getText();
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    return value;
  }

  public boolean isNodeIndexSelected() {
    return tabs.getSelectedIndex() == 0;
  }

  public boolean isRelationshipIndexSelected() {
    return tabs.getSelectedIndex() == 1;
  }
  
  public static boolean isRelationshipIndex(Class<? extends Element> type) {
    return Edge.class.isAssignableFrom(type);
  }

  public static boolean isNodeIndex(Class<? extends Element> type) {
    return Vertex.class.isAssignableFrom(type);
  }
}
