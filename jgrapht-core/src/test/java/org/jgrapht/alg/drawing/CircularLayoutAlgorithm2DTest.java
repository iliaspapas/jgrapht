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
package org.jgrapht.alg.drawing;

import static org.junit.Assert.assertEquals;

import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.model.DoublePoint2D;
import org.jgrapht.alg.drawing.model.DoubleRectangle2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;
import org.junit.Test;

/**
 * Test {@link CircularLayoutAlgorithm2D}.
 * 
 * @author Dimitrios Michail
 */
public class CircularLayoutAlgorithm2DTest
{

    @Test
    public void testSimple()
    {
        Graph<String,
            DefaultEdge> graph = GraphTypeBuilder
                .undirected().vertexSupplier(SupplierUtil.createStringSupplier())
                .edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();

        String v1 = graph.addVertex();
        String v2 = graph.addVertex();
        String v3 = graph.addVertex();
        String v4 = graph.addVertex();

        CircularLayoutAlgorithm2D<String, DefaultEdge> alg = new CircularLayoutAlgorithm2D<>(1d);
        MapLayoutModel2D<String, Double> model =
            new MapLayoutModel2D<>(DoubleRectangle2D.of(0d, 0d, 2d, 2d));

        alg.layout(graph, model);

        assertEquals(DoublePoint2D.of(2d, 1d), model.get(v1));
        assertEquals(DoublePoint2D.of(1d, 2d), model.get(v2));
        assertEquals(DoublePoint2D.of(0d, 1d), model.get(v3));
        assertEquals(DoublePoint2D.of(1d, 0d), model.get(v4));
    }

    @Test
    public void testWithOrder()
    {
        Graph<String,
            DefaultEdge> graph = GraphTypeBuilder
                .undirected().vertexSupplier(SupplierUtil.createStringSupplier())
                .edgeSupplier(SupplierUtil.createDefaultEdgeSupplier()).buildGraph();

        String v1 = "4";
        graph.addVertex(v1);
        String v2 = "3";
        graph.addVertex(v2);
        String v3 = "2";
        graph.addVertex(v3);
        String v4 = "1";
        graph.addVertex(v4);

        CircularLayoutAlgorithm2D<String, DefaultEdge> alg =
            new CircularLayoutAlgorithm2D<>(1d, (a, b) -> a.compareTo(b));
        MapLayoutModel2D<String, Double> model =
            new MapLayoutModel2D<>(DoubleRectangle2D.of(0d, 0d, 2d, 2d));

        alg.layout(graph, model);

        assertEquals(DoublePoint2D.of(2d, 1d), model.get(v4));
        assertEquals(DoublePoint2D.of(1d, 2d), model.get(v3));
        assertEquals(DoublePoint2D.of(0d, 1d), model.get(v2));
        assertEquals(DoublePoint2D.of(1d, 0d), model.get(v1));
    }

}
