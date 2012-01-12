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

import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.Window;
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
