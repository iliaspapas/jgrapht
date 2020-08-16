package org.jgrapht.alg.drawing;








import java.util.Map;


import org.jgrapht.alg.drawing.model.Point2D;

import org.jgrapht.alg.drawing.model.MapLayoutModel2D;

import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.util.SupplierUtil;

import static org.junit.Assert.assertTrue;

/**
 * Test {@link KKLayoutAlgorithm}
 * @author : Elias Papadakis
 */
public class KKLayoutAlgorithmTest {

    public void testA() {
        Box2D Db = new Box2D(500, 400);
        MapLayoutModel2D<Integer> mlayoutmodel = new MapLayoutModel2D<Integer>(Db);
        SimpleGraph<Integer, DefaultEdge> sgraph = new SimpleGraph<Integer, DefaultEdge>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        sgraph.addVertex(1);
        sgraph.addVertex(2);
        sgraph.addVertex(3);
        sgraph.addVertex(4);
        sgraph.addVertex(5);
        sgraph.addVertex(6);
        sgraph.addEdge(1,2);
        sgraph.addEdge(1,5);
        sgraph.addEdge(2,5);
        sgraph.addEdge(2,4);
        sgraph.addEdge(3,4);
        sgraph.addEdge(3,6);
        sgraph.addEdge(4,6);
        KKLayoutAlgorithm<Integer, DefaultEdge> alg34 = new KKLayoutAlgorithm<Integer, DefaultEdge>(sgraph, mlayoutmodel, 0.0002, 0.01);
        Map<Integer, Point2D> result = mlayoutmodel.collect();

        assertTrue(result.get(1).getX() > result.get(2).getX());
        assertTrue(result.get(1).getY() < result.get(2).getY());

        assertTrue(result.get(1).getX() > result.get(5).getX());
        assertTrue(result.get(1).getY() > result.get(5).getY());

        assertTrue(result.get(2).getX() > result.get(5).getX());
        assertTrue(result.get(2).getY() > result.get(5).getY());

        assertTrue(result.get(2).getX() > result.get(4).getX());
        assertTrue(result.get(2).getY() < result.get(4).getY());

        assertTrue(result.get(3).getX() > result.get(4).getX());
        assertTrue(result.get(3).getY() > result.get(4).getY());

        assertTrue(result.get(3).getX() < result.get(6).getX());
        assertTrue(result.get(3).getY() > result.get(6).getY());

        assertTrue(result.get(4).getX() < result.get(6).getX());
        assertTrue(result.get(4).getY() > result.get(6).getY());


    }





    public void testB() {
        Box2D Db = new Box2D(500, 400);
        MapLayoutModel2D<Integer> mlayoutmodel = new MapLayoutModel2D<Integer>(Db);
        SimpleGraph<Integer, DefaultEdge> sgraph = new SimpleGraph<Integer, DefaultEdge>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        sgraph.addVertex(1);
        sgraph.addVertex(2);
        sgraph.addVertex(3);
        sgraph.addVertex(4);
        sgraph.addVertex(5);
        sgraph.addVertex(6);
        sgraph.addVertex(7);
        sgraph.addVertex(8);
        sgraph.addEdge(1,2);
        sgraph.addEdge(1,4);
        sgraph.addEdge(1,5);
        sgraph.addEdge(2,6);
        sgraph.addEdge(2,3);
        sgraph.addEdge(3,4);
        sgraph.addEdge(3,7);
        sgraph.addEdge(4,8);
        sgraph.addEdge(5,8);
        sgraph.addEdge(5,6);
        sgraph.addEdge(6,7);
        sgraph.addEdge(7,8);
        KKLayoutAlgorithm<Integer, DefaultEdge> alg34 = new KKLayoutAlgorithm<Integer, DefaultEdge>(sgraph, mlayoutmodel, 0.0002, 0.01);
        Map<Integer, Point2D> result = mlayoutmodel.collect();

        assertTrue(result.get(1).getX() > result.get(2).getX());
        assertTrue(result.get(1).getY() < result.get(2).getY());

        assertTrue(result.get(1).getX() > result.get(5).getX());
        assertTrue(result.get(1).getY() > result.get(5).getY());

        assertTrue(result.get(2).getX() > result.get(5).getX());
        assertTrue(result.get(2).getY() > result.get(5).getY());

        assertTrue(result.get(2).getX() > result.get(4).getX());
        assertTrue(result.get(2).getY() < result.get(4).getY());

        assertTrue(result.get(3).getX() > result.get(4).getX());
        assertTrue(result.get(3).getY() > result.get(4).getY());

        assertTrue(result.get(3).getX() < result.get(6).getX());
        assertTrue(result.get(3).getY() > result.get(6).getY());

        assertTrue(result.get(4).getX() < result.get(6).getX());
        assertTrue(result.get(4).getY() > result.get(6).getY());


    }
    public void testC() {
        Box2D Db = new Box2D(500, 400);
        MapLayoutModel2D<Integer> mlayoutmodel = new MapLayoutModel2D<Integer>(Db);
        SimpleGraph<Integer, DefaultEdge> sgraph = new SimpleGraph<Integer, DefaultEdge>(SupplierUtil.createIntegerSupplier(), SupplierUtil.createDefaultEdgeSupplier(), false);
        sgraph.addVertex(1);
        sgraph.addVertex(2);
        sgraph.addVertex(3);
        sgraph.addVertex(4);
        sgraph.addVertex(5);
        sgraph.addVertex(6);
        sgraph.addVertex(7);
        sgraph.addVertex(8);
        sgraph.addVertex(9);
        sgraph.addVertex(10);
        sgraph.addEdge(1,2);
        sgraph.addEdge(1,6);
        sgraph.addEdge(1,5);
        sgraph.addEdge(2,3);
        sgraph.addEdge(2,7);
        sgraph.addEdge(3,9);
        sgraph.addEdge(3,10);
        sgraph.addEdge(3,4);
        sgraph.addEdge(4,5);
        sgraph.addEdge(4,8);
        sgraph.addEdge(5,8);
        sgraph.addEdge(6,7);
        sgraph.addEdge(9,10);
        KKLayoutAlgorithm<Integer, DefaultEdge> alg34 = new KKLayoutAlgorithm<Integer, DefaultEdge>(sgraph, mlayoutmodel, 0.0002, 0.01);
        Map<Integer, Point2D> result = mlayoutmodel.collect();

        assertTrue(result.get(1).getX() > result.get(2).getX());
        assertTrue(result.get(1).getY() < result.get(2).getY());

        assertTrue(result.get(1).getX() > result.get(5).getX());
        assertTrue(result.get(1).getY() > result.get(5).getY());

        assertTrue(result.get(2).getX() > result.get(5).getX());
        assertTrue(result.get(2).getY() > result.get(5).getY());

        assertTrue(result.get(2).getX() > result.get(4).getX());
        assertTrue(result.get(2).getY() < result.get(4).getY());

        assertTrue(result.get(3).getX() > result.get(4).getX());
        assertTrue(result.get(3).getY() > result.get(4).getY());

        assertTrue(result.get(3).getX() < result.get(6).getX());
        assertTrue(result.get(3).getY() > result.get(6).getY());

        assertTrue(result.get(4).getX() < result.get(6).getX());
        assertTrue(result.get(4).getY() > result.get(6).getY());


    }
}


