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

package org.eknet.neoswing.loader;

import com.tinkerpop.blueprints.Graph;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 14.11.12 22:34
 */
public interface GraphLoader {

  /**
   * Opens the graph at the given location or creates a new one.
   * @param args the arguments used to instantiate the concrete database impl
   * @return
   */
  Graph loadGraph(Object...args);

  /**
   * Returns the concrete database type that this loader is creating.
   * <p/>
   * As a convention, you may want to return the classname of the concrete
   * underlying graph database this loader creates. For example, for Neo4j
   * this could be {@code org.neo4j.kernel.EmbeddedGraphDatabase}. This name
   * is used to lookup loaders.
   *
   * @return
   */
  String getName();
}
