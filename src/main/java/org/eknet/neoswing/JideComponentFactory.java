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

import com.jidesoft.swing.AutoCompletionComboBox;
import com.jidesoft.swing.ButtonStyle;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideComboBox;
import com.jidesoft.swing.JideMenu;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.JideSplitButton;
import com.jidesoft.swing.JideTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:49
 */
public class JideComponentFactory extends DefaultComponentFactory implements ComponentFactory {

  @NotNull
  @Override
  public JTabbedPane createTabbedPane() {
    return new JideTabbedPane();
  }

  @NotNull
  @Override
  public JButton createToolbarButton() {
    JideButton button = new JideButton();
    button.setHideActionText(true);
    button.setBorderPainted(false);
    button.setHorizontalTextPosition(JButton.CENTER);
    button.setVerticalTextPosition(JButton.BOTTOM);
    button.setButtonStyle(ButtonStyle.TOOLBAR_STYLE);
    return button;
  }

  @NotNull
  @Override
  public JComboBox createComboBox() {
    return new JideComboBox();
  }

  @NotNull
  @Override
  public JComboBox createComboBox(Object[] values, boolean required, boolean editable) {
    AutoCompletionComboBox box = new AutoCompletionComboBox();
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
    box.setStrict(!editable);

    return box;
  }

  @Override
  public JPopupMenu createPopupMenu() {
    return new JidePopupMenu();
  }

  @Override
  public void addMenuItemGroup(JComponent menu, Iterable<? extends Action> actions) {
    JideMenu splitButton = new JideSplitButton();
    menu.add(splitButton);
    boolean first = true;
    for (Action action : actions) {
      if (first) {
        splitButton.setAction(action);
        first = false;
      } else {
        splitButton.add(action);
      }
    }
  }
}
