/*
 * (C) Copyright 2018-2018, by Dimitrios Michail and Contributors.
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

import org.junit.Test;

/**
 * Test {@link DoublePoint2D}.
 * 
 * @author Dimitrios Michail
 */
public class DoublePoint2DTest
{

    @Test
    public void testDefaultConstructor()
    {
        DoublePoint2D p = new DoublePoint2D();
        assertEquals(p.getX(), 0d, 1e-9);
        assertEquals(p.getY(), 0d, 1e-9);
    }

    @Test
    public void testConstructorAndGetters()
    {
        DoublePoint2D p = new DoublePoint2D(3d, 2d);
        assertEquals(p.getX(), 3d, 1e-9);
        assertEquals(p.getY(), 2d, 1e-9);
    }

}