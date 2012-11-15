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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import org.eknet.neoswing.ComponentFactory;
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.ElementId;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.NeoSwingUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays the {@code Label}s of the relationships of a node.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 14:55
 */
public class RelationTypesPanel extends JPanel {

  private final GraphModel model;
  private ElementId<Vertex> nodeId;

  private JLabel infoLabel;

  private final ComponentFactory factory;
  private final RelationTypeTableModel tableModel = new RelationTypeTableModel();
  

  public RelationTypesPanel(GraphModel model, ComponentFactory factory) {
    super(new BorderLayout(), true);
    this.factory = factory;
    this.model = model;
    
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
    JPanel panel = factory.createPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEADING));
    infoLabel = factory.createLabel();
    infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
    panel.add(infoLabel);

    return panel;
  }

  private void updateComponents() {
    tableModel.reload();
    if (nodeId != null) {
      model.execute(new DbAction<String, Object>() {
        @Override
        protected String doInTx(GraphModel model) {
          Vertex v = model.getDatabase().lookup(nodeId);
          StringBuilder text = new StringBuilder();
          text.append("RelationshipTypes of ");
          text.append("node ").append(v.getId());
          text.append(" [").append(tableModel.getRowCount()).append("]");
          return text.toString();
        }

        @Override
        protected void done() {
          infoLabel.setText(safeGet());
        }
      });
    } else {
      infoLabel.setText(null);
    } 
  }

  public void setNodeId(ElementId<Vertex> id) {
    if (id != null && this.nodeId != null) {
      if (id.equals(this.nodeId)) {
        return;
      }
    }
    this.nodeId = id;
    updateComponents();
  }

  private void setRelationshipsVisible(final String type, final Direction direction) {
    model.execute(new DbAction<Object, Runnable>() {
      @Override
      protected Object doInTx(GraphModel model) {
        final Vertex v = model.getDatabase().lookup(nodeId);
        Iterable<Edge> relationships = direction != null
            ? v.getEdges(direction, type)
            : new ArrayList<Edge>();
        for (final Edge edge : relationships) {
          publish(new Runnable() {
            @Override
            public void run() {
              NeoSwingUtil.addEdge(getModel().getGraph(), edge);
            }
          });
        }
        if (direction != Direction.BOTH) {
          relationships = direction != null
              ? v.getEdges(direction.opposite(), type)
              : v.getEdges(Direction.BOTH, type);
          for (final Edge rt : relationships) {
            publish(new Runnable() {
              @Override
              public void run() {
                getModel().getGraph().removeEdge(rt);
                getModel().getGraph().removeVertex(GraphDb.getOtherNode(rt, v));
              }
            });
          }
        }
        return null;
      }

      @Override
      protected void process(List<Runnable> chunks) {
        for (Runnable r : chunks) {
          r.run();
        }
      }

      @Override
      protected void done() {
        getModel().getViewer().repaint();
      }
    });
  }

  final class RelationTypeTableModel extends AbstractTableModel {

    private List<RelationTypeEntry> data = new ArrayList<RelationTypeEntry>();
    private final String[] cols = new String[]{"Type", "In", "Out"};
    
    public void reload() {
      this.data = new ArrayList<RelationTypeEntry>();
      load();
    }
    
    private void load() {
      if (data.isEmpty() && nodeId != null) {
        model.execute(new DbAction<Object, Object>() {
          @Override
          protected Object doInTx(GraphModel model) {
            //noinspection ConstantConditions
            if (nodeId != null) {
              Vertex v = model.getDatabase().lookup(nodeId);
              for (Edge rt : v.getEdges(Direction.BOTH)) {
                RelationTypeEntry entry = new RelationTypeEntry(rt.getLabel());
                if (!data.contains(entry)) {
                  data.add(entry);
                }
              }
            }
            return null;
          }

          @Override
          protected void done() {
            fireTableDataChanged();
          }
        });
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
        return e.type;
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
    
    private String type;
    private boolean visibleIn;
    private boolean visibleOut;

    private RelationTypeEntry(String type, boolean visibleIn, boolean visibleOut) {
      this.type = type;
      this.visibleIn = visibleIn;
      this.visibleOut = visibleOut;
    }

    private RelationTypeEntry(String type) {
      this(type, true, true);
    }

    public Direction getVisibleDirection() {
      if (visibleIn && visibleOut) {
        return Direction.BOTH;
      }
      if (visibleIn) {
        return Direction.IN;
      }
      if (visibleOut) {
        return Direction.OUT;
      }
      return null;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      RelationTypeEntry that = (RelationTypeEntry) o;

      if (type != null ? !type.equals(that.type) : that.type != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return type != null ? type.hashCode() : 0;
    }
  }
}
