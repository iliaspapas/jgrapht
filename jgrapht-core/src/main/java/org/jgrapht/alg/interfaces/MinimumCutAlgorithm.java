/*
 * (C) Copyright 2018-2018, by Dimitrios Michail and Contributors.
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
package org.jgrapht.alg.interfaces;

import java.util.Set;

import org.jgrapht.util.WeightedUnmodifiableSet;

/**
 * An algorithm for the computation of a cut of minimum weight.
 * 
 * <p>
 * Let $G=(V,E)$ be an undirected graph and let $w: E \mapsto \mathcal{R}_{\ge 0}$ be a non-negative
 * weight function on the edges of $G$. A cut $C$ of $G$ is any subset of $V$ with $0 \neq C \neq
 * V$. The weight of a cut is the total weight of the edges crossing the cut.
 * 
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 *
 * @author Dimitrios Michail
 */
public interface MinimumCutAlgorithm<V, E>
{
    /**
     * Computes a cut.
     *
     * @return a cut
     */
    Cut<V> getCut();

    /**
     * A cut
     * 
     * @param <V> the vertex type
     */
    interface Cut<V>
        extends Set<V>
    {
        /**
         * Returns the weight of the cut.
         *
         * @return weight of the cut
         */
        double getWeight();
    }

    /**
     * Default implementation of a cut
     *
     * @param <V> the vertex type
     */
    class CutImpl<V, E>
        extends WeightedUnmodifiableSet<V>
        implements Cut<V>
    {
        private static final long serialVersionUID = 1908336876001937523L;

        public CutImpl(Set<V> vertexCover)
        {
            super(vertexCover);
        }

        public CutImpl(Set<V> vertexCover, double weight)
        {
            super(vertexCover, weight);
        }
    }

}
