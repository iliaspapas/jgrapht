package org.jgrapht.alg.drawing;

import java.util.Objects;
import java.util.Random;
import java.util.function.BiFunction;
import org.jgrapht.Graph;

import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;

import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.alg.shortestpath.*;
import org.jgrapht.alg.util.Pair;

/*This algorithm is based on the algorithm kamada and kawai build for graph drawing.
    
    This is an algoithm which successfuly drawes undirected graphs.The basic idea of the algorithm 
    is that we consider the desirable geometric distance between two vertices in the drawing 
    as the graph theoretic distance between them in the corresponding graph . This algorithm
    consideres the edges between two vertices as springes. So the algorithm drawes a graph 
    which is the optimal layout of vertices as the state in which the total spring energy 
    of the system is minimal
    
    
    @author : Elias Papadakis 
 */
public class KKLayoutalgorithm<V, E>
        implements LayoutAlgorithm2D<V, E> {

    /**
     * Default number of iterations
     */
    public static final int DEFAULT_ITERATIONS = 100;

    /**
     * Default normalization factor when calculating optimal distance
     */
    public static final double DEFAULT_NORMALIZATION_FACTOR = 0.5;

    protected Random rng;
    protected double L;
    protected double K;
    protected double epsilon;
    protected double normalizationFactor;
    protected int iterations;
    protected double[] x;
    protected double[] y;
    protected BiFunction<LayoutModel2D<V>, Integer, TemperatureModel> temperatureModelSupplier;

    /**
     * Create a new layout algorithm
     */
    public KKLayoutalgorithm(SimpleDirectedGraph<V, E> sgraph, MapLayoutModel2D<V> mlayoutmodel, double K, double epsilon) {
        this(DEFAULT_ITERATIONS, DEFAULT_NORMALIZATION_FACTOR, new Random(), sgraph, mlayoutmodel, K, epsilon);
    }

    /**
     * Create a new layout algorithm
     *
     * @param iterations number of iterations
     */
    public KKLayoutalgorithm(int iterations, SimpleDirectedGraph<V, E> sgraph, MapLayoutModel2D<V> mlayoutmodel, double K, double epsilon) {
        this(iterations, DEFAULT_NORMALIZATION_FACTOR, new Random(), sgraph, mlayoutmodel, K, epsilon);
    }

    /**
     * Create a new layout algorithm
     *
     * @param iterations number of iterations
     * @param normalizationFactor normalization factor for the optimal distance
     */
    public KKLayoutalgorithm(int iterations, double normalizationFactor, SimpleDirectedGraph<V, E> sgraph, MapLayoutModel2D<V> mlayoutmodel, double K, double epsilon) {
        this(iterations, normalizationFactor, new Random(), sgraph, mlayoutmodel, K, epsilon);
    }

    /**
     * Create a new layout algorithm
     *
     * @param iterations number of iterations
     * @param normalizationFactor normalization factor for the optimal distance
     * @param rng the random number generator
     */
    public KKLayoutalgorithm(int iterations, double normalizationFactor, Random rng, SimpleDirectedGraph<V, E> sgraph, MapLayoutModel2D<V> mlayoutmodel, double K, double epsilon) {
        this.K = K;
        this.epsilon = epsilon;
        this.rng = Objects.requireNonNull(rng);
        this.iterations = iterations;
        this.normalizationFactor = normalizationFactor;
        this.temperatureModelSupplier = (model, totalIterations) -> {
            double dimension
                    = Math.min(model.getDrawableArea().getWidth(), model.getDrawableArea().getHeight());
            return new InverseLinearTemperatureModel(
                    -1d * dimension / (10d * totalIterations), dimension / 10d);
        };
        executealgorithm(sgraph, mlayoutmodel);

    }

    /**
     * Create a new layout algorithm
     *
     * @param iterations number of iterations
     * @param normalizationFactor normalization factor for the optimal distance
     * @param temperatureModelSupplier a simulated annealing temperature model
     * supplier
     * @param rng the random number generators
     */
    public KKLayoutalgorithm(
            int iterations, double normalizationFactor,
            BiFunction<LayoutModel2D<V>, Integer, TemperatureModel> temperatureModelSupplier,
            Random rng, double K, double epsilon) {
        this.rng = Objects.requireNonNull(rng);
        this.iterations = iterations;
        this.normalizationFactor = normalizationFactor;
        this.temperatureModelSupplier = Objects.requireNonNull(temperatureModelSupplier);
        this.K = K;
        this.epsilon = epsilon;
    }

    private void executealgorithm(SimpleDirectedGraph<V, E> sgraph, MapLayoutModel2D<V> mlayoutmodel) {
        layout(sgraph, mlayoutmodel);
    }

    private Pair<Double,Double> calculateDxDy(int n, double[][] k, double[][] l, int maxm, double[] E_xm, double[] E_ym) {
        double E2_xm = 0;
        double E2_ym = 0;
        double E2_yx = 0;
        double E2_xy = 0;
        double Dx,Dy;
        for (int i = 0; i < n; i++) {
            if (i != maxm) {
                E2_xm += k[maxm][i] * ((1 - l[maxm][i] * (y[maxm] - y[i]) * (y[maxm] - y[i])) / (Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i])))));
                E2_ym += k[maxm][i] * ((1 - l[maxm][i] * (x[maxm] - x[i]) * (x[maxm] - x[i])) / (Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i])))));
                E2_yx += k[maxm][i] * (l[maxm][i] * ((x[maxm] - x[i]) * (y[maxm] - y[i])) / (Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i])))));
                E2_xy += k[maxm][i] * (l[maxm][i] * ((x[maxm] - x[i]) * (y[maxm] - y[i])) / (Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i])))));

            }
        }
        Dx = -((E_xm[maxm] - E2_xy * ((E_ym[maxm] * E2_xm + E2_yx * E_xm[maxm]) / (-E2_yx * E2_xy + E2_ym * E2_xm))) / E2_xm);
        Dy = ((E_ym[maxm] * E2_xm + E2_yx * E_xm[maxm]) / (-E2_yx * E2_xy + E2_ym * E2_xm));
        Pair d;
        d=new Pair(Dx,Dy);
        return d;
    }

    private void calculateL(int n, double[][] l, double L0, double[][] d) {
        double maxpathweight = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (d[i][j] > maxpathweight) {
                    maxpathweight = d[i][j];
                }
            }
        }
        L = L0 / maxpathweight;
        for (int i = 0; i < l.length; i++) {
            for (int j = 0; j < l[0].length; j++) {
                l[i][j] = L * d[i][j];
            }
        }
    }

    private void calculateExm_Eym(double[] E_xm, double[] E_ym, double[][] l, double[][] k, int n, int m) {
        for (int i = 0; i < n; i++) {
            if (i != m) {
                E_xm[m] = E_xm[m] + (k[m][i] * ((x[m] - x[i]) - (l[m][i] * (x[m] - x[i]) / Math.sqrt((x[m] - x[i]) * (x[m] - x[i]) + (y[m] - y[i]) * (y[m] - y[i])))));
                E_ym[m] = E_ym[m] + (k[m][i] * ((y[m] - y[i]) - (l[m][i] * (y[m] - y[i]) / Math.sqrt((x[m] - x[i]) * (x[m] - x[i]) + (y[m] - y[i]) * (y[m] - y[i])))));
            }
        }
    }

    @Override
    public void layout(Graph<V, E> graph, LayoutModel2D<V> model) {

        // read area
        Box2D drawableArea = model.getDrawableArea();
        double minX = drawableArea.getMinX();
        double minY = drawableArea.getMinY();
        System.out.println("drawable " + minX + "," + minY);

        // assign random initial positions
        MapLayoutModel2D<V> randomModel
                = new MapLayoutModel2D(drawableArea);
        new RandomLayoutAlgorithm2D<V, E>(rng).layout(graph, randomModel);
        for (V v : graph.vertexSet()) {
            System.out.println("v" + v);

            model.put(v, randomModel.get(v));
        }

        // calculate optimal distance between vertices
        double width = drawableArea.getWidth();
        double height = drawableArea.getHeight();
        double area = width * height;
        int n = graph.vertexSet().size();
        if (n == 0) {
            return;
        }
        System.out.println("number vertices " + n);
        for (E v : graph.edgeSet()) {
            System.out.println("Edge:" + v);

        }
        int i, j;
        //veltisth apostash sto graph metaksy korifwn--L 
        // L = normalizationFactor * Math.sqrt(area / n);
        FloydWarshallShortestPaths spath = new FloydWarshallShortestPaths(graph);
        double[][] d = new double[n][n];
        i = 0;
        for (V source : graph.vertexSet()) {
            j = 0;
            for (V sink : graph.vertexSet()) {
                d[i][j] = spath.getPathWeight(source, sink);
                j++;
            }
            i++;
        }
        double L0;
        if (width > height) {
            L0 = width; //L0=max(width,height)
        } else {
            L0 = height;
        }
        //L=L0/maxpath

        double[][] l = new double[d.length][d[0].length];
        double[][] k = new double[l.length][l[0].length];
        calculateL(n, l, L0, d);
        i = 0;
        for (V source : graph.vertexSet()) {
            j = 0;
            for (V sink : graph.vertexSet()) {
                k[i][j] = K / spath.getPathWeight(source, sink);
                j++;
            }
            i++;
        }

        i = 0;
        j = 0;
        x = new double[n];
        y = new double[n];
        for (V v : graph.vertexSet()) {
            Point2D vPos = model.get(v);
            x[i++] = (double) vPos.getX();
            y[j++] = (double) vPos.getY();
        }

        double[] E_xm = new double[n];
        double[] E_ym = new double[n];

        double Dm, max_Dm;

        while (true) {
            max_Dm = 0;
            int maxm = 0;
            //compute max_dm
            for (int m = 0; m < n; m++) {
                E_xm[m] = 0;
                E_ym[m] = 0;
                calculateExm_Eym(E_xm, E_ym, l, k, n, m);

                Dm = Math.sqrt(E_xm[m] * E_xm[m] + E_ym[m] * E_ym[m]);
                if (Dm > max_Dm) {
                    max_Dm = Dm;
                    maxm = m;
                }

            }
            while (max_Dm > epsilon) {
                double Dy;
                double Dx;
                
                Pair<Double,Double> D=calculateDxDy(n, k, l, maxm, E_xm, E_ym);
                Dx = D.getFirst();
                Dy = D.getSecond();
                x[maxm] = x[maxm] + Dx;
                y[maxm] = y[maxm] + Dy;

                E_xm[maxm] = 0;
                E_ym[maxm] = 0;
                calculateExm_Eym(E_xm, E_ym, l, k, n, maxm);
                Dm = Math.sqrt(E_xm[maxm] * E_xm[maxm] + E_ym[maxm] * E_ym[maxm]);
                max_Dm = Dm;

            }

            if (max_Dm <= epsilon) {
                break;
            }
        }
        i = 0;
        for (V v : graph.vertexSet()) {
            Point2D vPos = new Point2D(x[i], y[i]);
            model.put(v, vPos);
            i++;
        }

    }

    /**
     * A general interface for a temperature model.
     *
     * <p>
     * The temperature should start from a high enough value and gradually
     * become zero.
     */
    public interface TemperatureModel {

        /**
         * Return the temperature for the new iteration
         *
         * @param iteration the next iteration
         * @param maxIterations total number of iterations
         * @return the temperature for the next iteration
         */
        double temperature(int iteration, int maxIterations);

    }

    /**
     * An inverse linear temperature model.
     */
    protected class InverseLinearTemperatureModel
            implements
            TemperatureModel {

        private double a;
        private double b;

        /**
         * Create a new inverse linear temperature model.
         *
         * @param a a
         * @param b b
         */
        public InverseLinearTemperatureModel(double a, double b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public double temperature(int iteration, int maxIterations) {
            if (iteration >= maxIterations - 1) {
                return 0.0;
            }
            return a * iteration + b;
        }

    }

}
