/*
 * Copyright (c) 2012 Eike Kettner
 *
 * This file is part of NeoSwing.
 *
 * NeoSwing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NeoSwing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NeoSwing.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eknet.neoswing;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.DAGLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;

/**
 * @author <a href="mailto:eike.kettner@gmail.com">Eike Kettner</a>
 * @since 14.05.11 13:11
 */
public enum Layouts implements LayoutFactory {

  SpringLayout {
    @Override
    public <V,E> AbstractLayout<V, E> createLayout(Graph<V, E> graoh) {
      return new SpringLayout2<V, E>(graoh);
    }
  },

  FRLayout {
    @Override
    public <V,E> AbstractLayout<V, E> createLayout(Graph<V, E> graph) {
      return new FRLayout2<V, E>(graph);
    }
  },

  CircleLayout {
    @Override
    public <V,E> AbstractLayout<V, E> createLayout(Graph<V, E> graph) {
      return new CircleLayout<V, E>(graph);
    }
  },

  DAGLayout {
    @Override
    public <V, E> AbstractLayout<V, E> createLayout(Graph<V, E> graph) {
      return new DAGLayout<V, E>(graph);
    }
  },

  ISOMLayout {
    @Override
    public <V, E> AbstractLayout<V, E> createLayout(Graph<V, E> graph) {
      return new ISOMLayout<V, E>(graph);
    }
  },

  KKLayout {
    @Override
    public <V, E> AbstractLayout<V, E> createLayout(Graph<V, E> graph) {
      return new KKLayout<V, E>(graph);
    }
  },

  RandomLayout {
    @Override
    public <V, E> AbstractLayout<V, E> createLayout(Graph<V, E> graph) {
      return new StaticLayout<V, E>(graph);
    }
  }

}
