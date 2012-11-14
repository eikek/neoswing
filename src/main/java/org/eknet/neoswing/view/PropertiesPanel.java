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
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.actions.DeletePropertyAction;
import org.eknet.neoswing.actions.EditPropertyAction;
import org.eknet.neoswing.actions.SetDefaultLabelAction;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.PopupTrigger;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows the properties of a {@link Element}.
 * <p/>
 * Do not use the same instance for {@link Element}s coming from
 * different databases (or change the class to cleanup the {@code TransactionEventHandler}
 * properly).
 * 
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 19:19
 */
public class PropertiesPanel extends JPanel {

  private Element element;

  private final ComponentFactory factory;
  private final PropertiesTableModel tableModel = new PropertiesTableModel();
//  private final TransactionEventHandler<Boolean> updateHandler = new TransactionEventHandler<Boolean>() {
//    @Override
//    public Boolean beforeCommit(TransactionData data) throws Exception {
//      if (element instanceof Node) {
//        Node node = (Node) element;
//        if (data.isDeleted(node)) {
//          return null;
//        } else {
//          for (PropertyEntry<Node> entry : data.assignedNodeProperties()) {
//            if (entry.entity().getId() == node.getId()) {
//              return true;
//            }
//          }
//          for (PropertyEntry<Node> entry : data.removedNodeProperties()) {
//            if (entry.entity().getId() == node.getId()) {
//              return true;
//            }
//          }
//        }
//      }
//      if (element instanceof Relationship) {
//        Relationship relationship = (Relationship) element;
//        if (data.isDeleted(relationship)) {
//          return null;
//        } else {
//          for (PropertyEntry<Relationship> entry : data.assignedRelationshipProperties()) {
//            if (entry.entity().getId() == relationship.getId()) {
//              return true;
//            }
//          }
//          for (PropertyEntry<Relationship> entry : data.assignedRelationshipProperties()) {
//            if (entry.entity().getId() == relationship.getId()) {
//              return true;
//            }
//          }
//        }
//      }
//      return false;
//    }
//
//    @Override
//    public void afterCommit(TransactionData data, Boolean state) {
//      if (state == null) {
//        setElement(null);
//        updateComponents();
//      }
//      if (state != null && state) {
//        updateComponents();
//      }
//    }
//
//    @Override
//    public void afterRollback(TransactionData data, Boolean state) {
//    }
//  };

  private final GraphModel model;
  private final EditPropertyAction addPropertyAction;

  private JLabel infoLabel;
  private JTable table;

  private final PopupTrigger popupTrigger = new PopupTrigger(true) {
    @Override
    protected JPopupMenu getPopupMenu() {
      return createPopup();
    }
  };
  
  public PropertiesPanel(GraphModel model, ComponentFactory factory, final Element element) {
    super(new BorderLayout(), true);
    this.element = element;
    this.factory = factory;
    this.model = model;
    this.addPropertyAction = new EditPropertyAction(model.getDatabase());


    table = factory.createTable();
    table.setModel(tableModel);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.addMouseListener(popupTrigger);
    add(new JScrollPane(table), BorderLayout.CENTER);
    add(createHeadPanel(), BorderLayout.NORTH);
    updateComponents();
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
      if (element instanceof Vertex) {
        Vertex node = (Vertex) element;
        text.append("node ").append(node.getId());
      }
      if (element instanceof Edge) {
        Edge relationship = (Edge) element;
        text.append("relationship ")
            .append(relationship.getId())
            .append(" / ")
            .append(relationship.getLabel());
      }
      text.append(" [").append(tableModel.getRowCount()).append("]");
      infoLabel.setText(text.toString());
    } 
  }

  public Element getElement() {
    return element;
  }

  public void setElement(Element element) {
    if (NeoSwingUtil.equals(this.element, element)) {
      return;
    }
    if (this.element != null) {
      unregisterUpdateHandler();
    }
    this.element = element;
    if (this.element != null) {
//      this.element.getGraphDatabase().registerTransactionEventHandler(updateHandler);
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
        EditPropertyAction editAction = new EditPropertyAction(element, entry.key, model.getDatabase());
        editAction.setWindow(owner);
        menu.add(new JMenuItem(editAction));

        DeletePropertyAction deleteAction = new DeletePropertyAction(model.getDatabase(), element, entry.key);
        deleteAction.setWindow(owner);
        menu.add(new JMenuItem(deleteAction));

        menu.addSeparator();
        menu.add(new JMenuItem(new SetDefaultLabelAction(model.getDatabase(), element, entry.key)));
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
//    this.element.getGraphDatabase().registerTransactionEventHandler(updateHandler);
//    this.element.getGraphDatabase().unregisterTransactionEventHandler(updateHandler);
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
