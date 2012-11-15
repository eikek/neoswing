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

package org.eknet.neoswing;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;

/**
 * Encodes the element type and its id used for lookups. The vertex and
 * edge objects can/should not be shared between transactions. If some
 * elements are to keep in memory, than this class can be used.
 *
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 15.11.12 23:03
 */
public final class ElementId<T extends Element> {

  private final Class<T> elementClass;
  private final Object id;

  private ElementId(Object id, Class<T> elementClass) {
    this.id = id;
    this.elementClass = elementClass;
  }

  public static ElementId<Vertex> forVertex(Object id) {
    return new ElementId<Vertex>(id, Vertex.class);
  }

  public static ElementId<Edge> forEdge(Object id) {
    return new ElementId<Edge>(id, Edge.class);
  }

  public static ElementId<Vertex> vertexId(Vertex v) {
    return new ElementId<Vertex>(v.getId(), Vertex.class);
  }

  public static ElementId<Edge> edgeId(Edge e) {
    return new ElementId<Edge>(e.getId(), Edge.class);
  }

  public Object getId() {
    return id;
  }

  public boolean isVertex() {
    return elementClass == Vertex.class;
  }

  public boolean isEdge() {
    return elementClass == Edge.class;
  }

  public Class<T> getElementClass() {
    return elementClass;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ElementId elementId = (ElementId) o;

    if (elementClass != null ? !elementClass.equals(elementId.elementClass) : elementId.elementClass != null)
      return false;
    if (id != null ? !id.equals(elementId.id) : elementId.id != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = elementClass != null ? elementClass.hashCode() : 0;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "ElementId{" +
        "elementClass=" + elementClass +
        ", id=" + id +
        '}';
  }
}
