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

import org.eknet.neoswing.NeoSwing;
import org.eknet.neoswing.view.DefaultVisualizationViewFactory;
import org.neo4j.graphdb.PropertyContainer;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 18:44
 */
public class RemoveDefaultLabelAction extends AbstractAction {

  private final static Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);
  private PropertyContainer element;

  public RemoveDefaultLabelAction(PropertyContainer element) {
    setElement(element);
    putValue(NAME, "Remove default label");
    putValue(SHORT_DESCRIPTION, "Removes the default label settings for this node");
  }

  public PropertyContainer getElement() {
    return element;
  }

  public void setElement(PropertyContainer element) {
    this.element = element;
    setEnabled(this.element != null);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (element == null) {
      return;
    }
    String key = DefaultVisualizationViewFactory.createDefaultLabelPrefKey(element);
    prefs.remove(key);
  }
}
