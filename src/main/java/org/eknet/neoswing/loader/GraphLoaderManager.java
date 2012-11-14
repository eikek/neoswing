package org.eknet.neoswing.loader;

import com.tinkerpop.blueprints.Graph;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 14.11.12 23:44
 */
public interface GraphLoaderManager {
  void registerLoader(GraphLoader loader);

  void removeLoader(GraphLoader loader);

  Iterable<String> getRegisteredLoaders();

  Graph loadGraph(String type, Object... args);
}
