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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:49
 */
public interface ComponentFactory {

  @NotNull
  JTabbedPane createTabbedPane();

  @NotNull
  JToolBar createToolbar();

  @NotNull
  JButton createToolbarButton();

  @NotNull
  JButton createButton();

  @NotNull
  JComboBox createComboBox();

  @NotNull
  JComboBox createComboBox(Object[] values, boolean required, boolean editable);

  @NotNull
  JTable createTable();

  @NotNull
  JLabel createLabel();

  @NotNull
  JTextField createTextField();

  JPopupMenu createPopupMenu();

  void addMenuItemGroup(JComponent menu, Iterable<? extends Action> actions);
}
