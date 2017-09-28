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

import java.io.Serializable;

import org.jgrapht.Graph;

/**
 * The edge of the intrusive graph.
 * 
 * @author Dimitrios Michail
 */
public class IntrusiveEdge
    implements Serializable
{
    private static final long serialVersionUID = -4763245997532827496L;

    transient IntrusiveGraph graph = null;

    IntrusiveVertex source;
    IntrusiveVertex target;
    double weight;

    transient IntrusiveEdge outNext = null;
    transient IntrusiveEdge outPrev = null;

    transient IntrusiveEdge inNext = null;
    transient IntrusiveEdge inPrev = null;

    /**
     * Create a new intrusive edge.
     */
    public IntrusiveEdge()
    {
        this.source = null;
        this.target = null;
        this.weight = Graph.DEFAULT_EDGE_WEIGHT;
    }

    IntrusiveVertex getOpposite(IntrusiveVertex v)
    {
        if (v == source) {
            return target;
        } else if (v == target) {
            return source;
        } else {
            throw new IllegalArgumentException("no such vertex: " + v.toString());
        }
    }

}
