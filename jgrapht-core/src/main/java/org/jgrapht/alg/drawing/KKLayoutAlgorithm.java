package org.jgrapht.alg.drawing;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.SimpleGraph;
import java.util.Objects;
import java.util.Random;

/*This algorithm is based on the algorithm kamada and kawai build for graph drawing.

    This is an algorithm which successfully draws undirected graphs.The basic idea of the algorithm
    is that we consider the desirable geometric distance between two vertices in the drawing
    as the graph theoretic distance between them in the corresponding graph . This algorithm
    considers the edges between two vertices as springs. So the algorithm draws a graph
    which is the optimal layout of vertices as the state in which the total spring energy
    of the system is minimal


    @author : Elias Papadakis
 */
public class KKLayoutAlgorithm<V, E> extends
        BaseLayoutAlgorithm2D<V, E> {


    protected Random rng;
    protected double L;
    protected double K;
    protected double epsilon;
    protected double[] x;
    protected double[] y;


    /**
     * Create a new layout algorithm
     * @param sgraph the simple directed graph
     * @param mlayoutmodel the model
     * @param K constant
     * @param epsilon constant

     */
    public KKLayoutAlgorithm(SimpleGraph<V, E> sgraph, MapLayoutModel2D<V> mlayoutmodel, double K, double epsilon) {
        this(new Random(), sgraph, mlayoutmodel, K, epsilon);
    }

    /**
     * Create a new layout algorithm
     *  @param rng the random number generator
     * @param sgraph the simple directed graph
     * @param mlayoutmodel the model
     * @param K constant
     * @param epsilon constant

     */
    public KKLayoutAlgorithm(Random rng, SimpleGraph<V, E> sgraph, MapLayoutModel2D<V> mlayoutmodel, double K, double epsilon) {
        this.K = K;
        this.epsilon = epsilon;
        this.rng = Objects.requireNonNull(rng);

        executealgorithm(sgraph, mlayoutmodel);

    }

    /**
     * Create a new layout algorithm
     *
     * @param rng the random number generator
     * @param K constant
     * @param epsilon constant
     */
    public KKLayoutAlgorithm(Random rng, double K, double epsilon) {
        this.rng = Objects.requireNonNull(rng);
        this.K = K;
        this.epsilon = epsilon;
    }
    /**
     * Execute layout algorithm
     * @param sgraph the simple directed graph
     * @param mlayoutmodel the model
     */
    private void executealgorithm(SimpleGraph<V, E> sgraph, MapLayoutModel2D<V> mlayoutmodel) {
        layout(sgraph, mlayoutmodel);
    }
    /**
     * Calculate dx & dy
     *
     * @param n number of nodes
     * @param k strength of spring
     * @param l length of edge
     * @param maxm node with max Dm
     * @param E_xm derivative by xm of energy
     * @param E_ym derivative by ym of energy
     * @return pair including dx and dy values
     */
    private Pair<Double, Double> calculateDxDy(int n, double[][] k, double[][] l, int maxm, double[] E_xm, double[] E_ym) {
        double Energy2_xm = 0;
        double Energy2_ym = 0;
        double Energy2_yx = 0;
        double Energy2_xy = 0;
        double Dx, Dy;
        for (int i = 0; i < n; i++) {
            if (i != maxm) {
                Energy2_xm += k[maxm][i] * (1 - ((l[maxm][i] * (y[maxm] - y[i]) * (y[maxm] - y[i])) / (Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))))));
                Energy2_ym += k[maxm][i] * (1 - ((l[maxm][i] * (x[maxm] - x[i]) * (x[maxm] - x[i])) / (Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))))));
                Energy2_yx += k[maxm][i] * (l[maxm][i] * ((x[maxm] - x[i]) * (y[maxm] - y[i])) / (Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i])))));
                Energy2_xy += k[maxm][i] * (l[maxm][i] * ((x[maxm] - x[i]) * (y[maxm] - y[i])) / (Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i]))) * Math.sqrt((x[maxm] - x[i]) * (x[maxm] - x[i]) + ((y[maxm] - y[i]) * (y[maxm] - y[i])))));

            }
        }
        double denominator = Energy2_xm * Energy2_ym - Energy2_yx * Energy2_xy;
        Dx = (Energy2_xy * E_ym[maxm] - Energy2_ym * E_xm[maxm]) / denominator;
        Dy = (Energy2_yx * E_xm[maxm] - Energy2_xm * E_ym[maxm]) / denominator;
        Pair d;
        d = new Pair(Dx, Dy);
        return d;
    }
    /**
     * Calculate L
     *
     * @param n number of nodes
     * @param l length of edge
     * @param L0 length of a side of display square area
     * @param d shortest paths
     */
    private void calculateL(int n, double[][] l, double L0, double[][] d) {
        double maxpathweight = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (d[i][j] > maxpathweight) {
                    maxpathweight = d[i][j];
                }
            }
        }
        L = (L0 / maxpathweight) * 0.9;
        for (int i = 0; i < l.length; i++) {
            for (int j = 0; j < l[0].length; j++) {
                l[i][j] = L * d[i][j];
            }
        }
    }
    /**
     * Calculate Exm & Ey
     *
     * @param E_xm derivative by xm of energy
     * @param E_ym derivative by ym of energy
     * @param l length of edge
     * @param k strength of spring
     * @param n number of nodes
     * @param m node
     */
    private void calculateExm_Eym(double[] E_xm, double[] E_ym, double[][] l, double[][] k, int n, int m) {
        for (int i = 0; i < n; i++) {
            if (i != m) {
                E_xm[m] = E_xm[m] + (k[m][i] * ((x[m] - x[i]) - (l[m][i] * (x[m] - x[i]) / Math.sqrt((x[m] - x[i]) * (x[m] - x[i]) + (y[m] - y[i]) * (y[m] - y[i])))));
                E_ym[m] = E_ym[m] + (k[m][i] * ((y[m] - y[i]) - (l[m][i] * (y[m] - y[i]) / Math.sqrt((x[m] - x[i]) * (x[m] - x[i]) + (y[m] - y[i]) * (y[m] - y[i])))));
            }
        }
    }

    /**
     * Adjust nodes to the center of display area
     * @param width the width of display area
     * @param height the height of display area
     * @param x the x coordinate of vertices
     * @param y the y coordinate of vertices
     * @param graph the graph
     * @param model the model
     */

    public void adjustForGravity(double width, double height, double[] x, double[] y, Graph<V, E> graph, LayoutModel2D<V> model) {

        double gx = 0;
        double gy = 0;
        for (int i = 0; i < x.length; i++) {
            gx += x[i];
            gy += y[i];
        }
        gx /= x.length;
        gy /= x.length;
        double diffx = width / 2 - gx;
        double diffy = height / 2 - gy;

        for (int i = 0; i < x.length; i++) {
            x[i] = x[i] + diffx;
            y[i] = y[i] + diffy;

        }
        int i = 0;
        for (V v : graph.vertexSet()) {
            Point2D vPos = new Point2D(x[i], y[i]);

            model.put(v, vPos);
            i++;
        }
    }

    /**
     * Calculate energy
     * @param n number of nodes
     * @param d shortest paths between vertices
     * @param x x coordinate of vertices
     * @param y y coordinate of vertices
     * @return energy
     */

    private double calcEnergy(int n, double[][] d, double[] x, double[] y) {
        double energy = 0;
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                double dist = d[i][j];
                double l_ij = L * dist;
                double k_ij = K / (dist * dist);
                double dx = x[i] - x[j];
                double dy = y[i] - y[j];
                double d1 = Math.sqrt(dx * dx + dy * dy);

                energy += k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij - 2 * l_ij * d1);
            }
        }
        return energy;
    }

    /**
     * Calculates the energy function E as if positions of the specified nodes are exchanged.
     * @param p source vertex
     * @param q destination vertex
     * @param n number of nodes
     * @param d shortest paths among vertices
     * @param x x coordinate of vertices
     * @param y y coordinate of vertices
     * @return energy
     */

    private double calcEnergyIfExchanged(int p, int q, int n, double[][] d, double[] x, double[] y) {
        if (p >= q) {
            throw new RuntimeException("p should be < q");
        }
        double energy = 0; // < 0
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                int ii = i;
                int jj = j;
                if (i == p) {
                    ii = q;
                }
                if (j == q) {
                    jj = p;
                }

                double dist = d[i][j];
                double l_ij = L * dist;
                double k_ij = K / (dist * dist);
                double dx = x[ii] - y[jj];
                double dy = y[ii] - y[jj];
                double d1 = Math.sqrt(dx * dx + dy * dy);

                energy += k_ij / 2 * (dx * dx + dy * dy + l_ij * l_ij - 2 * l_ij * d1);
            }
        }
        return energy;
    }

    @Override
    /**
     * Calculate the optimal positions of vertices in display area using Kamada-Kawai layout algorithm
     *
     */
    public void layout(Graph<V, E> graph, LayoutModel2D<V> model) {

        // read area
        Box2D drawableArea = model.getDrawableArea();
        double minX = drawableArea.getMinX();
        double minY = drawableArea.getMinY();
        if (getInitializer() != null) {
            // respect user initializer
            init(graph, model);
            // make sure all vertices have coordinates
            for (V v : graph.vertexSet()) {
                Point2D vPos = model.get(v);
                if (vPos == null) {
                    model.put(v, Point2D.of(minX, minY));
                }
            }
        } else {
            // assign random initial positions
            MapLayoutModel2D<V> randomModel
                    = new MapLayoutModel2D(drawableArea);
            new RandomLayoutAlgorithm2D<V, E>(rng).layout(graph, randomModel);
            for (V v : graph.vertexSet()) {

                model.put(v, randomModel.get(v));
            }
        }
        // calculate optimal distance between vertices
        double width = drawableArea.getWidth();
        double height = drawableArea.getHeight();
        double area = width * height;
        int n = graph.vertexSet().size();
        if (n == 0) {
            return;
        }
        int i, j;
        //veltisth apostash sto graph metaksy korifwn--L

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
        if (width < height) {
            L0 = width;
        } else {
            L0 = height;
        }

        double[][] l = new double[d.length][d[0].length];
        double[][] k = new double[l.length][l[0].length];
        calculateL(n, l, L0, d);
        i = 0;
        for (V source : graph.vertexSet()) {
            j = 0;
            for (V sink : graph.vertexSet()) {
                k[i][j] = K / (spath.getPathWeight(source, sink) * spath.getPathWeight(source, sink));
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
        for (int u = 1; u <= 100000; u++) {
            max_Dm = 0;
            int maxm = -1;
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
            if (maxm == -1) break;
            if (max_Dm <= epsilon) {
                break;
            }

            for (int f = 1; f <= 100000; f++) {
                double Dy;
                double Dx;

                Pair<Double, Double> D = calculateDxDy(n, k, l, maxm, E_xm, E_ym);
                Dx = D.getFirst();
                Dy = D.getSecond();
                x[maxm] = x[maxm] + Dx;
                y[maxm] = y[maxm] + Dy;
                E_xm[maxm] = 0;
                E_ym[maxm] = 0;
                calculateExm_Eym(E_xm, E_ym, l, k, n, maxm);
                Dm = Math.sqrt(E_xm[maxm] * E_xm[maxm] + E_ym[maxm] * E_ym[maxm]);

                if (Dm < epsilon) break;
            }
            adjustForGravity(width, height, x, y, graph, model);

            double energy = calcEnergy(n, d, x, y);
        }
        i = 0;
        for (V v : graph.vertexSet()) {
            Point2D vPos = new Point2D(x[i], y[i]);
            vPos = Point2D.of(
                    Math.min(minX + width, Math.max(minX, vPos.getX())),
                    Math.min(minY + height, Math.max(minY, vPos.getY())));
            model.put(v, vPos);
            i++;
        }

    }


}