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

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 14.11.12 22:37
 */
public class Neo4JEmbeddedLoader implements GraphLoader {

  public static final String NAME = "org.neo4j.kernel.EmbeddedGraphDatabase";

  private static final String neoGraphDb = "org.neo4j.graphdb.GraphDatabaseService";
  private static final String bpName = "com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph";

  private final ClassLoader classLoader;

  public Neo4JEmbeddedLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public Neo4JEmbeddedLoader() {
    this.classLoader = Thread.currentThread().getContextClassLoader();
  }

  public Graph loadGraph(Object... args) {
    try {
      checkDirectory(new File((String) args[0]));
      Class neodbClass = classLoader.loadClass(NAME);
      Object neodb = findConstructor(neodbClass, args).newInstance(args);

      Class<?> arg = classLoader.loadClass(neoGraphDb);
      Class<? extends Graph> graphClass = (Class<? extends Graph>) classLoader.loadClass(bpName);
      Graph g = graphClass.getConstructor(arg).newInstance(neodb);
      return g;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Constructor findConstructor(Class clazz, Object...args) {
    for (Constructor ctor : clazz.getConstructors()) {
      if (ctor.getParameterTypes().length == args.length) {
        return ctor;
      }
    }
    throw new IllegalStateException("Cannot find constructor for arguments: " + Arrays.toString(args));
  }

  @Override
  public String getName() {
    return NAME;
  }

  private void checkDirectory(File db) {
    if (!db.isDirectory()) {
      throw new IllegalArgumentException("'" + db + "' is not a directory");
    }
    File[] content = db.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.isFile();
      }
    });
    if (content.length > 0) {
      content = db.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
          return name.startsWith("neostore");
        }
      });
      if (content.length == 0) {
        throw new IllegalArgumentException("'" + db + "' doesn't seem to be a neo4j database.");
      }
    }
  }
}
