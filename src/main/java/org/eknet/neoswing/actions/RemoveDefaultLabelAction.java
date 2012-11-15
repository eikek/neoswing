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
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.ElementId;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.NeoSwing;
import org.eknet.neoswing.view.DefaultVisualizationViewFactory;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 18:44
 */
public class RemoveDefaultLabelAction extends AbstractAction {

  private final static Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);

  private final GraphModel model;
  private ElementId<?> element;

  public RemoveDefaultLabelAction(GraphModel model, ElementId<?> element) {
    this.model = model;
    setElement(element);
    putValue(NAME, "Remove default label");
    putValue(SHORT_DESCRIPTION, "Removes the default label settings for this node");
  }

  public void setElement(ElementId<?> element) {
    this.element = element;
    setEnabled(this.element != null);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (element == null) {
      return;
    }
    model.execute(new DbAction<Object, Object>() {
      @Override
      protected Object doInTx(GraphModel model) {
        Element el = model.getDatabase().lookup(element);
        String key = DefaultVisualizationViewFactory.createDefaultLabelPrefKey(el, model.getDatabase());
        prefs.remove(key);
        return null;
      }
    });
  }
}
