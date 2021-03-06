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

import com.tinkerpop.blueprints.Vertex;
import org.eknet.neoswing.DbAction;
import org.eknet.neoswing.GraphModel;
import org.eknet.neoswing.utils.NeoSwingUtil;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 21:14
 */
public class AddNodeAction extends AbstractSwingAction {

  private final GraphModel graphModel;

  public AddNodeAction(GraphModel graphModel) {
    this.graphModel = graphModel;

    putValue(NAME, "New Node");
    putValue(SHORT_DESCRIPTION, "Create a new node");
    putValue(SMALL_ICON, NeoSwingUtil.icon("pill_add"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    graphModel.execute(new DbAction<Object, Object>() {
      @Override
      protected Object doInTx(GraphModel model) {
        Vertex node = model.getDatabase().createNode();
        graphModel.getGraph().addVertex(node);
        return null;
      }

      @Override
      protected void done() {
        getModel().getViewer().repaint();
      }
    });
  }
}
