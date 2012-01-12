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
import org.eknet.neoswing.utils.Dialogs;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.jetbrains.annotations.Nullable;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Transaction;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 13:50
 */
public class DeletePropertyAction extends AbstractSwingAction {

  private PropertyContainer element;
  private String key;

  public DeletePropertyAction() {
    this(null, null);
  }

  public DeletePropertyAction(@Nullable PropertyContainer element, @Nullable String key) {
    this.element = element;
    this.key = key;

    putValue(NAME, "Delete Property");
    putValue(SHORT_DESCRIPTION, "Delete Property");
    putValue(SMALL_ICON, NeoSwingUtil.icon("delete"));
  }

  public PropertyContainer getElement() {
    return element;
  }

  public void setElement(PropertyContainer element) {
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
      Transaction tx = element.getGraphDatabase().beginTx();
      try {
        if (element.hasProperty(key)) {
          element.removeProperty(key);
        }
        tx.success();
      } finally {
        tx.finish();
      }
    }
  }
}
