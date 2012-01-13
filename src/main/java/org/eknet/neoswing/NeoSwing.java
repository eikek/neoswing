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

package org.eknet.neoswing;

import org.eknet.neoswing.utils.NeoSwingUtil;
import org.eknet.neoswing.utils.WindowUtil;
import org.eknet.neoswing.view.MultiGraphViewer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.io.File;
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
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout(5, 5));
    frame.setIconImage(NeoSwingUtil.getFrameIcon());

    browser = new MultiGraphViewer(componentFactory);
    frame.getContentPane().add(browser, BorderLayout.CENTER);

    frame.setSize(1027, 800);
    WindowUtil.bindToPrefs(frame, prefs, "neoswing.main.%s");
  }

  public void openDatabase(File db) {
    browser.openDatabase(db);
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


  public static void main(String[] args) {
    File db = null;
    if (args != null && args.length > 0) {
      db = new File(args[0]);
      try {
        NeoSwingUtil.checkDatabaseDirectory(db);
      } catch (IllegalArgumentException e) {
        System.out.println("Error: The specified argument is not a neo4j database directory!");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  [directory]   A directory that is either empty or denotes a valid");
        System.out.println("                neo4j database directory.");
        System.out.println("                If an empty directory is specified, a new database");
        System.out.println("                is created.");
        System.out.println("                If nothing is given, a directory can be selected via");
        System.out.println("                the Gui.");
        System.out.println();
        System.exit(1);
      }
    }
    NeoSwing neoSwing = new NeoSwing(NeoSwingUtil.getFactory(true));
    neoSwing.show();
    if (db != null) {
      try {
        neoSwing.openDatabase(db);
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }
  }
}
