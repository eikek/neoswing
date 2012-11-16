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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 16.11.12 00:26
 */
public abstract class AbstractGraphLoader implements GraphLoader {

  protected final ClassLoader classLoader;

  protected AbstractGraphLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  protected AbstractGraphLoader() {
    this(Thread.currentThread().getContextClassLoader());
  }

  @Override
  public boolean isSupported() {
    return isClassAvailable(getName());
  }

  protected boolean isClassAvailable(String name) {
    try {
      classLoader.loadClass(name);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  protected <T> Constructor<T> findMatchingCtor(Class<T> clazz, Object...args) {
    List<Constructor> ctors = new ArrayList<Constructor>();
    for (Constructor ct : clazz.getConstructors()) {
      if (isCompatible(ct.getParameterTypes(), args)) {
        ctors.add(ct);
      }
    }
    if (ctors.isEmpty()) {
      return null;
    }
    Collections.sort(ctors, new Comparator<Constructor>() {
      @Override
      public int compare(Constructor o1, Constructor o2) {
        int sum = 0;
        for (Class c0 : o1.getParameterTypes()) {
          for (Class c1 : o2.getParameterTypes()) {
            sum += classDist(c0, c1);
          }
        }
        return sum;
      }
    });
    //noinspection unchecked
    return ctors.get(0);
  }

  private boolean isCompatible(Class<?>[] paramTypes, Object...args) {
    if (paramTypes.length == args.length) {
      int sum = 0;
      for (int i = 0; i < paramTypes.length; i++) {
        if (paramTypes[i].isAssignableFrom(args[i].getClass())) {
          sum++;
        }
      }
      return sum == args.length;
    } else {
      return false;
    }
  }

  protected Method findMatchingMethod(Class<?> owner, String name, Object...args) {
    List<Method> methods = new ArrayList<Method>();
    for (Method m : owner.getMethods()) {
      if (m.getName().equals(name)) {
        if (isCompatible(m.getParameterTypes(), args)) {
          methods.add(m);
        }
      }
    }
    if (methods.isEmpty()) {
      return null;
    }
    Collections.sort(methods, new Comparator<Method>() {
      @Override
      public int compare(Method o1, Method o2) {
        int sum = 0;
        for (Class c0 : o1.getParameterTypes()) {
          for (Class c1 : o2.getParameterTypes()) {
            sum += classDist(c0, c1);
          }
        }
        return sum;
      }
    });
    //noinspection unchecked
    return methods.get(0);
  }

  private int classDist(Class c1, Class c2) {
    Class superClass;
    Class subClass;
    if (c1.isAssignableFrom(c2)) {
      superClass = c1;
      subClass = c2;
    }
    else if (c2.isAssignableFrom(c1)) {
      superClass = c2;
      subClass = c1;
    }
    else {
      return 0;
    }
    int i=0;
    Class c = subClass;
    while (c != superClass && c != null) {
      c = c.getSuperclass();
      i++;
    }
    if (superClass == c1) {
      return i;
    } else {
      return -i;
    }
  }

}
