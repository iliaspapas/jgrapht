/*
 * (C) Copyright 2018-2018 Dimitrios Michail and Contributors.
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
package org.jgrapht.alg.isomorphism;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.VertexLabelingAlgorithm;
import org.jgrapht.alg.util.Pair;

/**
 * The Weisfeiler-Lehman labeling.
 * 
 * <p>
 * This is an algorithm which computes the 1-dimensional Weisfeiler-Lehman labeling, also known as
 * the naive vertex refinement. The labeling was introduced in the paper: B. Weisfeiler and A. A.
 * Lehman. A reduction of a graph to a canonical form and an algebra arising during this reduction.
 * Nauchno-Technicheskaya Informatsia, Ser. 2, 9, 1968.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class WeisfeilerLehmanLabeling<V, E>
    implements
    VertexLabelingAlgorithm<V>
{

    private static final String DEFAULT_INITIAL_LABEL = "1";

    private final Graph<V, E> graph;
    private final int iterations;
    private final NeighborhoodType neighborhoodType;
    private Map<V, String> labels;

    /**
     * Construct a new labeling algorithm.
     * 
     * @param graph the input graph
     * @param iterations number of iterations of the Weisfeiler-Lehman relabeling
     * @param initialLabels initial labels of the vertices
     * @param neighborhoodType what kind of neighbors to use for each vertex in order to compute the
     *        labeling
     */
    public WeisfeilerLehmanLabeling(
        Graph<V, E> graph, int iterations, Map<V, String> initialLabels,
        NeighborhoodType neighborhoodType)
    {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        if (iterations < 1) {
            throw new IllegalArgumentException("Number of iterations must be positive");
        }
        this.iterations = iterations;
        this.neighborhoodType = Objects.requireNonNull(neighborhoodType);

        if (initialLabels == null) {
            throw new IllegalArgumentException("Initial labels cannot be null.");
        }
        this.labels = new HashMap<>();
        for (V v : graph.vertexSet()) {
            labels.put(v, initialLabels.getOrDefault(v, DEFAULT_INITIAL_LABEL));
        }
    }

    /**
     * Construct a new labeling algorithm.
     * 
     * @param graph the input graph
     * @param iterations number of iterations of the Weisfeiler-Lehman relabeling
     */
    public WeisfeilerLehmanLabeling(Graph<V, E> graph, int iterations)
    {
        this(graph, iterations, Collections.emptyMap(), NeighborhoodType.OUTGOING);
    }

    @Override
    public Labeling<V> getLabeling()
    {
        final int n = graph.vertexSet().size();

        for (int k = 0; k < iterations; k++) {
            /*
             * Generate new labels. Use an array so we can sort fast later on.
             */
            @SuppressWarnings("unchecked") Pair<String, V>[] newLabels =
                (Pair<String, V>[]) Array.newInstance(Pair.class, n);
            int cur = 0;
            for (V v : graph.vertexSet()) {
                newLabels[cur++] = Pair.of(computeNewLabel(v), v);
            }

            /*
             * Sort
             */
            Arrays.parallelSort(newLabels, Comparator.comparing(Pair::getFirst));

            /*
             * Compress
             */
            for (int i = 0, nextLabel = 1; i < n; i++) {
                String curLabel = newLabels[i].getFirst();
                if (i > 0) {
                    String prevLabel = newLabels[i - 1].getFirst();
                    if (!curLabel.equals(prevLabel)) {
                        nextLabel++;
                    }
                }
                V curVertex = newLabels[i].getSecond();
                labels.put(curVertex, String.valueOf(nextLabel));
            }
        }

        return new LabelingImpl<>(labels);
    }

    /**
     * What kind of neighborhood to use when computing new labels.
     */
    public enum NeighborhoodType
    {
        OUTGOING,
        INCOMING,
    }

    /**
     * Compute a new label for a particular vertex.
     * 
     * @param v the vertex to compute
     * @return the new label
     */
    private String computeNewLabel(V v)
    {
        /*
         * Get sorted list of neighborhood labels
         */
        Set<E> neighbors;
        switch (neighborhoodType) {
        case INCOMING:
            neighbors = graph.incomingEdgesOf(v);
            break;
        default:
            neighbors = graph.outgoingEdgesOf(v);
            break;
        }
        String[] multiset = (String[]) Array.newInstance(String.class, neighbors.size());
        int i = 0;
        for (E e : neighbors) {
            multiset[i++] = labels.get(Graphs.getOppositeVertex(graph, e, v));
        }
        Arrays.parallelSort(multiset);

        /*
         * Prepend own label and append sorted neighbors
         */
        StringBuilder sb = new StringBuilder();
        sb.append(labels.get(v));
        sb.append(String.join("", multiset));
        return sb.toString();
    }

}
