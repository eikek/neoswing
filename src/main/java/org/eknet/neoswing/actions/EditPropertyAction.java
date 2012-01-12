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

package org.eknet.neoswing.actions;

import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.view.PropertyEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.PropertyContainer;

import java.awt.event.ActionEvent;

import static java.awt.Dialog.ModalityType.*;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 11:27
 */
public class EditPropertyAction extends AbstractSwingAction {

  private PropertyContainer element;
  private String key;

  /**
   * Creates a action that will popup a dialog to edit the property with the specified
   * key of the specified element.
   *
   * @param element
   * @param key
   */
  public EditPropertyAction(@Nullable PropertyContainer element, @Nullable String key) {
    setElement(element);
    setKey(key);

    if (key == null) {
      putValue(NAME, "Add Property");
      putValue(SMALL_ICON, NeoSwingUtil.icon("add"));
      putValue(SHORT_DESCRIPTION, "Add new property");
    } else {
      putValue(SMALL_ICON, NeoSwingUtil.icon("pencil"));
      putValue(NAME, "Edit Property");
      putValue(SHORT_DESCRIPTION, "Edit property");
    }
  }

  /**
   * Creates a action that will popup a dialog to add a new property to the
   * specified element.
   *
   * @param element
   */
  public EditPropertyAction(@NotNull PropertyContainer element) {
    this(element, null);
  }
  
  public EditPropertyAction() {
    this(null, null);
  }

  public PropertyContainer getElement() {
    return element;
  }

  public void setElement(PropertyContainer element) {
    this.element = element;
    setEnabled(element != null);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Dialog dialog = PropertyEditor.inDialog(element, key);
    dialog.show(getWindow(e), APPLICATION_MODAL);
  }
}
