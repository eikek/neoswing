/*
 * Copyright 2010 Raffael Herzog
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

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.prefs.Preferences;


/**
 * Minor modified version of the original found in cru-swing at http://maven.raffael.ch/ch/raffael/util/cru-swing/.
 * Copied code here to reduce dependencies.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a> (original author)
 */
class DefaultWindowPlacementManager {

  private final ConcurrentMap<BindingKey, Binding> prefsBindings = new ConcurrentHashMap<BindingKey, Binding>();

  public static void fitRectangle(Rectangle rect, Rectangle target) {
    if ( rect.width > target.width ) {
      rect.width = target.width;
    }
    if ( rect.height > target.height ) {
      rect.height = target.height;
    }
    if ( rect.x < target.x ) {
      rect.x = target.x;
    }
    else if ( rect.x + rect.width > target.x + target.width ) {
      rect.x = target.x + target.width - rect.width;
    }
    if ( rect.y < target.y ) {
      rect.y = target.y;
    }
    else if ( rect.y + rect.height > target.y + target.height ) {
      rect.y = target.y + target.height - rect.height;
    }
  }

  public void bindToPrefs(Window window) {
    Preferences prefs = Preferences.userNodeForPackage(window.getClass());
    String keyFormat = window.getClass().getName();
    int pos = keyFormat.lastIndexOf('.');
    if ( pos >= 0 ) {
      keyFormat = keyFormat.substring(pos + 1);
    }
    bindToPrefs(window, prefs, keyFormat + "-%s");
  }

  public void bindToPrefs(Window window, Preferences prefs, String keyFormat) {
    BindingKey key = new BindingKey(prefs, keyFormat);
    Binding binding = prefsBindings.get(key);
    if ( binding == null ) {
      binding = new Binding(createPrefsBindingWindowListener(prefs, keyFormat));
      Binding current = prefsBindings.putIfAbsent(key, binding);
      if ( current != null ) {
        binding = current;
      }
    }
    binding.setCurrentComponent(null);
    Rectangle bounds = window.getBounds();
    bounds.x = prefs.getInt(String.format(keyFormat, "x"), bounds.x);
    bounds.y = prefs.getInt(String.format(keyFormat, "y"), bounds.y);
    bounds.width = prefs.getInt(String.format(keyFormat, "width"), bounds.width);
    bounds.height = prefs.getInt(String.format(keyFormat, "height"), bounds.height);
    fitRectangle(bounds, window.getGraphicsConfiguration().getBounds());
    window.setBounds(bounds);
    updatePrefsFromComponent(prefs, window, keyFormat);
    binding.setCurrentComponent(window);
  }

  protected static void updatePrefsFromComponent(Preferences prefs, Component comp, String keyFormat) {
    Rectangle bounds = comp.getBounds();
    prefs.putInt(String.format(keyFormat, "x"), bounds.x);
    prefs.putInt(String.format(keyFormat, "y"), bounds.y);
    prefs.putInt(String.format(keyFormat, "width"), bounds.width);
    prefs.putInt(String.format(keyFormat, "height"), bounds.height);
  }

  protected ComponentListener createPrefsBindingWindowListener(final Preferences prefs, final String keyFormat) {
    return new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        updatePrefsFromComponent(prefs, (Component)e.getSource(), keyFormat);
      }
      @Override
      public void componentMoved(ComponentEvent e) {
        updatePrefsFromComponent(prefs, (Component)e.getSource(), keyFormat);
      }
    };
  }

  private class BindingKey {
    private final Preferences prefs;
    private final String keyFormat;
    private BindingKey(Preferences prefs, String keyFormat) {
      this.prefs = prefs;
      this.keyFormat = keyFormat;
    }
    @Override
    public boolean equals(Object o) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }
      BindingKey that = (BindingKey)o;
      if ( !keyFormat.equals(that.keyFormat) ) {
        return false;
      }
      return prefs.absolutePath().equals(that.prefs.absolutePath());
    }
    @Override
    public int hashCode() {
      int result = prefs.absolutePath().hashCode();
      result = 31 * result + keyFormat.hashCode();
      return result;
    }
  }

  private class Binding implements ComponentListener {
    private final ComponentListener listener;
    private volatile WeakReference<Component> currentComponent = null;
    private Binding(ComponentListener listener) {
      this.listener = listener;
    }
    public Component getCurrentComponent() {
      if ( currentComponent != null ) {
        return currentComponent.get();
      }
      else {
        return null;
      }
    }
    public void setCurrentComponent(Component comp) {
      Component old = getCurrentComponent();
      if ( old != null ) {
        old.removeComponentListener(this);
      }
      if ( comp == null ) {
        currentComponent = null;
      }
      else {
        currentComponent = new WeakReference<Component>(comp);
        comp.addComponentListener(this);
      }
    }
    @Override
    public void componentResized(ComponentEvent e) {
      WeakReference<Component> current = currentComponent;
      if ( current != null && e.getSource() == current.get() ) {
        listener.componentResized(e);
      }
    }
    @Override
    public void componentMoved(ComponentEvent e) {
      WeakReference<Component> current = currentComponent;
      if ( current != null && e.getSource() == current.get() ) {
        listener.componentMoved(e);
      }
    }
    @Override
    public void componentShown(ComponentEvent e) {
      WeakReference<Component> current = currentComponent;
      if ( current != null && e.getSource() == current.get() ) {
        listener.componentShown(e);
      }
    }
    @Override
    public void componentHidden(ComponentEvent e) {
      WeakReference<Component> current = currentComponent;
      if ( current != null && e.getSource() == current.get() ) {
        listener.componentHidden(e);
      }
    }
  }

}
