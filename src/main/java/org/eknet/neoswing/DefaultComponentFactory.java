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

package org.eknet.neoswing;

import javax.swing.*;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:49
 */
public class DefaultComponentFactory implements ComponentFactory {

  @Override
  public JPanel createPanel() {
    return new JPanel();
  }

  @Override
  public JSplitPane createSplitPane() {
    return new JSplitPane();
  }

  @Override
  public JTabbedPane createTabbedPane() {
    return new JTabbedPane();
  }

  @Override
  public JTabbedPane createTabbedPane(Action closeAction) {
    return createTabbedPane();
  }

  @Override
  public JToolBar createToolbar() {
    return new JToolBar();
  }

  @Override
  public JButton createToolbarButton() {
    JButton button = new JButton();
    button.setHideActionText(true);
    button.setBorderPainted(false);
    button.setHorizontalTextPosition(JButton.CENTER);
    button.setVerticalTextPosition(JButton.BOTTOM);
    return button;
  }

  @Override
  public JButton createButton() {
    return new JButton();
  }

  @Override
  public JComboBox createComboBox() {
    return new JComboBox();
  }

  @Override
  public JComboBox createComboBox(Object[] values, boolean required, boolean editable) {
    JComboBox box = createComboBox();
    Object[] items = values;
    if (!required) {
      items = new Object[values.length + 1];
      items[0] = null;
      System.arraycopy(values, 1, items, 0, values.length);
    }
    DefaultComboBoxModel model = new DefaultComboBoxModel(items);
    box.setModel(model);
    if (required) {
      if (values.length > 0) {
        box.setSelectedItem(values[0]);
      }
    }
    box.setEditable(editable);
    return box;
  }

  @Override
  public JTable createTable() {
    return new JTable();
  }

  @Override
  public JLabel createLabel() {
    return new JLabel();
  }

  @Override
  public JTextField createTextField() {
    return new JTextField();
  }

  @Override
  public JPopupMenu createPopupMenu() {
    return new JPopupMenu();
  }

  @Override
  public void addMenuItemGroup(JComponent menu, Iterable<? extends Action> actions) {
    for (Action action : actions) {
      menu.add(new JMenuItem(action));
    }
  }

}
