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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for class {@link FilteredSet}.
 * 
 * @author Dimitrios Michail
 */
public class FilteredSetTest
{

    @Test
    public void testFilteredSet()
    {
        Set<Integer> onlyEven = new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> t.intValue() % 2 == 0);
        assertFalse(onlyEven.contains(Integer.valueOf(1)));
        assertTrue(onlyEven.contains(Integer.valueOf(2)));
        assertFalse(onlyEven.contains(Integer.valueOf(3)));
        assertTrue(onlyEven.contains(Integer.valueOf(4)));
        assertFalse(onlyEven.contains(Integer.valueOf(5)));
        assertTrue(onlyEven.contains(Integer.valueOf(6)));
        assertFalse(onlyEven.contains(Integer.valueOf(7)));
        assertTrue(onlyEven.contains(Integer.valueOf(8)));
        assertFalse(onlyEven.isEmpty());
        assertTrue(onlyEven.containsAll(Arrays.asList(2, 4, 6, 8)));
        assertFalse(onlyEven.containsAll(Arrays.asList(2, 4, 3, 6, 8)));
        Object[] a = onlyEven.toArray();
        for (int i = 0; i < a.length; i++) {
            Integer v = (Integer) a[i];
            assertTrue(v.intValue() == (i + 1) * 2);
        }
        assertEquals(4, onlyEven.size());
    }

    @Test
    public void testEmptySet()
    {
        Set<Integer> empty = new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> false);
        assertFalse(empty.contains(Integer.valueOf(1)));
        assertFalse(empty.contains(Integer.valueOf(2)));
        assertFalse(empty.contains(Integer.valueOf(3)));
        assertFalse(empty.contains(Integer.valueOf(4)));
        assertFalse(empty.contains(Integer.valueOf(5)));
        assertFalse(empty.contains(Integer.valueOf(6)));
        assertFalse(empty.contains(Integer.valueOf(7)));
        assertFalse(empty.contains(Integer.valueOf(8)));
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.size());
    }

    @Test
    public void testIterator()
    {
        Set<Integer> onlyEven = new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> t.intValue() % 2 == 0);
        Iterator<Integer> it = onlyEven.iterator();
        assertTrue(it.hasNext());
        assertEquals(2, it.next().intValue());
        assertTrue(it.hasNext());
        assertEquals(4, it.next().intValue());
        assertTrue(it.hasNext());
        assertEquals(6, it.next().intValue());
        assertTrue(it.hasNext());
        assertEquals(8, it.next().intValue());
        assertFalse(it.hasNext());
    }

    @Test
    public void testEmptyIterator()
    {
        Set<Integer> empty = new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> false);
        Iterator<Integer> it = empty.iterator();
        assertFalse(it.hasNext());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test2()
    {
        new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> t.intValue() % 2 == 0)
                .add(10);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test3()
    {
        new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> t.intValue() % 2 == 0)
                .addAll(Arrays.asList(10, 11));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test4()
    {
        new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> t.intValue() % 2 == 0)
                .clear();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test5()
    {
        new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> t.intValue() % 2 == 0)
                .remove(2);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test6()
    {
        new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> t.intValue() % 2 == 0)
                .removeAll(Arrays.asList(2, 4));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test7()
    {
        new FilteredSet<>(
            new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)), t -> t.intValue() % 2 == 0)
                .retainAll(Arrays.asList(2, 4));
    }

}
