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
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the {@link RelationshipType}s of the relationships of a node.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 14:55
 */
public class RelationTypesPanel extends JPanel {

  private GraphModel model;
  private Node node;

  private JLabel infoLabel;

  private final ComponentFactory factory;
  private final RelationTypeTableModel tableModel = new RelationTypeTableModel();
  
  public RelationTypesPanel() {
    this(NeoSwingUtil.getFactory(true));
  }

  public RelationTypesPanel(Node node) {
    this(NeoSwingUtil.getFactory(true));
    setNode(node);
  }

  public RelationTypesPanel(@NotNull ComponentFactory factory) {
    super(new BorderLayout(), true);
    this.factory = factory;
    
    final JTable table = factory.createTable();
    table.setModel(tableModel);
    table.getColumnModel().getColumn(1).setPreferredWidth(50);
    table.getColumnModel().getColumn(2).setPreferredWidth(50);
    table.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        table.getColumnModel().getColumn(0).setPreferredWidth(table.getWidth() - 100);
      }
    });
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(createHeadPanel(), BorderLayout.NORTH);
  }

  private JPanel createHeadPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
    infoLabel = factory.createLabel();
    infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
    panel.add(infoLabel);

    return panel;
  }

  private void updateComponents() {
    tableModel.reload();
    if (node != null) {
      StringBuilder text = new StringBuilder();
      text.append("RelationshipTypes of ");
      text.append("node ").append(node.getId());
      text.append(" [").append(tableModel.getRowCount()).append("]");
      infoLabel.setText(text.toString());
    } else {
      infoLabel.setText(null);
    } 
  }

  private GraphModel getModel() {
    if (model == null) {
      model = NeoSwingUtil.getGraphModel(this);
    }
    return model;
  }

  public void setNode(@Nullable Node node) {
    if (node != null && this.node != null) {
      if (node.getId() == this.node.getId()) {
        return;
      }
    }
    this.node = node;
    updateComponents();
  }

  public Node getNode() {
    return node;
  }

  private void setRelationshipsVisible(@NotNull RelationshipType type, @Nullable Direction direction) {
    Iterable<Relationship> relationships = direction != null
            ? node.getRelationships(direction, type)
            : new ArrayList<Relationship>();
    for (Relationship rt : relationships) {
      NeoSwingUtil.addEdge(getModel().getGraph(), rt);
    }
    if (direction != Direction.BOTH) {
      relationships = direction != null
              ? node.getRelationships(direction.reverse(), type)
              : node.getRelationships(type);
      for (Relationship rt : relationships) {
        getModel().getGraph().removeEdge(rt);
        getModel().getGraph().removeVertex(rt.getOtherNode(node));
      }
    }
    getModel().getViewer().repaint();
  }

  final class RelationTypeTableModel extends AbstractTableModel {

    private List<RelationTypeEntry> data = new ArrayList<RelationTypeEntry>();
    private final String[] cols = new String[]{"Type", "In", "Out"};
    
    public void reload() {
      this.data = new ArrayList<RelationTypeEntry>();
      load();
    }
    
    private void load() {
      if (data.isEmpty() && node != null) {
        for (Relationship rt : node.getRelationships()) {
          RelationTypeEntry entry = new RelationTypeEntry(rt.getType());
          if (!data.contains(entry)) {
            data.add(entry);
          }
        }
        fireTableDataChanged();
      }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return columnIndex > 0;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
      RelationTypeEntry entry = data.get(rowIndex);
      if (columnIndex == 1) {
        entry.visibleIn = !entry.visibleIn;
      }
      if (columnIndex == 2) {
        entry.visibleOut = !entry.visibleOut;
      }
      
      setRelationshipsVisible(entry.type, entry.getVisibleDirection());
    }

    @Override
    public String getColumnName(int column) {
      return cols[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0) {
        return String.class;
      }
      if (columnIndex == 1 || columnIndex == 2) {
        return Boolean.class;
      }
      return super.getColumnClass(columnIndex);
    }

    @Override
    public int getRowCount() {
      load();
      return data.size();
    }

    @Override
    public int getColumnCount() {
      return cols.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      load();
      RelationTypeEntry e = data.get(rowIndex);
      if (columnIndex == 0) {
        return e.type.name();
      }
      if (columnIndex == 1) {
        return e.visibleIn;
      }
      if (columnIndex == 2) {
        return e.visibleOut;
      }
      return null;
    }
  }
  private static class RelationTypeEntry {
    
    private RelationshipType type;
    private boolean visibleIn;
    private boolean visibleOut;

    private RelationTypeEntry(RelationshipType type, boolean visibleIn, boolean visibleOut) {
      this.type = type;
      this.visibleIn = visibleIn;
      this.visibleOut = visibleOut;
    }

    private RelationTypeEntry(RelationshipType type) {
      this(type, true, true);
    }

    public Direction getVisibleDirection() {
      if (visibleIn && visibleOut) {
        return Direction.BOTH;
      }
      if (visibleIn) {
        return Direction.INCOMING;
      }
      if (visibleOut) {
        return Direction.OUTGOING;
      }
      return null;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      RelationTypeEntry that = (RelationTypeEntry) o;

      if (type != null ? !type.name().equals(that.type.name()) : that.type != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return type != null ? type.name().hashCode() : 0;
    }
  }
}
