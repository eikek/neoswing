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
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.iterators.FilterIterator;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 14.11.12 22:34
 */
public final class DefaultGraphLoaderManager implements GraphLoaderManager {
  private final static GraphLoaderManager INSTANCE = new DefaultGraphLoaderManager();

  private final ConcurrentMap<String, GraphLoader> loaders = new ConcurrentHashMap<String, GraphLoader>();

  private DefaultGraphLoaderManager() {
    registerLoader(new Neo4JEmbeddedLoader());
    registerLoader(new OrientDbLoader());
    registerLoader(new TitanLoader());
  }

  public static GraphLoaderManager getInstance() {
    return INSTANCE;
  }

  @Override
  public void registerLoader(GraphLoader loader) {
    this.loaders.put(loader.getName(), loader);
  }

  @Override
  public void removeLoader(GraphLoader loader) {
    this.loaders.remove(loader.getName());
  }

  @Override
  public Iterable<String> getRegisteredLoaders() {
    return new Iterable<String>() {
      @Override
      public Iterator<String> iterator() {
        return new FilterIterator<String>(loaders.keySet().iterator(), new Predicate<String>() {
          @Override
          public boolean evaluate(String s) {
            GraphLoader gl = loaders.get(s);
            return gl != null && gl.isSupported();
          }
        });
      }
    };
  }

  @Override
  public Graph loadGraph(String type, Object... args) {
    GraphLoader loader = loaders.get(type);
    if (loader == null) {
      throw new IllegalArgumentException("No loader for type: " + type);
    }
    return loader.loadGraph(args);
  }
}
