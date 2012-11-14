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

import com.jidesoft.swing.*;

import javax.swing.*;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:49
 */
public class JideComponentFactory extends DefaultComponentFactory implements ComponentFactory {

  @Override
  public JTabbedPane createTabbedPane() {
    return new JideTabbedPane();
  }

  @Override
  public JTabbedPane createTabbedPane(Action closeAction) {
    JideTabbedPane tp = new JideTabbedPane();
    tp.setShowCloseButtonOnTab(true);
    tp.setCloseAction(closeAction);
    return tp;
  }

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

  @Override
  public JComboBox createComboBox() {
    return new JideComboBox();
  }

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
