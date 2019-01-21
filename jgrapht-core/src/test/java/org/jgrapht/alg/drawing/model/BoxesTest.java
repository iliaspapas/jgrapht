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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jgrapht.alg.util.Pair;
import org.junit.Test;

/**
 * Test {@link Boxes}.
 * 
 * @author Dimitrios Michail
 */
public class BoxesTest
{

    @Test
    public void testContainsPoint2D()
    {
        DoubleBox2D region = DoubleBox2D.of(0, 0, 10, 10);
        
        assertTrue(Boxes.containsPoint(region, DoublePoint2D.of(5, 5)));
        assertTrue(Boxes.containsPoint(region, DoublePoint2D.of(10, 5)));
        assertTrue(Boxes.containsPoint(region, DoublePoint2D.of(10, 10)));
        assertTrue(Boxes.containsPoint(region, DoublePoint2D.of(0, 0)));
        assertTrue(Boxes.containsPoint(region, DoublePoint2D.of(0, 10)));
        assertTrue(Boxes.containsPoint(region, DoublePoint2D.of(10, 0)));
        assertFalse(Boxes.containsPoint(region, DoublePoint2D.of(11, 0)));
        assertFalse(Boxes.containsPoint(region, DoublePoint2D.of(0, 11)));
    }
    
    @Test
    public void testContainsPoint3D()
    {
        DoubleBox3D region = DoubleBox3D.of(0, 0, 0, 10, 10, 10);
        
        assertTrue(Boxes.containsPoint(region, DoublePoint3D.of(5, 5, 5)));
        assertTrue(Boxes.containsPoint(region, DoublePoint3D.of(10, 5, 5)));
        assertTrue(Boxes.containsPoint(region, DoublePoint3D.of(10, 10, 10)));
        assertTrue(Boxes.containsPoint(region, DoublePoint3D.of(0, 0, 0)));
        assertTrue(Boxes.containsPoint(region, DoublePoint3D.of(0, 10, 10)));
        assertTrue(Boxes.containsPoint(region, DoublePoint3D.of(10, 0, 0)));
        assertFalse(Boxes.containsPoint(region, DoublePoint3D.of(11, 0, 0)));
        assertFalse(Boxes.containsPoint(region, DoublePoint3D.of(0, 11, 0)));
        assertFalse(Boxes.containsPoint(region, DoublePoint3D.of(0, 0, 11)));
        assertFalse(Boxes.containsPoint(region, DoublePoint3D.of(-1, 0, 0)));
        assertFalse(Boxes.containsPoint(region, DoublePoint3D.of(0, -1, 0)));
        assertFalse(Boxes.containsPoint(region, DoublePoint3D.of(0, 0, -1)));
    }
    
    @Test
    public void testSplitAlongXAxis()
    {
        DoubleBox2D region = DoubleBox2D.of(5, 5, 10, 10);
        Pair<Box2D<Double>, Box2D<Double>> pair = Boxes.splitAlongXAxis(region);
        Box2D<Double> westRegion = pair.getFirst();
        Box2D<Double> eastRegion = pair.getSecond();
        
        assertEquals(5d, westRegion.getMinX(), 1e-9);
        assertEquals(5d, westRegion.getMinY(), 1e-9);
        assertEquals(10d, westRegion.getHeight(), 1e-9);
        assertEquals(5d, westRegion.getWidth(), 1e-9);
        
        assertEquals(10d, eastRegion.getMinX(), 1e-9);
        assertEquals(5d, eastRegion.getMinY(), 1e-9);
        assertEquals(10d, eastRegion.getHeight(), 1e-9);
        assertEquals(5d, eastRegion.getWidth(), 1e-9);
    }
    
    @Test
    public void testSplitAlongYAxis()
    {
        DoubleBox2D region = DoubleBox2D.of(5, 5, 10, 10);
        Pair<Box2D<Double>, Box2D<Double>> pair = Boxes.splitAlongYAxis(region);
        Box2D<Double> southRegion = pair.getFirst();
        Box2D<Double> northRegion = pair.getSecond();
        
        assertEquals(5d, southRegion.getMinX(), 1e-9);
        assertEquals(5d, southRegion.getMinY(), 1e-9);
        assertEquals(5d, southRegion.getHeight(), 1e-9);
        assertEquals(10d, southRegion.getWidth(), 1e-9);
        
        assertEquals(5d, northRegion.getMinX(), 1e-9);
        assertEquals(10d, northRegion.getMinY(), 1e-9);
        assertEquals(5d, northRegion.getHeight(), 1e-9);
        assertEquals(10d, northRegion.getWidth(), 1e-9);
    }

}