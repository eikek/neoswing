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

package org.eknet.neoswing.utils;

import com.tinkerpop.blueprints.CloseableIterable;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Index;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 14.11.12 23:53
 */
public class EmptyIndex<T extends Element> implements Index<T> {

  private final String name;
  private final Class<T> type;

  public EmptyIndex(String name, Class<T> type) {
    this.name = name;
    this.type = type;
  }

  @Override
  public String getIndexName() {
    return name;
  }

  @Override
  public Class<T> getIndexClass() {
    return type;
  }

  @Override
  public void put(String key, Object value, T element) {
  }

  @Override
  public CloseableIterable<T> get(String key, Object value) {
    return new CloseIter<T>();
  }

  @Override
  public CloseableIterable<T> query(String key, Object query) {
    return new CloseIter<T>();
  }

  @Override
  public long count(String key, Object value) {
    return 0;
  }

  @Override
  public void remove(String key, Object value, T element) {
  }

  static class CloseIter<T> implements CloseableIterable<T> {
    @Override
    public void close() {
    }

    @Override
    public Iterator<T> iterator() {
      return Collections.<T>emptyList().iterator();
    }
  }
}
