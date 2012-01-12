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

package org.eknet.neoswing.utils;

import javax.swing.JLabel;
import javax.swing.JPanel;

import static java.awt.Dialog.ModalityType.*;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 19:45
 */
public final class Dialogs {

  public static void error(Object component, String message) {
    Dialog dialog = new Dialog("Error");
    dialog.setShowCancelOption(false);
    dialog.setShowOkOption(true);
    JPanel content = new JPanel();
    content.add(new JLabel(message));
    dialog.setContent(content);
    dialog.show(NeoSwingUtil.findOwner(component), MODELESS);
  }

  public static Dialog.Option confirm(Object component, String message) {
    Dialog dialog = new Dialog("Confirm");
    dialog.setShowCancelOption(true);
    dialog.setShowOkOption(true);
    JPanel content = new JPanel();
    content.add(new JLabel(message));
    dialog.setContent(content);
    return dialog.show(NeoSwingUtil.findOwner(component), APPLICATION_MODAL);
  }
}
