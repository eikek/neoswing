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
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 13:50
 */
public class DeletePropertyAction extends AbstractSwingAction {

  private final GraphDb db;
  private Element element;
  private String key;

  public DeletePropertyAction(GraphDb db) {
    this(db, null, null);
  }

  public DeletePropertyAction(GraphDb db, Element element, String key) {
    this.db = db;
    this.element = element;
    this.key = key;

    putValue(NAME, "Delete Property");
    putValue(SHORT_DESCRIPTION, "Delete Property");
    putValue(SMALL_ICON, NeoSwingUtil.icon("delete"));
  }

  public Element getElement() {
    return element;
  }

  public void setElement(Element element) {
    this.element = element;
    setEnabled(element != null && key != null);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (element != null && key != null) {
      Dialog.Option option = Dialogs.confirm(getWindow(e), "Really delete the property?");
      if (option != Dialog.Option.OK) {
        return;
      }
      GraphDb.Tx tx = db.beginTx();
      try {
        if (element.getProperty(key) != null) {
          element.removeProperty(key);
        }
        tx.success();
      } finally {
        tx.finish();
      }
    }
  }
}
