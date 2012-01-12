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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Convenience mouse listener that shows the popup obtained by {@link #getPopupMenu()}.
 * If {@link #isExecuteDefault()} is {@code true} it will execute the first action of
 * of the popup menu on double click.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 06.11.11 15:58
 */
public abstract class PopupTrigger extends MouseAdapter {

  private boolean executeDefault = false;

  protected PopupTrigger(boolean executeDefault) {
    this.executeDefault = executeDefault;
  }

  protected PopupTrigger() {
  }

  public boolean isExecuteDefault() {
    return executeDefault;
  }

  public void setExecuteDefault(boolean executeDefault) {
    this.executeDefault = executeDefault;
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger()) {
      showPopup(e);
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON3 && e.isPopupTrigger()) {
      showPopup(e);
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getClickCount() == 2 && executeDefault) {
      JPopupMenu popup = getPopupMenu();
      MenuElement[] elements = popup.getSubElements();
      if (elements != null) {
        for (MenuElement el : elements) {
          if (el instanceof JMenuItem) {
            JMenuItem jMenuItem = (JMenuItem) el;
            if (jMenuItem.isEnabled()) {
              NeoSwingUtil.invoke(jMenuItem.getAction(), e.getSource());
            }
            break;
          }
        }
      }
    }
  }

  protected void markFirstActionAsDefault(JPopupMenu popup) {
    MenuElement[] elements = popup.getSubElements();
    if (elements != null) {
      for (MenuElement el : elements) {
        if (el instanceof JMenuItem) {
          JMenuItem jMenuItem = (JMenuItem) el;
          if (jMenuItem.isEnabled()) {
            JMenuItem i = new JMenuItem();
            jMenuItem.setFont(i.getFont().deriveFont(Font.BOLD, i.getFont().getSize()));
            break;
          }
        }
      }
    }
  }

  protected void showPopup(MouseEvent e) {
    JPopupMenu popup = getPopupMenu();
    if (popup != null) {
      if (isExecuteDefault()) {
        markFirstActionAsDefault(popup);
      }
      popup.show((Component) e.getSource(), e.getX(), e.getY());
    }
  }

  protected abstract JPopupMenu getPopupMenu();

}

