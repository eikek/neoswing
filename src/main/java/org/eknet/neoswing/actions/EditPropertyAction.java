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

package org.eknet.neoswing.actions;

import com.tinkerpop.blueprints.Element;
import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.utils.Dialog;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.view.PropertyEditor;

import java.awt.event.ActionEvent;

import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 11:27
 */
public class EditPropertyAction extends AbstractSwingAction {

  private final GraphDb db;
  private Element element;
  private String key;

  /**
   * Creates a action that will popup a dialog to edit the property with the specified
   * key of the specified element.
   *
   * @param element
   * @param key
   * @param db
   */
  public EditPropertyAction(Element element, String key, GraphDb db) {
    this.db = db;
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
   * @param db
   */
  public EditPropertyAction(Element element, GraphDb db) {
    this(element, null, db);
  }
  
  public EditPropertyAction(GraphDb db) {
    this(null, null, db);
  }

  public Element getElement() {
    return element;
  }

  public void setElement(Element element) {
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
    Dialog dialog = PropertyEditor.inDialog(db, element, key);
    dialog.show(getWindow(e), APPLICATION_MODAL);
  }
}
