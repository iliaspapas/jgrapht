/*
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A filtered view of a set.
 * 
 * <p>
 * This set is not modifiable but it reflects the changes in the underlying set. This means that
 * operations {@link #size()}, {@link #containsAll(Collection)} and {@link #isEmpty()} take time
 * proportional to the size of the underlying set.
 * 
 * @param <E> the element type
 * 
 * @author Dimitrios Michail
 * @since October 2016
 */
public class FilteredSet<E>
    extends AbstractSet<E>
    implements Serializable
{
    private static final long serialVersionUID = 6041315311692097898L;

    private final Set<E> set;
    private final Predicate<E> predicate;

    /**
     * Construct a new filtered set.
     * 
     * @param set the underlying set
     * @param predicate the predicate
     */
    public FilteredSet(Set<E> set, Predicate<E> predicate)
    {
        this.set = Objects.requireNonNull(set, "Invalid set provided");
        this.predicate = Objects.requireNonNull(predicate, "Invalid predicate provided");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size()
    {
        int s = 0;
        for (E e : set) {
            if (predicate.test(e)) {
                s++;
            }
        }
        return s;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty()
    {
        if (set.isEmpty()) {
            return true;
        }
        for (E e : set) {
            if (predicate.test(e)) {
                return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o)
    {
        Objects.requireNonNull(o, "Set does not support null elements");
        if (!set.contains(o)) {
            return false;
        }
        E t = TypeUtil.uncheckedCast(o, null);
        return predicate.test(t);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<E> iterator()
    {
        return set.stream().filter(predicate).iterator();
    }

}
