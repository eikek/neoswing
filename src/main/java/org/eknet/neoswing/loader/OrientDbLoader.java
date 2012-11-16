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
 * @since 14.11.12 22:57
 */
public class OrientDbLoader extends AbstractGraphLoader {

  public static final String NAME = "com.orientechnologies.orient.core.db.graph.OGraphDatabase";

  private static final String bpOrient = "com.tinkerpop.blueprints.impls.orient.OrientGraph";

  public OrientDbLoader(ClassLoader classLoader) {
    super(classLoader);
  }

  public OrientDbLoader() {
  }

  @Override
  public Graph loadGraph(Object... args) {
    try {
      Class<? extends Graph> graphClass = (Class<? extends Graph>) classLoader.loadClass(bpOrient);
      Object first = args[0];
      if (first instanceof String) {
        first = "local:" + first;
        args[0] = first;
      }

      return findMatchingCtor(graphClass, args).newInstance(args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isSupported() {
    return isClassAvailable(NAME) && isClassAvailable(bpOrient);
  }
}
