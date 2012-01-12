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

import org.eknet.neoswing.view.GraphPanel;
import org.jetbrains.annotations.NotNull;
import org.neo4j.graphdb.GraphDatabaseService;

import javax.swing.JFrame;
import javax.swing.UIManager;
import java.awt.BorderLayout;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 20:07
 */
public final class QuickView {

  static {
    UIManager.put("swing.boldMetal", false);
  }

  /**
   * Fires up a frame showing the graph from the specified
   * datbase.
   *
   * @param db
   */
  public static void show(@NotNull GraphDatabaseService db) {
    GraphPanel gp = new GraphPanel(db);
    JFrame frame = new JFrame("NeoSwing");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setIconImage(NeoSwingUtil.getFrameIcon());
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(gp, BorderLayout.CENTER);
    frame.setSize(800, 800);
    frame.setVisible(true);
  }

  /**
   * Fires up  a modal dialog showing the graph from the
   * specified database.
   * 
   * @param db
   */
  public static void showModal(@NotNull GraphDatabaseService db) {
    GraphPanel gp = new GraphPanel(db);
    Dialog dialog = new Dialog("NeoSwing");
    dialog.setContent(gp);
    dialog.setIcon(NeoSwingUtil.getFrameIcon());
    dialog.setShowCancelOption(false);
    dialog.show();
  }
}
