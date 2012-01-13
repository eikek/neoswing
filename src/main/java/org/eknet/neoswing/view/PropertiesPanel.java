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
import org.eknet.neoswing.actions.DeletePropertyAction;
import org.eknet.neoswing.actions.EditPropertyAction;
import org.eknet.neoswing.actions.SetDefaultLabelAction;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.PopupTrigger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.event.PropertyEntry;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows the properties of a {@link PropertyContainer}.
 * <p/>
 * Do not use the same instance for {@link PropertyContainer}s coming from
 * different databases (or change the class to cleanup the {@link TransactionEventHandler}
 * properly).
 * 
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 19:19
 */
public class PropertiesPanel extends JPanel {

  private PropertyContainer element;

  private final ComponentFactory factory;
  private final PropertiesTableModel tableModel = new PropertiesTableModel();
  private final TransactionEventHandler<Boolean> updateHandler = new TransactionEventHandler<Boolean>() {
    @Override
    public Boolean beforeCommit(TransactionData data) throws Exception {
      if (element instanceof Node) {
        Node node = (Node) element;
        if (data.isDeleted(node)) {
          return null;
        } else {
          for (PropertyEntry<Node> entry : data.assignedNodeProperties()) {
            if (entry.entity().getId() == node.getId()) {
              return true;
            }
          }
          for (PropertyEntry<Node> entry : data.removedNodeProperties()) {
            if (entry.entity().getId() == node.getId()) {
              return true;
            }
          }
        } 
      }
      if (element instanceof Relationship) {
        Relationship relationship = (Relationship) element;
        if (data.isDeleted(relationship)) {
          return null;
        } else {
          for (PropertyEntry<Relationship> entry : data.assignedRelationshipProperties()) {
            if (entry.entity().getId() == relationship.getId()) {
              return true;
            }
          }
          for (PropertyEntry<Relationship> entry : data.assignedRelationshipProperties()) {
            if (entry.entity().getId() == relationship.getId()) {
              return true;
            }
          }
        }
      }
      return false;
    }

    @Override
    public void afterCommit(TransactionData data, Boolean state) {
      if (state == null) {
        setElement(null);
        updateComponents();
      }
      if (state != null && state) {
        updateComponents();
      }
    }

    @Override
    public void afterRollback(TransactionData data, Boolean state) {
    }
  };

  private final EditPropertyAction addPropertyAction = new EditPropertyAction();

  private JLabel infoLabel;
  private JTable table;

  private final PopupTrigger popupTrigger = new PopupTrigger(true) {
    @Override
    protected JPopupMenu getPopupMenu() {
      return createPopup();
    }
  };
  
  public PropertiesPanel(@NotNull ComponentFactory factory, @Nullable final PropertyContainer element) {
    super(new BorderLayout(), true);
    this.element = element;
    this.factory = factory;

    table = factory.createTable();
    table.setModel(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.addMouseListener(popupTrigger);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(createHeadPanel(), BorderLayout.NORTH);
    updateComponents();
  }

  public PropertiesPanel(PropertyContainer element) {
    this(NeoSwingUtil.getFactory(true), element);
  }
  
  public PropertiesPanel() {
    this(NeoSwingUtil.getFactory(true), null);
  }



  private JPanel createHeadPanel() {
    JPanel main = factory.createPanel();
    main.setLayout(new BorderLayout());
    JToolBar bar = factory.createToolbar();
    main.add(bar, BorderLayout.NORTH);

    JButton button = factory.createToolbarButton();
    button.setAction(addPropertyAction);
    bar.add(button);

    JPanel panel = factory.createPanel();
    panel.setLayout(new FlowLayout(FlowLayout.LEADING));
    main.add(panel, BorderLayout.CENTER);
    infoLabel = factory.createLabel();
    infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
    panel.add(infoLabel);

    return main;
  }

  private void updateComponents() {
    tableModel.reload();
    addPropertyAction.setElement(element);
    
    if (element != null) {
      StringBuilder text = new StringBuilder();
      text.append("Properties of ");
      if (element instanceof Node) {
        Node node = (Node) element;
        text.append("node ").append(node.getId());
      }
      if (element instanceof Relationship) {
        Relationship relationship = (Relationship) element;
        text.append("relationship ")
            .append(relationship.getId())
            .append(" / ")
            .append(relationship.getType().name());
      }
      text.append(" [").append(tableModel.getRowCount()).append("]");
      infoLabel.setText(text.toString());
    } 
  }

  public PropertyContainer getElement() {
    return element;
  }

  public void setElement(@Nullable PropertyContainer element) {
    if (NeoSwingUtil.equals(this.element, element)) {
      return;
    }
    if (this.element != null) {
      unregisterUpdateHandler();
    }
    this.element = element;
    if (this.element != null) {
      this.element.getGraphDatabase().registerTransactionEventHandler(updateHandler);
    }
    updateComponents();
  }

  private JPopupMenu createPopup() {
    JPopupMenu menu = factory.createPopupMenu();
    Window owner = NeoSwingUtil.findOwner(this);
    if (element != null) {
      int index = table.getSelectedRow();
      if (index >= 0) {
        Entry entry = tableModel.getEntry(index);
        EditPropertyAction editAction = new EditPropertyAction(element, entry.key);
        editAction.setWindow(owner);
        menu.add(new JMenuItem(editAction));

        DeletePropertyAction deleteAction = new DeletePropertyAction(element, entry.key);
        deleteAction.setWindow(owner);
        menu.add(new JMenuItem(deleteAction));

        menu.addSeparator();
        menu.add(new JMenuItem(new SetDefaultLabelAction(element, entry.key)));
      }
    }
    menu.addSeparator();
    addPropertyAction.setWindow(owner);
    menu.add(new JMenuItem(addPropertyAction));
    return menu;
  }
  
  @Override
  protected void finalize() throws Throwable {
    if (this.element != null) {
      unregisterUpdateHandler();
    }
    super.finalize();
  }

  private void unregisterUpdateHandler() {
    // just to avoid IllegalStateException. Neo4j will throw if unregistering an
    // unregistered handler, but do nothing if registering the same handler twice
    // so the next line ensures that there is one to unregister with the second
    this.element.getGraphDatabase().registerTransactionEventHandler(updateHandler);
    this.element.getGraphDatabase().unregisterTransactionEventHandler(updateHandler);
  }


  private final class PropertiesTableModel extends AbstractTableModel {

    private List<Entry> data = new ArrayList<Entry>();
    private final String[] cols = new String[]{"Key", "Value"};
    
    public void reload() {
      this.data = new ArrayList<Entry>();
      load();
    }
    
    private void load() {
      if (data.isEmpty() && element != null) {
        for (String key : element.getPropertyKeys()) {
          data.add(new Entry(key, element.getProperty(key)));
        }
        fireTableDataChanged();
      }
    }

    @Override
    public String getColumnName(int column) {
      return cols[column];
    }

    @Override
    public int getRowCount() {
      return data.size();
    }

    @Override
    public int getColumnCount() {
      return cols.length;
    }

    public Entry getEntry(int index) {
      return data.get(index);
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      Entry entry = data.get(rowIndex);
      if (columnIndex == 0) {
        return entry.key;
      }
      if (columnIndex == 1) {
        return entry.value.toString();
      }
      return null;
    }
  }
  
  private static class Entry {
    private String key;
    private Object value;

    private Entry(String key, Object value) {
      this.key = key;
      this.value = value;
    }
  }
}
