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

package org.eknet.neoswing;

import org.eknet.neoswing.loader.DefaultGraphLoaderManager;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.WindowUtil;
import org.eknet.neoswing.view.MultiGraphViewer;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 18:19
 */
public class NeoSwing {

  protected JFrame frame;
  private final ComponentFactory componentFactory;

  private final static Preferences prefs = Preferences.userNodeForPackage(NeoSwing.class);

  private MultiGraphViewer browser;

  static {
    UIManager.put("swing.boldMetal", false);
  }

  public NeoSwing(ComponentFactory componentFactory) {
    this.componentFactory = componentFactory;
    this.frame = new JFrame(NeoSwingUtil.getApplicationName() + " - " + NeoSwingUtil.getApplicationVersion());
  }

  /**
   * Executed on startup.
   * 
   */
  protected void initComponents() {
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout(5, 5));
    frame.setIconImage(NeoSwingUtil.getFrameIcon());

    browser = new MultiGraphViewer(componentFactory, DefaultGraphLoaderManager.getInstance());
    frame.getContentPane().add(browser, BorderLayout.CENTER);

    frame.setSize(1027, 800);
    WindowUtil.bindToPrefs(frame, prefs, "neoswing.main.%s");
  }

  /**
   * Executed on startup after {@link #initComponents()}. It is executed
   * on the EDT. This method must set the frame visible.
   *
   */
  protected void onInitialize() {
    frame.setVisible(true);
  }
  
  public void show() {
    initComponents();
    try {
      SwingUtilities.invokeAndWait(new Runnable() {
        @Override
        public void run() {
          onInitialize();
        }
      });
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    if (this.frame != null) {
      this.frame.setVisible(false);
      this.browser.close();
      this.frame.dispose();
    }
  }

  public static void main(String[] args) {
    NeoSwing neoSwing = new NeoSwing(NeoSwingUtil.getFactory(true));
    neoSwing.show();
  }
}
