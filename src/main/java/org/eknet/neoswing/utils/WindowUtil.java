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

import javax.swing.JSplitPane;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 12.01.12 15:44
 */
public final class WindowUtil {

  private WindowUtil() {}

  private static final DefaultWindowPlacementManager manager = new DefaultWindowPlacementManager();
  
  public static void bindToPrefs(Window window, Preferences prefs, String keyFormat) {
    manager.bindToPrefs(window, prefs, keyFormat);
  }

  public static void bindDividerLocationToPrefs(final JSplitPane splitPane, final Preferences prefs, final String keyFormat) {
    splitPane.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("dividerLocation")) {
          prefs.putInt(String.format(keyFormat, "dividerLocation"), splitPane.getDividerLocation());
        }
      }
    });
    splitPane.setDividerLocation(prefs.getInt("dividerLocation", 350));
  }
}
