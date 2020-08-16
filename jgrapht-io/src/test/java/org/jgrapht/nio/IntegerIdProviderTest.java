/*
 * (C) Copyright 2019-2020, by Amr ALHOSSARY and Contributors.
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
package org.jgrapht.nio;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests
 * 
 * @author Amr ALHOSSARY
 */
public class IntegerIdProviderTest
{

    @Test
    public void testDefaultConstructor()
    {
        IntegerIdProvider<Object> provider = new IntegerIdProvider<>();
        String id1 = provider.apply(new Object());
        assertEquals("1", id1);
        String id2 = provider.apply(new Object());
        assertEquals("2", id2);
    }

    @Test
    public void testConstructorWithParameter()
    {
        IntegerIdProvider<Object> provider = new IntegerIdProvider<>(0);
        String id1 = provider.apply(new Object());
        assertEquals("0", id1);
        String id2 = provider.apply(new Object());
        assertEquals("1", id2);
    }

}
