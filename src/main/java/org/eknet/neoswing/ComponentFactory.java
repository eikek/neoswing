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
public interface ComponentFactory {

  JPanel createPanel();

  JSplitPane createSplitPane();

  JTabbedPane createTabbedPane();

  JTabbedPane createTabbedPane(Action closeAction);

  JToolBar createToolbar();

  JButton createToolbarButton();

  JButton createButton();

  JComboBox createComboBox();

  JComboBox createComboBox(Object[] values, boolean required, boolean editable);

  JTable createTable();

  JLabel createLabel();

  JTextField createTextField();

  JPopupMenu createPopupMenu();

  void addMenuItemGroup(JComponent menu, Iterable<? extends Action> actions);
}
