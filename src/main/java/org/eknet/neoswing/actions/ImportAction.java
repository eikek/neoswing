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

import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.Function;
import org.eknet.neoswing.utils.NeoSwingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 16.11.12 12:27
 */
public class ImportAction extends AbstractSwingAction {
  private static final Logger log = LoggerFactory.getLogger(ImportAction.class);

  private final GraphModel model;

  public ImportAction(GraphModel model) {
    this.model = model;
    putValue(NAME, "Import GraphML file");
    putValue(SHORT_DESCRIPTION, "Imports a graph from a GraphML xml file into this database.");
    putValue(SMALL_ICON, NeoSwingUtil.icon("database_go"));
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    NeoSwingUtil.chooseSingleFile(e, "Import Graph", new Function<File, Object>() {
      @Override
      public Object apply(final File file) {
        model.execute(new DbAction<Object, Object>() {
          @Override
          protected Object doInTx(GraphModel model) throws Exception {
            GraphMLReader reader = new GraphMLReader(model.getDatabase().getDelegate());
            reader.setVertexIdKey("id");
            reader.setEdgeIdKey("id");
            reader.setEdgeLabelKey("label");
            try {
              InputStream fin = new BufferedInputStream(new FileInputStream(file));
              reader.inputGraph(fin);
            } catch (IOException e1) {
              log.error("Error importing graph from file '" + file + "!", e1);
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
