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
package org.jgrapht.alg.drawing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.DoubleFRQuadTree.Node;
import org.jgrapht.alg.drawing.model.DoublePoint2D;
import org.jgrapht.alg.drawing.model.LayoutModel;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.drawing.model.Points;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.util.ToleranceDoubleComparator;

/**
 * Fruchterman and Reingold Force-Directed Placement Algorithm using the Barnes-Hut indexing
 * technique with a Quad-Tree.
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
public class IndexedFRLayoutAlgorithm2D<V, E>
    extends
    FRLayoutAlgorithm2D<V, E>
{
    /**
     * Default $\theta$ value for approximation using the Barnes-Hut technique
     */
    public static final double DEFAULT_THETA_FACTOR = 0.5;

    protected double theta;
    protected long savedComparisons;
    protected ToleranceDoubleComparator comparator;

    /**
     * Create a new layout algorithm
     */
    public IndexedFRLayoutAlgorithm2D()
    {
        this(DEFAULT_ITERATIONS, DEFAULT_THETA_FACTOR, DEFAULT_NORMALIZATION_FACTOR);
    }

    /**
     * Create a new layout algorithm
     * 
     * @param iterations number of iterations
     * @param theta parameter for approximation using the Barnes-Hut technique
     */
    public IndexedFRLayoutAlgorithm2D(int iterations, double theta)
    {
        this(iterations, theta, DEFAULT_NORMALIZATION_FACTOR);
    }

    /**
     * Create a new layout algorithm
     * 
     * @param iterations number of iterations
     * @param theta parameter for approximation using the Barnes-Hut technique
     * @param normalizationFactor normalization factor for the optimal distance
     */
    public IndexedFRLayoutAlgorithm2D(int iterations, double theta, double normalizationFactor)
    {
        this(iterations, theta, normalizationFactor, new Random());
    }

    /**
     * Create a new layout algorithm
     * 
     * @param iterations number of iterations
     * @param theta theta parameter for the Barnes-Hut approximation
     * @param normalizationFactor normalization factor for the optimal distance
     * @param rng the random number generator
     */
    public IndexedFRLayoutAlgorithm2D(
        int iterations, double theta, double normalizationFactor, Random rng)
    {
        this(
            iterations, theta, normalizationFactor, rng, ToleranceDoubleComparator.DEFAULT_EPSILON);
    }

    /**
     * Create a new layout algorithm
     * 
     * @param iterations number of iterations
     * @param theta theta parameter for the Barnes-Hut approximation
     * @param normalizationFactor normalization factor for the optimal distance
     * @param rng the random number generator
     * @param tolerance tolerance used when comparing floating point values
     */
    public IndexedFRLayoutAlgorithm2D(
        int iterations, double theta, double normalizationFactor, Random rng, double tolerance)
    {
        super(iterations, normalizationFactor, rng);
        this.theta = theta;
        if (theta < 0d || theta > 1d) {
            throw new IllegalArgumentException("Illegal theta value");
        }
        this.savedComparisons = 0;
        this.comparator = new ToleranceDoubleComparator(tolerance);
    }

    @Override
    public void layout(
        Graph<V, E> graph, LayoutModel<V, Double, Point2D<Double>, Box2D<Double>> model)
    {
        this.savedComparisons = 0;
        super.layout(graph, model);
    }

    @Override
    protected Map<V, Point2D<Double>> calculateRepulsiveForces(
        Graph<V, E> graph, LayoutModel<V, Double, Point2D<Double>, Box2D<Double>> model)
    {
        // index all points
        DoubleFRQuadTree quadTree = new DoubleFRQuadTree(model.getDrawableArea());
        for (V v : graph.vertexSet()) {
            quadTree.insert(model.get(v));
        }
        
        Point2D<Double> origin =
            DoublePoint2D.of(model.getDrawableArea().getMinX(), model.getDrawableArea().getMinY());

        // compute displacement with index
        Map<V, Point2D<Double>> disp = new HashMap<>();
        for (V v : graph.vertexSet()) {
            Point2D<Double> vPos = Points.sub(model.get(v), origin);
            Point2D<Double> vDisp = DoublePoint2D.of(0d, 0d);

            Deque<Node> queue = new ArrayDeque<>();
            queue.add(quadTree.getRoot());
            
            while (!queue.isEmpty()) {
                Node node = queue.removeFirst();
                Box2D<Double> box = node.getBox();
                double boxWidth = box.getWidth();

                Point2D<Double> uPos = null;
                if (node.isLeaf()) {
                    if (!node.hasPoints()) {
                        continue;
                    }
                    uPos = Points.sub(node.getPoints().iterator().next(), origin);
                } else {
                    double distanceToCentroid = Points.length(Points.sub(vPos, node.getCentroid()));
                    if (comparator.compare(distanceToCentroid, 0d) == 0) {
                        savedComparisons += node.getNumberOfPoints() - 1;
                        continue;
                    } else if (comparator.compare(boxWidth / distanceToCentroid, theta) < 0) {
                        uPos = Points.sub(node.getCentroid(), origin);
                        savedComparisons += node.getNumberOfPoints() - 1;
                    } else {
                        for (Node child : node.getChildren()) {
                            queue.add(child);
                        }
                        continue;
                    }
                }

                if (comparator.compare(vPos.getX(), uPos.getX()) != 0
                    || comparator.compare(vPos.getY(), uPos.getY()) != 0)
                {
                    Point2D<Double> delta = Points.sub(vPos, uPos);
                    double deltaLen = Points.length(delta);
                    Point2D<Double> dispContribution =
                        Points.scalarMultiply(delta, repulsiveForce(deltaLen) / deltaLen);
                    vDisp = Points.add(vDisp, dispContribution);
                }
            }

            disp.put(v, vDisp);
        }
        return disp;
    }

    /**
     * Get the total number of saved comparisons due to the Barnes-Hut technique.
     * 
     * @return the total number of saved comparisons
     */
    public long getSavedComparisons()
    {
        return savedComparisons;
    }

}