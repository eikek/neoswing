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

package org.eknet.neoswing.actions;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.Function;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 16.11.12 11:57
 */
public class ExportAction extends AbstractSwingAction {
  private static final Logger log = LoggerFactory.getLogger(ExportAction.class);
  private final GraphModel model;

  private boolean exportAll;

  public ExportAction(GraphModel model, boolean exportAll) {
    this.model = model;
    this.exportAll = exportAll;
    updateNames();
  }

  private void updateNames() {
    if (isExportAll()) {
      putValue(NAME, "Export complete graph");
      putValue(SHORT_DESCRIPTION, "Exports the complete graph in a GraphML file");
      putValue(SMALL_ICON, NeoSwingUtil.icon("database_save"));
    } else {
      putValue(NAME, "Export the graph view");
      putValue(SHORT_DESCRIPTION, "Exports the current graph view in a GraphML file");
      putValue(SMALL_ICON, NeoSwingUtil.icon("database_table"));
    }
  }

  public boolean isExportAll() {
    return exportAll;
  }

  public void setExportAll(boolean exportAll) {
    this.exportAll = exportAll;
    updateNames();
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    NeoSwingUtil.chooseSingleFile(e, "Export Graph", new Function<File, Object>() {
      @Override
      public Object apply(final File file) {
        model.execute(new DbAction<Object, Object>() {
          @Override
          protected Object doInTx(GraphModel model) throws Exception {
            Graph g = model.getDatabase().getDelegate();
            if (!isExportAll()) {
              g = new TinkerGraph();
              NeoSwingUtil.copyView(model.getGraph(), g);
            }

            GraphMLWriter writer = new GraphMLWriter(g);
            try {
              FileOutputStream fos = new FileOutputStream(file);
              writer.outputGraph(new BufferedOutputStream(fos));
              fos.flush();
              fos.close();
            } catch (IOException e1) {
              log.error("Unable to export Graph", e1);
              throw e1;
            }
            return null;
          }

          @Override
          protected void done() {
            safeGet();
          }
        });
        return null;
      }
    });
  }
}
