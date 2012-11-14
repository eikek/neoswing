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
