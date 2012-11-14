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

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import org.eknet.neoswing.GraphModel;

import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 10.01.12 20:55
 */
public class HideElementAction extends AbstractSwingAction {

  private final Element element;
  private final GraphModel graphModel;
  
  public HideElementAction(GraphModel graphModel, Element element) {
    this.element = element;
    this.graphModel = graphModel;
    
    putValue(NAME, "Hide Element");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (element instanceof Vertex) {
      graphModel.getGraph().removeVertex((Vertex) element);
    }
    if (element instanceof Edge) {
      graphModel.getGraph().removeEdge((Edge) element);
    }
  }
}
