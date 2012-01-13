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

package org.eknet.neoswing;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:49
 */
public class DefaultComponentFactory implements ComponentFactory {

  @NotNull
  @Override
  public JPanel createPanel() {
    return new JPanel();
  }

  @NotNull
  @Override
  public JSplitPane createSplitPane() {
    return new JSplitPane();
  }

  @NotNull
  @Override
  public JTabbedPane createTabbedPane() {
    return new JTabbedPane();
  }

  @NotNull
  @Override
  public JToolBar createToolbar() {
    return new JToolBar();
  }

  @NotNull
  @Override
  public JButton createToolbarButton() {
    JButton button = new JButton();
    button.setHideActionText(true);
    button.setBorderPainted(false);
    button.setHorizontalTextPosition(JButton.CENTER);
    button.setVerticalTextPosition(JButton.BOTTOM);
    return button;
  }

  @NotNull
  @Override
  public JButton createButton() {
    return new JButton();
  }

  @NotNull
  @Override
  public JComboBox createComboBox() {
    return new JComboBox();
  }

  @NotNull
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

  @NotNull
  @Override
  public JTable createTable() {
    return new JTable();
  }

  @NotNull
  @Override
  public JLabel createLabel() {
    return new JLabel();
  }

  @NotNull
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
