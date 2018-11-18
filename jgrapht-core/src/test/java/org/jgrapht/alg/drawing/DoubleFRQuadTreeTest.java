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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import org.jgrapht.alg.drawing.DoubleFRQuadTree.Node;
import org.jgrapht.alg.drawing.model.DoublePoint2D;
import org.jgrapht.alg.drawing.model.DoubleRectangle2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.junit.Test;

/**
 * Test {@link DoubleFRQuadTree}.
 * 
 * @author Dimitrios Michail
 */
public class DoubleFRQuadTreeTest
{

    @Test
    public void testQuadTree()
    {
        double width = 100;
        double height = 100;
        int points = 10000;
        DoubleRectangle2D region = DoubleRectangle2D.of(0, 0, width, height);
        DoubleFRQuadTree tree = new DoubleFRQuadTree(region);

        Random rng = new Random(17);

        for (int i = 0; i < points; i++) {
            DoublePoint2D p = DoublePoint2D.of(rng.nextDouble() * width, rng.nextDouble() * height);
            tree.insert(p);
        }

        Deque<Node> queue = new ArrayDeque<>();
        Node root = tree.getRoot();
        assertEquals(root.getNumberOfPoints(), points);
        queue.addLast(root);

        while (!queue.isEmpty()) {
            Node cur = queue.poll();
            if (cur.hasPoints()) {
                assertEquals(cur.getCentroid(), centroid(cur.getPoints()));
            }
            int totalPoints = cur.getNumberOfPoints();

            if (!cur.isLeaf()) {
                int childrenPoints = 0;
                for (Node c : cur.getChildren()) {
                    queue.addLast(c);
                    childrenPoints += c.getNumberOfPoints();
                }
                assertEquals(totalPoints, childrenPoints);
            }
        }

    }

    private Point2D<Double> centroid(List<Point2D<Double>> points)
    {
        double x = 0d;
        double y = 0d;
        for (Point2D<Double> p : points) {
            x += p.getX();
            y += p.getY();
        }
        int n = points.size();
        return DoublePoint2D.of(x / n, y / n);
    }

}
