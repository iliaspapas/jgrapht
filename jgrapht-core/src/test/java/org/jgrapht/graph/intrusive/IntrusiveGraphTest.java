/*
 * (C) Copyright 2017-2017, by Dimitrios Michail and Contributors.
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
package org.jgrapht.graph.intrusive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

import org.jgrapht.graph.intrusive.IntrusiveEdge;
import org.jgrapht.graph.intrusive.IntrusiveGraph;
import org.jgrapht.graph.intrusive.IntrusiveVertex;
import org.junit.Test;

/**
 * Tests for {@link IntrusiveGraph}
 * 
 * @author Dimitrios Michail
 */
public class IntrusiveGraphTest
{
    private static final boolean DIRECTED = true;
    private static final boolean UNDIRECTED = false;

    @Test
    public void testAddRemoveVertices()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED, false);
        assertEquals(0, g.vertexSet().size());
        IntrusiveVertex v1 = new NamedVertex("v1");
        assertTrue(g.addVertex(v1));
        assertFalse(g.addVertex(v1));
        assertTrue(g.containsVertex(v1));
        assertEquals(1, g.vertexSet().size());
        assertTrue(g.removeVertex(v1));
        assertEquals(0, g.vertexSet().size());
        assertFalse(g.removeVertex(v1));
        assertTrue(g.addVertex(v1));
        IntrusiveVertex v2 = new NamedVertex("v2");
        assertTrue(g.addVertex(v2));
        assertEquals(2, g.vertexSet().size());
        IntrusiveVertex v3 = new NamedVertex("v3");
        assertTrue(g.addVertex(v3));
        assertEquals(3, g.vertexSet().size());

        assertTrue(g.vertexSet().contains(v1));
        assertTrue(g.vertexSet().contains(v2));
        assertTrue(g.vertexSet().contains(v3));
        try {
            g.vertexSet().add(new NamedVertex("v4"));
            fail("No");
        } catch (UnsupportedOperationException e) {
        }

        Iterator<IntrusiveVertex> it = g.vertexSet().iterator();
        assertEquals(v1, it.next());
        assertEquals(v2, it.next());
        assertEquals(v3, it.next());
        assertFalse(it.hasNext());

        assertTrue(g.removeVertex(v1));
        assertTrue(g.removeVertex(v2));
        assertTrue(g.removeVertex(v3));
        assertFalse(g.vertexSet().contains(v1));
        assertFalse(g.vertexSet().contains(v2));
        assertFalse(g.vertexSet().contains(v3));
        assertEquals(0, g.vertexSet().size());
    }

    @Test
    public void testContainsVertex()
    {
        IntrusiveGraph g1 = new IntrusiveGraph(NamedEdge.class, DIRECTED, false);
        IntrusiveGraph g2 = new IntrusiveGraph(NamedEdge.class, DIRECTED, false);

        IntrusiveVertex v1 = new NamedVertex("v1");
        assertTrue(g1.addVertex(v1));
        assertTrue(g1.containsVertex(v1));

        IntrusiveVertex v2 = new NamedVertex("v2");
        assertTrue(g2.addVertex(v2));
        assertTrue(g2.containsVertex(v2));

        assertFalse(g1.containsVertex(v2));
        assertFalse(g2.containsVertex(v1));

        assertFalse(g1.containsVertex(null));
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testVertexIteratorConcurrentModification()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED, false);
        assertTrue(g.addVertex(new NamedVertex("v1")));
        assertTrue(g.addVertex(new NamedVertex("v2")));
        assertTrue(g.addVertex(new NamedVertex("v3")));

        Iterator<IntrusiveVertex> it = g.vertexSet().iterator();
        it.next();
        assertTrue(g.addVertex(new NamedVertex("v4")));
        it.next();
    }

    @Test
    public void testEdgeFactory()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED);
        assertTrue(g.getType().isDirected());
        assertTrue(g.getEdgeFactory() != null);

        IntrusiveVertex v1 = new NamedVertex("v1");
        g.addVertex(v1);
        IntrusiveVertex v2 = new NamedVertex("v2");
        g.addVertex(v2);

        IntrusiveEdge e = g.getEdgeFactory().createEdge(v1, v2);

        assert (e instanceof NamedEdge);
    }

    @Test
    public void testEdgeSourceAndEdgeTarget()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED, false);

        IntrusiveVertex v1 = new NamedVertex("v1");
        IntrusiveVertex v2 = new NamedVertex("v2");
        g.addVertex(v1);
        g.addVertex(v2);

        IntrusiveEdge e1 = g.addEdge(v1, v2);

        assertTrue(v1 == g.getEdgeSource(e1));
        assertTrue(v2 == g.getEdgeTarget(e1));

        IntrusiveEdge e2 = new IntrusiveEdge();
        g.addEdge(v1, v2, e2);
        assertTrue(v1 == g.getEdgeSource(e1));
        assertTrue(v2 == g.getEdgeTarget(e1));
    }

    @Test
    public void testDirectedOneVertex()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED);

        IntrusiveVertex v1 = new NamedVertex("v1");
        g.addVertex(v1);
        g.addEdge(v1, v1);

        assertEquals(1, g.outDegreeOf(v1));
        assertEquals(1, g.inDegreeOf(v1));
        assertEquals(2, g.degreeOf(v1));

        assertEquals(1, g.outgoingEdgesOf(v1).size());
        assertEquals(1, g.incomingEdgesOf(v1).size());
        assertEquals(1, g.edgesOf(v1).size());
    }

    @Test
    public void testUndirectedOneVertex()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, UNDIRECTED);

        IntrusiveVertex v1 = new NamedVertex("v1");
        g.addVertex(v1);
        g.addEdge(v1, v1);

        assertEquals(2, g.outDegreeOf(v1));
        assertEquals(2, g.inDegreeOf(v1));
        assertEquals(2, g.degreeOf(v1));

        assertEquals(1, g.outgoingEdgesOf(v1).size());
        assertEquals(1, g.incomingEdgesOf(v1).size());
        assertEquals(1, g.edgesOf(v1).size());
    }

    @Test
    public void testWeightedEdgeUndirected()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, UNDIRECTED);

        IntrusiveVertex v1 = new NamedVertex("v1");
        g.addVertex(v1);
        IntrusiveEdge e = g.addEdge(v1, v1);
        assertEquals(1d, g.getEdgeWeight(e), 1e-9);
        g.setEdgeWeight(e, 5d);
        assertEquals(5d, g.getEdgeWeight(e), 1e-9);
    }

    public void testWeightedEdgeFromOtherGraph()
    {
        IntrusiveGraph g1 = new IntrusiveGraph(NamedEdge.class, UNDIRECTED);

        IntrusiveVertex v1 = new NamedVertex("v1");
        g1.addVertex(v1);
        IntrusiveEdge e1 = g1.addEdge(v1, v1);
        g1.setEdgeWeight(e1, 5d);

        IntrusiveGraph g2 = new IntrusiveGraph(NamedEdge.class, UNDIRECTED);
        assertEquals(5d, g2.getEdgeWeight(e1), 1e-9);
    }

    @Test
    public void testWeightedEdgeDirected()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED);
        IntrusiveVertex v1 = new NamedVertex("v1");
        g.addVertex(v1);
        IntrusiveEdge e = g.addEdge(v1, v1);
        assertEquals(1d, g.getEdgeWeight(e), 1e-9);
        g.setEdgeWeight(e, 5d);
        assertEquals(5d, g.getEdgeWeight(e), 1e-9);
    }

    @Test
    public void testDirectedLoops()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED);
        IntrusiveVertex v1 = new IntrusiveVertex();
        IntrusiveVertex v2 = new IntrusiveVertex();
        IntrusiveVertex v3 = new IntrusiveVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        IntrusiveEdge e1 = g.addEdge(v1, v1);
        IntrusiveEdge e2 = g.addEdge(v1, v2);
        IntrusiveEdge e3 = g.addEdge(v1, v1);
        IntrusiveEdge e4 = g.addEdge(v3, v1);
        IntrusiveEdge e5 = g.addEdge(v1, v1);
        IntrusiveEdge e6 = g.addEdge(v1, v3);
        IntrusiveEdge e7 = g.addEdge(v1, v1);
        IntrusiveEdge e8 = g.addEdge(v2, v1);
        IntrusiveEdge e9 = g.addEdge(v1, v1);
        IntrusiveEdge e10 = g.addEdge(v2, v1);

        assertEquals(8, g.inDegreeOf(v1));
        assertEquals(7, g.outDegreeOf(v1));

        assertEquals(7, g.outgoingEdgesOf(v1).size());
        assertTrue(
            g.outgoingEdgesOf(v1).containsAll(
                new HashSet<>(Arrays.asList(e1, e2, e3, e5, e6, e7, e9))));
        assertEquals(7, g.outgoingEdgesOf(v1).stream().collect(Collectors.toList()).size());
        assertEquals(7, g.outgoingEdgesOf(v1).stream().collect(Collectors.toSet()).size());

        assertEquals(8, g.incomingEdgesOf(v1).size());
        assertTrue(
            g.incomingEdgesOf(v1).containsAll(
                new HashSet<>(Arrays.asList(e1, e3, e4, e5, e7, e8, e9, e10))));
        assertEquals(8, g.incomingEdgesOf(v1).stream().collect(Collectors.toList()).size());
        assertEquals(8, g.incomingEdgesOf(v1).stream().collect(Collectors.toSet()).size());

        assertEquals(10, g.edgesOf(v1).size());
        assertEquals(10, g.edgesOf(v1).stream().collect(Collectors.toList()).size());
        assertEquals(10, g.edgesOf(v1).stream().collect(Collectors.toSet()).size());
    }

    @Test
    public void testUndirectedLoops()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, UNDIRECTED);
        IntrusiveVertex v1 = new IntrusiveVertex();
        IntrusiveVertex v2 = new IntrusiveVertex();
        IntrusiveVertex v3 = new IntrusiveVertex();
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        IntrusiveEdge e1 = g.addEdge(v1, v1);
        IntrusiveEdge e2 = g.addEdge(v1, v2);
        IntrusiveEdge e3 = g.addEdge(v1, v1);
        IntrusiveEdge e4 = g.addEdge(v3, v1);
        IntrusiveEdge e5 = g.addEdge(v1, v1);
        IntrusiveEdge e6 = g.addEdge(v1, v3);
        IntrusiveEdge e7 = g.addEdge(v1, v1);
        IntrusiveEdge e8 = g.addEdge(v2, v1);
        IntrusiveEdge e9 = g.addEdge(v1, v1);
        IntrusiveEdge e10 = g.addEdge(v2, v1);

        assertEquals(15, g.degreeOf(v1));
        assertEquals(10, g.edgesOf(v1).size());
        assertTrue(
            g.edgesOf(v1).containsAll(
                new HashSet<>(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10))));

        assertEquals(10, g.edgesOf(v1).stream().collect(Collectors.toList()).size());
        assertEquals(10, g.edgesOf(v1).stream().collect(Collectors.toSet()).size());
    }

    @Test
    public void testDirectedDirectGraph()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED);
        IntrusiveVertex[] v = new IntrusiveVertex[16];
        for (int i = 0; i < 16; i++) {
            v[i] = new NamedVertex("v" + i);
            g.addVertex(v[i]);
        }
        IntrusiveEdge e2_5 = g.addEdge(v[2], v[5]);
        IntrusiveEdge e2_6 = g.addEdge(v[2], v[6]);
        IntrusiveEdge e2_7 = g.addEdge(v[2], v[7]);
        IntrusiveEdge e3_7 = g.addEdge(v[3], v[7]);
        IntrusiveEdge e4_7 = g.addEdge(v[4], v[7]);
        IntrusiveEdge e8_5 = g.addEdge(v[8], v[5]);
        IntrusiveEdge e9_5 = g.addEdge(v[9], v[5]);
        IntrusiveEdge e7_10 = g.addEdge(v[7], v[10]);
        IntrusiveEdge e7_11 = g.addEdge(v[7], v[11]);
        IntrusiveEdge e12_13 = g.addEdge(v[12], v[13]);
        IntrusiveEdge e12_13_1 = g.addEdge(v[12], v[13]);
        IntrusiveEdge e12_13_2 = g.addEdge(v[12], v[13]);
        IntrusiveEdge e13_12 = g.addEdge(v[13], v[12]);
        IntrusiveEdge e13_12_1 = g.addEdge(v[13], v[12]);
        IntrusiveEdge e13_13 = g.addEdge(v[13], v[13]);
        IntrusiveEdge e13_13_1 = g.addEdge(v[13], v[13]);
        IntrusiveEdge e14_14 = g.addEdge(v[14], v[14]);
        IntrusiveEdge e15_15 = g.addEdge(v[15], v[15]);
        IntrusiveEdge e15_15_1 = g.addEdge(v[15], v[15]);
        IntrusiveEdge e15_15_2 = g.addEdge(v[15], v[15]);

        assertEquals(0, g.inDegreeOf(v[0]));
        assertTrue(g.incomingEdgesOf(v[0]).isEmpty());
        assertEquals(0, g.outDegreeOf(v[0]));
        assertTrue(g.outgoingEdgesOf(v[0]).isEmpty());
        assertTrue(g.edgesOf(v[0]).isEmpty());
        assertEquals(0, g.inDegreeOf(v[1]));
        assertTrue(g.incomingEdgesOf(v[1]).isEmpty());
        assertEquals(0, g.outDegreeOf(v[1]));
        assertTrue(g.outgoingEdgesOf(v[1]).isEmpty());
        assertTrue(g.edgesOf(v[1]).isEmpty());
        assertEquals(0, g.inDegreeOf(v[2]));
        assertTrue(g.incomingEdgesOf(v[2]).isEmpty());
        assertEquals(3, g.outDegreeOf(v[2]));
        assertTrue(
            g.outgoingEdgesOf(v[2]).containsAll(new HashSet<>(Arrays.asList(e2_5, e2_6, e2_7))));
        assertEquals(3, g.outgoingEdgesOf(v[2]).size());
        assertTrue(g.edgesOf(v[2]).containsAll(new HashSet<>(Arrays.asList(e2_5, e2_6, e2_7))));
        assertEquals(3, g.edgesOf(v[2]).size());
        assertEquals(0, g.inDegreeOf(v[3]));
        assertTrue(g.incomingEdgesOf(v[3]).isEmpty());
        assertEquals(1, g.outDegreeOf(v[3]));
        assertTrue(g.outgoingEdgesOf(v[3]).containsAll(new HashSet<>(Arrays.asList(e3_7))));
        assertEquals(1, g.outgoingEdgesOf(v[3]).size());
        assertTrue(g.edgesOf(v[3]).containsAll(new HashSet<>(Arrays.asList(e3_7))));
        assertEquals(1, g.edgesOf(v[3]).size());
        assertEquals(0, g.inDegreeOf(v[4]));
        assertTrue(g.incomingEdgesOf(v[4]).isEmpty());
        assertEquals(1, g.outDegreeOf(v[4]));
        assertTrue(g.outgoingEdgesOf(v[4]).containsAll(new HashSet<>(Arrays.asList(e4_7))));
        assertEquals(1, g.outgoingEdgesOf(v[4]).size());
        assertTrue(g.edgesOf(v[4]).containsAll(new HashSet<>(Arrays.asList(e4_7))));
        assertEquals(1, g.edgesOf(v[4]).size());
        assertEquals(3, g.inDegreeOf(v[5]));
        assertTrue(
            g.incomingEdgesOf(v[5]).containsAll(new HashSet<>(Arrays.asList(e2_5, e8_5, e9_5))));
        assertEquals(3, g.incomingEdgesOf(v[5]).size());
        assertEquals(0, g.outDegreeOf(v[5]));
        assertTrue(g.outgoingEdgesOf(v[5]).isEmpty());
        assertTrue(g.edgesOf(v[5]).containsAll(new HashSet<>(Arrays.asList(e2_5, e8_5, e9_5))));
        assertEquals(3, g.edgesOf(v[5]).size());
        assertEquals(1, g.inDegreeOf(v[6]));
        assertTrue(g.incomingEdgesOf(v[6]).containsAll(new HashSet<>(Arrays.asList(e2_6))));
        assertEquals(1, g.incomingEdgesOf(v[6]).size());
        assertEquals(0, g.outDegreeOf(v[6]));
        assertTrue(g.outgoingEdgesOf(v[6]).isEmpty());
        assertTrue(g.edgesOf(v[6]).containsAll(new HashSet<>(Arrays.asList(e2_6))));
        assertEquals(1, g.edgesOf(v[6]).size());
        assertEquals(3, g.inDegreeOf(v[7]));
        assertTrue(
            g.incomingEdgesOf(v[7]).containsAll(new HashSet<>(Arrays.asList(e2_7, e3_7, e4_7))));
        assertEquals(3, g.incomingEdgesOf(v[7]).size());
        assertEquals(2, g.outDegreeOf(v[7]));
        assertTrue(g.outgoingEdgesOf(v[7]).containsAll(new HashSet<>(Arrays.asList(e7_10, e7_11))));
        assertEquals(2, g.outgoingEdgesOf(v[7]).size());
        assertTrue(
            g.edgesOf(v[7]).containsAll(
                new HashSet<>(Arrays.asList(e7_10, e7_11, e2_7, e3_7, e4_7))));
        assertEquals(5, g.edgesOf(v[7]).size());
        assertEquals(0, g.inDegreeOf(v[8]));
        assertTrue(g.incomingEdgesOf(v[8]).isEmpty());
        assertEquals(1, g.outDegreeOf(v[8]));
        assertTrue(g.outgoingEdgesOf(v[8]).containsAll(new HashSet<>(Arrays.asList(e8_5))));
        assertEquals(1, g.outgoingEdgesOf(v[8]).size());
        assertTrue(g.edgesOf(v[8]).containsAll(new HashSet<>(Arrays.asList(e8_5))));
        assertEquals(1, g.edgesOf(v[8]).size());
        assertEquals(0, g.inDegreeOf(v[9]));
        assertTrue(g.incomingEdgesOf(v[9]).isEmpty());
        assertEquals(1, g.outDegreeOf(v[9]));
        assertTrue(g.outgoingEdgesOf(v[9]).containsAll(new HashSet<>(Arrays.asList(e9_5))));
        assertEquals(1, g.outgoingEdgesOf(v[9]).size());
        assertTrue(g.edgesOf(v[9]).containsAll(new HashSet<>(Arrays.asList(e9_5))));
        assertEquals(1, g.edgesOf(v[9]).size());
        assertEquals(1, g.inDegreeOf(v[10]));
        assertTrue(g.incomingEdgesOf(v[10]).containsAll(new HashSet<>(Arrays.asList(e7_10))));
        assertEquals(1, g.incomingEdgesOf(v[10]).size());
        assertEquals(0, g.outDegreeOf(v[10]));
        assertTrue(g.outgoingEdgesOf(v[10]).isEmpty());
        assertTrue(g.edgesOf(v[10]).containsAll(new HashSet<>(Arrays.asList(e7_10))));
        assertEquals(1, g.edgesOf(v[10]).size());
        assertEquals(1, g.inDegreeOf(v[11]));
        assertTrue(g.incomingEdgesOf(v[11]).containsAll(new HashSet<>(Arrays.asList(e7_11))));
        assertEquals(1, g.incomingEdgesOf(v[11]).size());
        assertEquals(0, g.outDegreeOf(v[11]));
        assertTrue(g.outgoingEdgesOf(v[11]).isEmpty());
        assertTrue(g.edgesOf(v[11]).containsAll(new HashSet<>(Arrays.asList(e7_11))));
        assertEquals(1, g.edgesOf(v[11]).size());
        assertEquals(2, g.inDegreeOf(v[12]));
        assertTrue(
            g.incomingEdgesOf(v[12]).containsAll(new HashSet<>(Arrays.asList(e13_12, e13_12_1))));
        assertEquals(2, g.incomingEdgesOf(v[12]).size());
        assertEquals(3, g.outDegreeOf(v[12]));
        assertTrue(
            g.outgoingEdgesOf(v[12]).containsAll(
                new HashSet<>(Arrays.asList(e12_13, e12_13_1, e12_13_2))));
        assertEquals(3, g.outgoingEdgesOf(v[12]).size());
        assertTrue(
            g.edgesOf(v[12]).containsAll(
                new HashSet<>(Arrays.asList(e12_13, e12_13_1, e12_13_2, e13_12, e13_12_1))));
        assertEquals(5, g.edgesOf(v[12]).size());
        assertEquals(5, g.inDegreeOf(v[13]));
        assertTrue(
            g.incomingEdgesOf(v[13]).containsAll(
                new HashSet<>(Arrays.asList(e12_13, e12_13_1, e12_13_2, e13_13, e13_13_1))));
        assertEquals(5, g.incomingEdgesOf(v[13]).size());
        assertEquals(4, g.outDegreeOf(v[13]));
        assertTrue(
            g.outgoingEdgesOf(v[13]).containsAll(
                new HashSet<>(Arrays.asList(e13_12, e13_12_1, e13_13, e13_13_1))));
        assertEquals(4, g.outgoingEdgesOf(v[13]).size());
        assertTrue(
            g.edgesOf(v[13]).containsAll(
                new HashSet<>(
                    Arrays
                        .asList(e12_13, e12_13_1, e12_13_2, e13_13_1, e13_12, e13_12_1, e13_13))));
        assertEquals(7, g.edgesOf(v[13]).size());
        assertEquals(1, g.inDegreeOf(v[14]));
        assertTrue(g.incomingEdgesOf(v[14]).containsAll(new HashSet<>(Arrays.asList(e14_14))));
        assertEquals(1, g.incomingEdgesOf(v[14]).size());
        assertEquals(1, g.outDegreeOf(v[14]));
        assertTrue(g.outgoingEdgesOf(v[14]).containsAll(new HashSet<>(Arrays.asList(e14_14))));
        assertEquals(1, g.outgoingEdgesOf(v[14]).size());
        assertTrue(g.edgesOf(v[14]).containsAll(new HashSet<>(Arrays.asList(e14_14))));
        assertEquals(1, g.edgesOf(v[14]).size());
        assertEquals(3, g.inDegreeOf(v[15]));
        assertTrue(
            g.incomingEdgesOf(v[15]).containsAll(
                new HashSet<>(Arrays.asList(e15_15, e15_15_1, e15_15_2))));
        assertEquals(3, g.incomingEdgesOf(v[15]).size());
        assertEquals(3, g.outDegreeOf(v[15]));
        assertTrue(
            g.outgoingEdgesOf(v[15]).containsAll(
                new HashSet<>(Arrays.asList(e15_15, e15_15_1, e15_15_2))));
        assertEquals(3, g.outgoingEdgesOf(v[15]).size());
        assertTrue(
            g.edgesOf(v[15]).containsAll(new HashSet<>(Arrays.asList(e15_15, e15_15_1, e15_15_2))));
        assertEquals(3, g.edgesOf(v[15]).size());

        assertEquals(20, g.edgeSet().size());
        assertEquals(20, g.edgeSet().stream().collect(Collectors.toList()).size());
        assertEquals(20, g.edgeSet().stream().collect(Collectors.toSet()).size());
        assertTrue(
            g.edgeSet().containsAll(
                Arrays.asList(
                    e2_5, e2_6, e2_7, e3_7, e4_7, e8_5, e9_5, e7_10, e7_11, e12_13, e12_13_1,
                    e12_13_2, e13_12, e13_12_1, e13_13, e13_13_1, e14_14, e15_15, e15_15_1,
                    e15_15_2)));
    }

    @Test
    public void testUndirectedDirectGraph()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, UNDIRECTED);
        IntrusiveVertex[] v = new IntrusiveVertex[16];
        for (int i = 0; i < 16; i++) {
            v[i] = new NamedVertex("v" + i);
            g.addVertex(v[i]);
        }
        IntrusiveEdge e2_5 = g.addEdge(v[2], v[5]);
        IntrusiveEdge e2_6 = g.addEdge(v[2], v[6]);
        IntrusiveEdge e2_7 = g.addEdge(v[2], v[7]);
        IntrusiveEdge e3_7 = g.addEdge(v[3], v[7]);
        IntrusiveEdge e4_7 = g.addEdge(v[4], v[7]);
        IntrusiveEdge e8_5 = g.addEdge(v[8], v[5]);
        IntrusiveEdge e9_5 = g.addEdge(v[9], v[5]);
        IntrusiveEdge e7_10 = g.addEdge(v[7], v[10]);
        IntrusiveEdge e7_11 = g.addEdge(v[7], v[11]);
        IntrusiveEdge e12_13 = g.addEdge(v[12], v[13]);
        IntrusiveEdge e12_13_1 = g.addEdge(v[12], v[13]);
        IntrusiveEdge e12_13_2 = g.addEdge(v[12], v[13]);
        IntrusiveEdge e13_12 = g.addEdge(v[13], v[12]);
        IntrusiveEdge e13_12_1 = g.addEdge(v[13], v[12]);
        IntrusiveEdge e13_13 = g.addEdge(v[13], v[13]);
        IntrusiveEdge e13_13_1 = g.addEdge(v[13], v[13]);
        IntrusiveEdge e14_14 = g.addEdge(v[14], v[14]);
        IntrusiveEdge e15_15 = g.addEdge(v[15], v[15]);
        IntrusiveEdge e15_15_1 = g.addEdge(v[15], v[15]);
        IntrusiveEdge e15_15_2 = g.addEdge(v[15], v[15]);

        assertEquals(0, g.degreeOf(v[0]));
        assertTrue(g.edgesOf(v[0]).isEmpty());
        assertEquals(0, g.degreeOf(v[1]));
        assertTrue(g.edgesOf(v[1]).isEmpty());
        assertEquals(3, g.degreeOf(v[2]));
        assertTrue(g.edgesOf(v[2]).containsAll(new HashSet<>(Arrays.asList(e2_5, e2_6, e2_7))));
        assertEquals(1, g.degreeOf(v[3]));
        assertTrue(g.edgesOf(v[3]).containsAll(new HashSet<>(Arrays.asList(e3_7))));
        assertEquals(1, g.degreeOf(v[4]));
        assertTrue(g.edgesOf(v[4]).containsAll(new HashSet<>(Arrays.asList(e4_7))));
        assertEquals(3, g.degreeOf(v[5]));
        assertTrue(g.edgesOf(v[5]).containsAll(new HashSet<>(Arrays.asList(e2_5, e8_5, e9_5))));
        assertEquals(1, g.degreeOf(v[6]));
        assertTrue(g.edgesOf(v[6]).containsAll(new HashSet<>(Arrays.asList(e2_6))));
        assertEquals(5, g.degreeOf(v[7]));
        assertTrue(
            g.edgesOf(v[7]).containsAll(
                new HashSet<>(Arrays.asList(e2_7, e3_7, e4_7, e7_10, e7_11))));
        assertEquals(1, g.degreeOf(v[8]));
        assertTrue(g.edgesOf(v[8]).containsAll(new HashSet<>(Arrays.asList(e8_5))));
        assertEquals(1, g.degreeOf(v[9]));
        assertTrue(g.edgesOf(v[9]).containsAll(new HashSet<>(Arrays.asList(e9_5))));
        assertEquals(1, g.degreeOf(v[10]));
        assertTrue(g.edgesOf(v[10]).containsAll(new HashSet<>(Arrays.asList(e7_10))));
        assertEquals(1, g.degreeOf(v[11]));
        assertTrue(g.edgesOf(v[11]).containsAll(new HashSet<>(Arrays.asList(e7_11))));
        assertEquals(5, g.degreeOf(v[12]));
        assertTrue(
            g.edgesOf(v[12]).containsAll(
                new HashSet<>(Arrays.asList(e12_13, e12_13_1, e12_13_2, e13_12, e13_12_1))));
        assertEquals(9, g.degreeOf(v[13]));
        assertTrue(
            g.edgesOf(v[13]).containsAll(
                new HashSet<>(
                    Arrays
                        .asList(e12_13, e12_13_1, e12_13_2, e13_12, e13_12_1, e13_13, e13_13_1))));
        assertEquals(2, g.degreeOf(v[14]));
        assertTrue(g.edgesOf(v[14]).containsAll(new HashSet<>(Arrays.asList(e14_14))));
        assertEquals(6, g.degreeOf(v[15]));
        assertTrue(
            g.edgesOf(v[15]).containsAll(new HashSet<>(Arrays.asList(e15_15, e15_15_1, e15_15_2))));

        assertEquals(20, g.edgeSet().size());
        assertEquals(20, g.edgeSet().stream().collect(Collectors.toList()).size());
        assertEquals(20, g.edgeSet().stream().collect(Collectors.toSet()).size());
        assertTrue(
            g.edgeSet().containsAll(
                Arrays.asList(
                    e2_5, e2_6, e2_7, e3_7, e4_7, e8_5, e9_5, e7_10, e7_11, e12_13, e12_13_1,
                    e12_13_2, e13_12, e13_12_1, e13_13, e13_13_1, e14_14, e15_15, e15_15_1,
                    e15_15_2)));
    }

    @Test
    public void testDirectGraph()
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED, true);
        IntrusiveVertex v1 = new NamedVertex("v1");
        IntrusiveVertex v2 = new NamedVertex("v2");
        g.addVertex(v1);
        assertTrue(g.containsVertex(v1));
        assertFalse(g.containsVertex(v2));
        g.addVertex(v2);
        assertTrue(g.containsVertex(v2));
        IntrusiveVertex v3 = new NamedVertex("v3");
        g.addVertex(v3);
        IntrusiveVertex v4 = new NamedVertex("v4");
        g.addVertex(v4);
        IntrusiveVertex v5 = new NamedVertex("v5");
        g.addVertex(v5);
        IntrusiveVertex v6 = new NamedVertex("v6");
        g.addVertex(v6);
        IntrusiveVertex v7 = new NamedVertex("v7");
        g.addVertex(v7);

        IntrusiveEdge e12 = g.addEdge(v1, v2);
        assertTrue(g.containsEdge(e12));
        IntrusiveEdge e13 = g.addEdge(v1, v3);
        assertTrue(g.containsEdge(e13));
        IntrusiveEdge e14 = g.addEdge(v1, v4);
        assertTrue(g.containsEdge(e14));
        IntrusiveEdge e15 = g.addEdge(v1, v5);
        assertTrue(g.containsEdge(e15));
        assertFalse(g.containsEdge(new IntrusiveEdge()));
        g.addEdge(v2, v3);
        g.addEdge(v2, v5);
        g.addEdge(v3, v5);
        g.addEdge(v3, v1);
        g.addEdge(v3, v3);

        for (IntrusiveVertex v : g.vertexSet()) {
            System.out.println("vertex " + v);
            System.out.println("-------");
            for (IntrusiveEdge e : g.edgesOf(v)) {
                System.out.println(
                    "edge " + e + " from " + g.getEdgeSource(e) + " to " + g.getEdgeTarget(e));
            }
        }

        System.out.println("All edges");

        for (IntrusiveEdge e : g.edgeSet()) {
            System.out
                .println("edge " + e + " from " + g.getEdgeSource(e) + " to " + g.getEdgeTarget(e));
        }

    }

    @Test
    public void testUndirectedToString()
        throws Exception
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, UNDIRECTED);

        IntrusiveVertex[] v = new IntrusiveVertex[16];
        for (int i = 0; i < 3; i++) {
            v[i] = new NamedVertex("v" + i);
            g.addVertex(v[i]);
        }
        g.addEdge(v[0], v[1]);
        g.addEdge(v[1], v[2]);

        System.out.println(g.toString());

        assertEquals("([v0, v1, v2], [(v0,v1)={v0,v1}, (v1,v2)={v1,v2}])", g.toString());
    }

    @Test
    public void testDirectedToString()
        throws Exception
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED);

        IntrusiveVertex[] v = new IntrusiveVertex[16];
        for (int i = 0; i < 3; i++) {
            v[i] = new NamedVertex("v" + i);
            g.addVertex(v[i]);
        }
        g.addEdge(v[0], v[1]);
        g.addEdge(v[1], v[2]);

        System.out.println(g.toString());

        assertEquals("([v0, v1, v2], [(v0,v1)=(v0,v1), (v1,v2)=(v1,v2)])", g.toString());
    }

    @Test
    public void testSerializeUndirected()
        throws Exception
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, UNDIRECTED);

        IntrusiveVertex[] v = new IntrusiveVertex[16];
        for (int i = 0; i < 16; i++) {
            v[i] = new NamedVertex("v" + i);
            g.addVertex(v[i]);
        }
        g.addEdge(v[2], v[5]);
        g.addEdge(v[2], v[6]);
        g.addEdge(v[2], v[7]);
        g.addEdge(v[3], v[7]);
        g.addEdge(v[4], v[7]);
        g.addEdge(v[8], v[5]);
        g.addEdge(v[9], v[5]);
        g.addEdge(v[7], v[10]);
        g.addEdge(v[7], v[11]);
        g.addEdge(v[12], v[13]);
        g.addEdge(v[12], v[13]);
        g.addEdge(v[12], v[13]);
        g.addEdge(v[13], v[12]);
        g.addEdge(v[13], v[12]);
        g.addEdge(v[13], v[13]);
        g.addEdge(v[13], v[13]);
        g.addEdge(v[14], v[14]);
        g.addEdge(v[15], v[15]);
        g.addEdge(v[15], v[15]);
        g.addEdge(v[15], v[15]);

        IntrusiveGraph g1 = (IntrusiveGraph) serializeAndDeserialize(g);
        assertEquals(g.toString(), g1.toString());
    }

    @Test
    public void testSerializeDirected()
        throws Exception
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED);
        IntrusiveVertex[] v = new IntrusiveVertex[16];
        for (int i = 0; i < 16; i++) {
            v[i] = new NamedVertex("v" + i);
            g.addVertex(v[i]);
        }
        g.addEdge(v[2], v[5]);
        g.addEdge(v[2], v[6]);
        g.addEdge(v[2], v[7]);
        g.addEdge(v[3], v[7]);
        g.addEdge(v[4], v[7]);
        g.addEdge(v[8], v[5]);
        g.addEdge(v[9], v[5]);
        g.addEdge(v[7], v[10]);
        g.addEdge(v[7], v[11]);
        g.addEdge(v[12], v[13]);
        g.addEdge(v[12], v[13]);
        g.addEdge(v[12], v[13]);
        g.addEdge(v[13], v[12]);
        g.addEdge(v[13], v[12]);
        g.addEdge(v[13], v[13]);
        g.addEdge(v[13], v[13]);
        g.addEdge(v[14], v[14]);
        g.addEdge(v[15], v[15]);
        g.addEdge(v[15], v[15]);
        g.addEdge(v[15], v[15]);
        
        assertTrue(g.edgeSet().stream().allMatch(e -> e.graph == g));
        assertTrue(g.vertexSet().stream().allMatch(e -> e.graph == g));

        IntrusiveGraph g1 = (IntrusiveGraph) serializeAndDeserialize(g);
        assertEquals(g.toString(), g1.toString());

        assertTrue(g1.edgeSet().stream().allMatch(e -> e.graph == g1));
        assertTrue(g1.vertexSet().stream().allMatch(e -> e.graph == g1));
    }

    @Test
    public void testSerializeWithWeights()
        throws Exception
    {
        IntrusiveGraph g = new IntrusiveGraph(NamedEdge.class, DIRECTED);
        IntrusiveVertex v1 = new NamedVertex("v1");
        g.addVertex(v1);
        IntrusiveVertex v2 = new NamedVertex("v2");
        g.addVertex(v2);
        IntrusiveEdge e1 = g.addEdge(v1, v2);
        g.setEdgeWeight(e1, 99.0);

        IntrusiveGraph g1 = (IntrusiveGraph) serializeAndDeserialize(g);
        assertEquals(g.toString(), g1.toString());

        assertEquals(99.0, g1.getEdgeWeight(g1.edgeSet().stream().findFirst().get()), 1e-9);
    }

    @Test
    public void testEquals()
        throws Exception
    {
        IntrusiveGraph g1 = new IntrusiveGraph(NamedEdge.class, DIRECTED);
        IntrusiveGraph g2 = new IntrusiveGraph(NamedEdge.class, DIRECTED);
        assertFalse(g1.equals(g2));
    }

    public static class NamedVertex
        extends IntrusiveVertex
    {
        private static final long serialVersionUID = 1L;

        private String name;

        public NamedVertex(String name)
        {
            super();
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    public static class NamedEdge
        extends IntrusiveEdge
    {
        private static final long serialVersionUID = 1L;

        public NamedEdge()
        {
            super();
        }

        @Override
        public String toString()
        {
            return "(" + source + "," + target + ")";
        }
    }

    private Object serializeAndDeserialize(Object obj)
        throws Exception
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);

        out.writeObject(obj);
        out.flush();

        ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bin);

        obj = in.readObject();
        return obj;
    }

}
