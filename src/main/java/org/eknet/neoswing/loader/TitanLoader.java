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

import java.lang.reflect.Method;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 14.11.12 23:10
 */
public class TitanLoader extends AbstractGraphLoader {

  public static final String NAME = "com.thinkaurelius.titan.core.TitanGraph";

  private static final String titanConfig = "com.thinkaurelius.titan.core.TitanFactory";

  public TitanLoader(ClassLoader classLoader) {
    super(classLoader);
  }

  public TitanLoader() {
  }

  @Override
  public Graph loadGraph(Object... args) {
    try {
      Class factoryClass = classLoader.loadClass(titanConfig);
      Method m = findMatchingMethod(factoryClass, "open", args);
      Object o = m.invoke(null, args);
      return (Graph) o;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getName() {
    return NAME;
  }
}

