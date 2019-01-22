/*
 * (C) Copyright 2018-2019, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.alg.drawing.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A layout model wrapper which adds support for listeners.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the vertex type
 * @param <N> the number type
 * @param <P> the point type
 * @param <B> the box type 
 */
public class ListenableLayoutModel<V, N extends Number, P extends Point<N>, B extends Box<N>>
    implements
    LayoutModel<V, N, P, B>
{
    protected LayoutModel<V, N, P, B> model;
    protected List<BiConsumer<V, P>> listeners;

    /**
     * Create a new model
     * 
     * @param model the underlying layout model
     */
    public ListenableLayoutModel(LayoutModel<V, N, P, B> model)
    {
        this.model = Objects.requireNonNull(model);
        this.listeners = new ArrayList<>();
    }

    @Override
    public B getDrawableArea()
    {
        return model.getDrawableArea();
    }

    @Override
    public void setDrawableArea(B drawableArea)
    {
        model.setDrawableArea(drawableArea);
    }

    @Override
    public Function<V, P> getInitializer()
    {
        return model.getInitializer();
    }

    @Override
    public Iterator<Entry<V, P>> iterator()
    {
        return model.iterator();
    }

    @Override
    public P get(V vertex)
    {
        return model.get(vertex);
    }

    @Override
    public P put(V vertex, P point)
    {
        if (!model.isFixed(vertex)) {
            P oldValue = model.put(vertex, point);
            notifyListeners(vertex, point);
            return oldValue;
        } else {
            return model.get(vertex);
        }
    }

    @Override
    public void setFixed(V vertex, boolean fixed)
    {
        model.setFixed(vertex, fixed);
    }

    @Override
    public boolean isFixed(V vertex)
    {
        return model.isFixed(vertex);
    }

    /**
     * Add a new listener.
     * 
     * @param listener the listener to add
     * @return the newly added listener
     */
    public BiConsumer<V, P> addListener(BiConsumer<V, P> listener)
    {
        listeners.add(listener);
        return listener;
    }

    /**
     * Remove a listener.
     * 
     * @param listener the listener to remove
     * @return true if the listener was removed, false otherwise
     */
    public boolean removeListener(BiConsumer<V, P> listener)
    {
        return listeners.remove(listener);
    }

    /**
     * Notify all registered listeners.
     * 
     * @param vertex the vertex
     * @param point the vertex location
     */
    protected void notifyListeners(V vertex, P point)
    {
        for (BiConsumer<V, P> listener : listeners) {
            listener.accept(vertex, point);
        }
    }

}
