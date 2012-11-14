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

package org.eknet.neoswing.utils;

import javax.swing.*;
import java.awt.*;
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

