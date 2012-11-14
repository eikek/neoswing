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

import org.eknet.neoswing.GraphDb;
import org.eknet.neoswing.view.GraphViewer;

import javax.swing.*;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 11.01.12 20:07
 */
public final class QuickView {

  static {
    UIManager.put("swing.boldMetal", false);
  }

  /**
   * Fires up  a modal dialog showing the graph of the
   * specified database.
   * 
   * @param db
   */
  public static void show(GraphDb db) {
    GraphViewer gv = new GraphViewer(db);
    Dialog dialog = new Dialog(NeoSwingUtil.getApplicationName()
        + " - " + NeoSwingUtil.getApplicationVersion());
    dialog.setContent(gv);
    dialog.setIcon(NeoSwingUtil.getFrameIcon());
    dialog.setShowCancelOption(false);
    dialog.show();
  }
}
