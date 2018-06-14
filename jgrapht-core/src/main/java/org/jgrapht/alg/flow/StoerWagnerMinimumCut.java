/*
 * (C) Copyright 2011-2018, by Robby McKilliam, Ernst de Ridder and Contributors.
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
package org.jgrapht.alg.flow;

import org.jgrapht.*;
import org.jgrapht.alg.interfaces.MinimumCutAlgorithm;
import org.jgrapht.graph.*;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

import java.util.*;

/**
 * Implements the <a href="http://dl.acm.org/citation.cfm?id=263872">Stoer and Wagner minimum cut
 * algorithm</a>. Deterministically computes the minimum cut in $O(|V||E| + |V| \log |V|)$ time.
 * This implementation uses Java's PriorityQueue and requires $O(|V||E| \log |E|)$ time. M. Stoer
 * and F. Wagner, "A Simple Min-Cut Algorithm", Journal of the ACM, volume 44, number 4. pp 585-591,
 * 1997.
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Robby McKilliam
 * @author Ernst de Ridder
 */
public class StoerWagnerMinimumCut<V, E>
    implements MinimumCutAlgorithm<V, E>
{
    private Graph<Set<V>, DefaultWeightedEdge> workingGraph;
    private final Graph<V, E> graph;

    private double bestCutWeight = Double.POSITIVE_INFINITY;
    private Set<V> bestCut;

    /**
     * Constructor
     *
     * @param graph input graph
     */
    public StoerWagnerMinimumCut(Graph<V, E> graph)
    {
        this.graph = GraphTests.requireUndirected(graph);
    }

    @Override
    public Cut<V> getCut()
    {
        if (bestCut == null) {
            if (graph.vertexSet().size() < 2) {
                throw new IllegalArgumentException("Graph has less than 2 vertices");
            }

            // get a version of this graph where each vertex is a set
            this.workingGraph = GraphTypeBuilder
                .undirected().allowingMultipleEdges(false).allowingSelfLoops(false).weighted(true)
                .edgeSupplier(SupplierUtil.DEFAULT_WEIGHTED_EDGE_SUPPLIER)
                .vertexSupplier(() -> (Set<V>) new HashSet<V>()).buildGraph();

            Map<V, Set<V>> vertexMap = new HashMap<>();
            for (V v : graph.vertexSet()) {
                Set<V> vNew = workingGraph.addVertex();
                vNew.add(v);
                vertexMap.put(v, vNew);
            }

            for (E e : graph.edgeSet()) {
                if (graph.getEdgeWeight(e) < 0.0) {
                    throw new IllegalArgumentException("Negative edge weights not allowed");
                }

                V s = graph.getEdgeSource(e);
                Set<V> sNew = vertexMap.get(s);
                V t = graph.getEdgeTarget(e);
                Set<V> tNew = vertexMap.get(t);

                if (s.equals(t)) {
                    continue;
                }

                // For multi graphs, we sum the edge weights (either all are
                // contained in a cut, or none)
                DefaultWeightedEdge eNew = workingGraph.getEdge(sNew, tNew);
                if (eNew == null) {
                    eNew = workingGraph.addEdge(sNew, tNew);
                    workingGraph.setEdgeWeight(eNew, graph.getEdgeWeight(e));
                } else {
                    workingGraph.setEdgeWeight(
                        eNew, workingGraph.getEdgeWeight(eNew) + graph.getEdgeWeight(e));
                }
            }

            // arbitrary vertex used to seed the algorithm.
            Set<V> a = workingGraph.vertexSet().iterator().next();

            while (workingGraph.vertexSet().size() > 1) {
                minimumCutPhase(a);
            }
        }
        return new CutImpl<>(bestCut, bestCutWeight);
    }

    /**
     * Implements the MinimumCutPhase function of Stoer and Wagner.
     * 
     * @param a the vertex
     */
    private void minimumCutPhase(Set<V> a)
    {
        // The last and before last vertices added to A.
        Set<V> last = a, beforelast = null;
        
        // queue contains vertices not in A ordered by max weight of edges to A.
        PriorityQueue<VertexAndWeight> queue = new PriorityQueue<>();

        // Maps vertices to elements of queue
        Map<Set<V>, VertexAndWeight> dmap = new HashMap<>();

        // Initialize queue
        for (Set<V> v : workingGraph.vertexSet()) {
            if (v.equals(a)) {
                continue;
            }
            DefaultWeightedEdge e = workingGraph.getEdge(v, a);
            Double w = (e == null) ? 0.0 : workingGraph.getEdgeWeight(e);
            VertexAndWeight vandw = new VertexAndWeight(v, w, e != null);
            queue.add(vandw);
            dmap.put(v, vandw);
        }

        // Now iteratively update the queue to get the required vertex ordering

        while (!queue.isEmpty()) {
            Set<V> v = queue.poll().vertex;
            dmap.remove(v);

            beforelast = last;
            last = v;

            for (DefaultWeightedEdge e : workingGraph.edgesOf(v)) {
                Set<V> vc = Graphs.getOppositeVertex(workingGraph, e, v);
                VertexAndWeight vcandw = dmap.get(vc);
                if (vcandw != null) {
                    queue.remove(vcandw); // this is O(log n) but could be O(1)?
                    vcandw.active = true;
                    vcandw.weight += workingGraph.getEdgeWeight(e);
                    queue.add(vcandw); // this is O(log n) but could be O(1)?
                }
            }
        }

        // Update the best cut
        double w = workingGraph
            .edgesOf(last).stream().mapToDouble(e -> workingGraph.getEdgeWeight(e)).sum();
        if (w < bestCutWeight) {
            bestCutWeight = w;
            bestCut = last;
        }

        // merge the last added vertices
        mergeVertices(beforelast, last);
    }

    /**
     * Merges vertex $t$ into vertex $s$, summing the weights as required. Returns the merged vertex
     * and the sum of its weights
     * 
     * @param s the first vertex
     * @param t the second vertex
     * 
     * @return the merged vertex and its weight
     */
    protected VertexAndWeight mergeVertices(Set<V> s, Set<V> t)
    {
        // construct the new combinedvertex
        Set<V> set = new HashSet<>();
        set.addAll(s);
        set.addAll(t);
        workingGraph.addVertex(set);

        // add edges and weights to the combined vertex
        double wsum = 0.0;
        for (Set<V> v : workingGraph.vertexSet()) {
            if ((s != v) && (t != v)) {
                double neww = 0.0;
                DefaultWeightedEdge etv = workingGraph.getEdge(t, v);
                DefaultWeightedEdge esv = workingGraph.getEdge(s, v);
                if (etv != null) {
                    neww += workingGraph.getEdgeWeight(etv);
                }
                if (esv != null) {
                    neww += workingGraph.getEdgeWeight(esv);
                }
                if ((etv != null) || (esv != null)) {
                    wsum += neww;
                    workingGraph.setEdgeWeight(workingGraph.addEdge(set, v), neww);
                }
            }
        }

        // remove original vertices
        workingGraph.removeVertex(t);
        workingGraph.removeVertex(s);

        return new VertexAndWeight(set, wsum, false);
    }

    /**
     * Class for weighted vertices
     */
    private class VertexAndWeight
        implements Comparable<VertexAndWeight>
    {
        public Set<V> vertex;
        public Double weight;
        public boolean active; // active == neighbour in A

        /**
         * Construct a new weighted vertex.
         * 
         * @param v the vertex
         * @param w the weight of the vertex
         * @param active whether it is active
         */
        public VertexAndWeight(Set<V> v, double w, boolean active)
        {
            this.vertex = v;
            this.weight = w;
            this.active = active;
        }

        /**
         * compareTo that sorts in reverse order because we need extract-max and queue provides
         * extract-min.
         */
        @Override
        public int compareTo(VertexAndWeight that)
        {
            if (this.active && that.active) {
                return -Double.compare(weight, that.weight);
            }
            if (this.active && !that.active) {
                return -1;
            }
            if (!this.active && that.active) {
                return +1;
            }

            // both inactive
            return 0;
        }

        @Override
        public String toString()
        {
            return "(" + vertex + ", " + weight + ")";
        }
    }

}

// End StoerWagnerMinimumCut.java
