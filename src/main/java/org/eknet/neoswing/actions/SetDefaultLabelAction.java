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
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.view.DefaultVisualizationViewFactory;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 18:29
 */
public class SetDefaultLabelAction extends AbstractSwingAction {
  
  private final GraphModel model;

  private ElementId<?> element;
  private String key;

  public SetDefaultLabelAction(GraphModel model, ElementId<?> element, String key) {
    this.model = model;
    setElement(element);
    setKey(key);
    putValue(NAME, "Set default label");
    putValue(SHORT_DESCRIPTION, "Sets this property to use for the node label in the graph");
  }

  public SetDefaultLabelAction(GraphModel model) {
    this(model, null, null);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (element == null || key == null) {
      return;
    }
    model.execute(new DbAction<Object, Object>() {
      @Override
      protected Object doInTx(GraphModel model) {
        Element el = model.getDatabase().lookup(element);
        String prefKey = DefaultVisualizationViewFactory.createDefaultLabelPrefKey(el, getModel().getDatabase());
        NeoSwingUtil.getPrefs().put(prefKey, key);
        return null;
      }
    });

  }

  public void setElement(ElementId<?> element) {
    this.element = element;
    setEnabled(element != null && key != null);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
    setEnabled(element != null && key != null);
  }
}
