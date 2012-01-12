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
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 16:16
 */
public class SelectIndexPanel extends JPanel {

  private final ComponentFactory factory;
  private final GraphDatabaseService db;

  private JComboBox nodeIndexCombo;
  private JTextField nodeQueryField;

  private JComboBox relationIndexCombo;
  private JTextField relationQueryField;

  private JTabbedPane tabs;

  public SelectIndexPanel(GraphDatabaseService db, ComponentFactory factory) {
    this.db = db;
    this.factory = factory;
    initComponents();
  }

  public SelectIndexPanel(GraphDatabaseService db) {
    this(db, NeoSwingUtil.getFactory(true));
  }

  private void initComponents() {
    setLayout(new BorderLayout(5, 5));

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
    add(top, BorderLayout.NORTH);
    top.add(new JLabel("Please select an index and query to execute against that index."));

    tabs = factory.createTabbedPane();
    add(tabs, BorderLayout.CENTER);
    JPanel nodeTab = createIndexPanel(Node.class);
    tabs.addTab("Nodes", nodeTab);
    JPanel relationTab = createIndexPanel(Relationship.class);
    tabs.addTab("Relationships", relationTab);
  }

  private JPanel createIndexPanel(Class<? extends PropertyContainer> type) {
    JPanel input = new JPanel();
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
    
    String[] names = isNodeIndex(type) ? db.index().nodeIndexNames() : db.index().relationshipIndexNames();
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
  
  public static boolean isRelationshipIndex(Class<? extends PropertyContainer> type) {
    return Relationship.class.isAssignableFrom(type);
  }

  public static boolean isNodeIndex(Class<? extends PropertyContainer> type) {
    return Node.class.isAssignableFrom(type);
  }
}
