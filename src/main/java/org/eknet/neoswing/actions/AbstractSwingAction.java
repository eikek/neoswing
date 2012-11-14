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

import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 21:59
 */
public abstract class AbstractSwingAction extends javax.swing.AbstractAction {

  private Window window;

  public void setWindow(Window window) {
    this.window = window;
  }

  protected Window getWindow(ActionEvent e) {
    Window w = NeoSwingUtil.findOwner(e.getSource());
    if (w == null) {
      return window;
    }
    return w;
  }

}
