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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.GraphType;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.alg.util.UnorderedPair;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.util.FilteredSet;
import org.jgrapht.util.TypeUtil;

/**
 * An intrusive graph.
 * 
 * <p>
 * An intrusive data structure one where the application elements contain themselves the necessary
 * references in order to be stored inside the data structure. This means that in order to add a
 * custom vertex or edge type one has to extend the {@link IntrusiveVertex} or the
 * {@link IntrusiveEdge} types.
 * 
 * @author Dimitrios Michail
 */
public final class IntrusiveGraph
    implements Graph<IntrusiveVertex, IntrusiveEdge>, Serializable
{
    private static final long serialVersionUID = 7755509570015945114L;

    private static final boolean WEIGHTED = true;
    private static final boolean ALLOWING_MULTIPLE_EDGES = true;
    private static final boolean ALLOWING_LOOPS = true;

    EdgeFactory<IntrusiveVertex, IntrusiveEdge> ef;

    transient IntrusiveVertex vHead = null;
    transient IntrusiveVertex vTail = null;

    transient int verticesCount = 0;
    transient int edgesCount = 0;

    transient Set<IntrusiveVertex> vertices = null;
    transient Set<IntrusiveEdge> edges = null;

    transient boolean directed;
    transient Map<Pair<IntrusiveVertex, IntrusiveVertex>, Set<IntrusiveEdge>> extraIndex;

    /**
     * The number of times this list has been <i>structurally modified</i>. Structural modifications
     * are those that change the size of the list, or otherwise perturb it in such a fashion that
     * iterations in progress may yield incorrect results.
     *
     * <p>
     * This field is used by the all iterator implementations. If the value of this field changes
     * unexpectedly, the iterator will throw a {@code ConcurrentModificationException} in response
     * to the {@code next} operation. This provides <i>fail-fast</i> behavior, rather than
     * non-deterministic behavior in the face of concurrent modification during iteration.
     */
    transient int modCount = 0;

    /**
     * Construct an intrusive graph
     * 
     * @param ef the edge factory
     * @param directed if true the graph will be directed, if false undirected
     */
    public IntrusiveGraph(EdgeFactory<IntrusiveVertex, IntrusiveEdge> ef, boolean directed)
    {
        this(ef, directed, false);
    }

    /**
     * Construct an intrusive graph
     * 
     * @param ef the edge factory
     * @param directed if true the graph will be directed, if false undirected
     * @param maintainVertexPairIndex whether to maintain an additional index for edge lookups using
     *        vertex pairs.
     */
    public IntrusiveGraph(
        EdgeFactory<IntrusiveVertex, IntrusiveEdge> ef, boolean directed,
        boolean maintainVertexPairIndex)
    {
        this.ef = ef;
        this.directed = directed;
        this.extraIndex = maintainVertexPairIndex ? new HashMap<>() : null;
        this.edges = new EdgeSet();
        this.vertices = new VertexSet();
    }

    /**
     * Construct an intrusive graph
     * 
     * @param edgeClass the edge class
     * @param directed if true the graph will be directed, if false undirected
     */
    public IntrusiveGraph(Class<? extends IntrusiveEdge> edgeClass, boolean directed)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass), directed, false);
    }

    /**
     * Construct an intrusive graph
     * 
     * @param edgeClass the edge class
     * @param directed if true the graph will be directed, if false undirected
     * @param maintainVertexPairIndex whether to maintain an additional index for edge lookups using
     *        vertex pairs.
     */
    public IntrusiveGraph(
        Class<? extends IntrusiveEdge> edgeClass, boolean directed, boolean maintainVertexPairIndex)
    {
        this(new ClassBasedEdgeFactory<>(edgeClass), directed, maintainVertexPairIndex);
    }

    @Override
    public EdgeFactory<IntrusiveVertex, IntrusiveEdge> getEdgeFactory()
    {
        return ef;
    }

    @Override
    public IntrusiveEdge addEdge(IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex)
    {
        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        IntrusiveEdge e = ef.createEdge(sourceVertex, targetVertex);
        linkEdge(sourceVertex, targetVertex, e);

        if (extraIndex != null) {
            addEdgeToExtraIndex(sourceVertex, targetVertex, e);
        }

        modCount++;
        return e;
    }

    @Override
    public boolean addEdge(
        IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex, IntrusiveEdge e)
    {
        if (this == e.graph) {
            return false;
        }
        assertVertexExist(sourceVertex);
        assertVertexExist(targetVertex);

        if (e.graph != null) {
            throw new IllegalArgumentException("Edge belongs to another graph.");
        }

        linkEdge(sourceVertex, targetVertex, e);

        if (extraIndex != null) {
            addEdgeToExtraIndex(sourceVertex, targetVertex, e);
        }

        modCount++;
        return true;
    }

    @Override
    public boolean addVertex(IntrusiveVertex v)
    {
        if (this == v.graph) {
            return false;
        }
        if (v.graph != null) {
            throw new IllegalArgumentException("Vertex belongs to another graph.");
        }

        linkVertex(v);

        modCount++;
        return true;
    }

    @Override
    public boolean containsEdge(IntrusiveEdge e)
    {
        if (e == null) {
            return false;
        }
        return this == e.graph;
    }

    @Override
    public boolean containsVertex(IntrusiveVertex v)
    {
        if (v == null) {
            return false;
        }
        return this == v.graph;
    }

    @Override
    public Set<IntrusiveEdge> edgesOf(IntrusiveVertex vertex)
    {
        return vertex.inOutEdgeSet;
    }

    @Override
    public IntrusiveVertex getEdgeSource(IntrusiveEdge e)
    {
        return e.source;
    }

    @Override
    public IntrusiveVertex getEdgeTarget(IntrusiveEdge e)
    {
        return e.target;
    }

    @Override
    public double getEdgeWeight(IntrusiveEdge e)
    {
        return e.weight;
    }

    @Override
    public Set<IntrusiveEdge> getAllEdges(
        IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex)
    {
        if (!containsVertex(sourceVertex) || !containsVertex(targetVertex)) {
            return null;
        }

        if (extraIndex != null) {
            return Collections.unmodifiableSet(getEdgesFromExtraIndex(sourceVertex, targetVertex));
        } else {
            Set<IntrusiveEdge> sec = directed ? sourceVertex.outEdgeSet : sourceVertex.inOutEdgeSet;
            Set<IntrusiveEdge> tec = directed ? targetVertex.inEdgeSet : targetVertex.inOutEdgeSet;

            // lookup from smaller adjacency list
            if (sec.size() <= tec.size()) {
                return new FilteredSet<>(sec, e -> e.getOpposite(sourceVertex) == targetVertex);
            } else {
                return new FilteredSet<>(tec, e -> e.getOpposite(targetVertex) == sourceVertex);
            }
        }
    }

    @Override
    public IntrusiveEdge getEdge(IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex)
    {
        if (containsVertex(sourceVertex) && containsVertex(targetVertex)) {
            if (extraIndex != null) {
                return this
                    .getEdgesFromExtraIndex(sourceVertex, targetVertex).stream().findFirst()
                    .orElse(null);
            } else {
                Set<IntrusiveEdge> sec =
                    directed ? sourceVertex.outEdgeSet : sourceVertex.inOutEdgeSet;
                Set<IntrusiveEdge> tec =
                    directed ? targetVertex.inEdgeSet : targetVertex.inOutEdgeSet;

                if (sec.size() <= tec.size()) {
                    return sec
                        .stream().filter(e -> isEdgeEqual(sourceVertex, targetVertex, e))
                        .findFirst().orElse(null);
                } else {
                    return tec
                        .stream().filter(e -> isEdgeEqual(sourceVertex, targetVertex, e))
                        .findFirst().orElse(null);
                }
            }
        }
        return null;
    }

    @Override
    public IntrusiveEdge removeEdge(IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex)
    {
        IntrusiveEdge e = getEdge(sourceVertex, targetVertex);
        if (e != null) {
            unlinkEdge(e);
        }

        if (extraIndex != null) {
            removeEdgeFromExtraIndex(e);
        }

        modCount++;
        return e;
    }

    @Override
    public boolean removeEdge(IntrusiveEdge e)
    {
        if (e == null) {
            return false;
        }
        if (this != e.graph) {
            return false;
        }

        unlinkEdge(e);

        if (extraIndex != null) {
            removeEdgeFromExtraIndex(e);
        }

        modCount++;
        return true;
    }

    @Override
    public boolean removeVertex(IntrusiveVertex v)
    {
        if (v == null) {
            return false;
        }
        if (this != v.graph) {
            return false;
        }

        // beware of ConcurrentModificationException
        removeAllEdges(new ArrayList<>(edgesOf(v)));
        unlinkVertex(v);

        modCount++;
        return true;
    }

    @Override
    public boolean containsEdge(IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex)
    {
        return getEdge(sourceVertex, targetVertex) != null;
    }

    @Override
    public boolean removeAllEdges(Collection<? extends IntrusiveEdge> edges)
    {
        boolean modified = false;

        for (IntrusiveEdge e : edges) {
            modified |= removeEdge(e);
        }

        return modified;
    }

    @Override
    public Set<IntrusiveEdge> removeAllEdges(
        IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex)
    {
        Set<IntrusiveEdge> removed = getAllEdges(sourceVertex, targetVertex);
        if (removed == null) {
            return null;
        }
        removeAllEdges(removed);

        return removed;
    }

    @Override
    public boolean removeAllVertices(Collection<? extends IntrusiveVertex> vertices)
    {
        boolean modified = false;

        for (IntrusiveVertex v : vertices) {
            modified |= removeVertex(v);
        }

        return modified;
    }

    @Override
    public Set<IntrusiveVertex> vertexSet()
    {
        return vertices;
    }

    @Override
    public Set<IntrusiveEdge> edgeSet()
    {
        return edges;
    }

    @Override
    public int degreeOf(IntrusiveVertex vertex)
    {
        assertVertexExist(vertex);
        return vertex.inEdgeCount + vertex.outEdgeCount + 2 * vertex.loopCount;
    }

    @Override
    public int inDegreeOf(IntrusiveVertex vertex)
    {
        assertVertexExist(vertex);
        if (directed) {
            return vertex.inEdgeCount + vertex.loopCount;
        } else {
            return vertex.inEdgeCount + vertex.outEdgeCount + 2 * vertex.loopCount;
        }
    }

    @Override
    public Set<IntrusiveEdge> incomingEdgesOf(IntrusiveVertex vertex)
    {
        assertVertexExist(vertex);
        if (directed) {
            return vertex.inEdgeSet;
        } else {
            return vertex.inOutEdgeSet;
        }
    }

    @Override
    public int outDegreeOf(IntrusiveVertex vertex)
    {
        assertVertexExist(vertex);
        if (directed) {
            return vertex.outEdgeCount + vertex.loopCount;
        } else {
            return vertex.inEdgeCount + vertex.outEdgeCount + 2 * vertex.loopCount;
        }
    }

    @Override
    public Set<IntrusiveEdge> outgoingEdgesOf(IntrusiveVertex vertex)
    {
        assertVertexExist(vertex);
        if (directed) {
            return vertex.outEdgeSet;
        } else {
            return vertex.inOutEdgeSet;
        }
    }

    @Override
    public void setEdgeWeight(IntrusiveEdge e, double weight)
    {
        e.weight = weight;
    }

    @Override
    public GraphType getType()
    {
        if (directed) {
            return new DefaultGraphType.Builder()
                .directed().weighted(WEIGHTED).allowMultipleEdges(ALLOWING_MULTIPLE_EDGES)
                .allowSelfLoops(ALLOWING_LOOPS).build();
        } else {
            return new DefaultGraphType.Builder()
                .undirected().weighted(WEIGHTED).allowMultipleEdges(ALLOWING_MULTIPLE_EDGES)
                .allowSelfLoops(ALLOWING_LOOPS).build();
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append('[');
        Iterator<IntrusiveVertex> vit = vertexSet().iterator();
        while (vit.hasNext()) {
            sb.append(vit.next().toString());
            if (vit.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        sb.append(']').append(',').append(' ').append('[');

        Iterator<IntrusiveEdge> eit = edgeSet().iterator();
        while (eit.hasNext()) {
            IntrusiveEdge e = eit.next();
            sb.append(e.toString());
            sb.append("=");
            if (directed) {
                sb.append("(");
            } else {
                sb.append("{");
            }
            sb.append(getEdgeSource(e));
            sb.append(",");
            sb.append(getEdgeTarget(e));
            if (directed) {
                sb.append(")");
            } else {
                sb.append("}");
            }
            if (eit.hasNext()) {
                sb.append(',').append(' ');
            }
        }
        sb.append(']').append(')');
        return sb.toString();
    }

    /**
     * Link a vertex into the list of vertices.
     * 
     * @param v the vertex
     */
    protected void linkVertex(IntrusiveVertex v)
    {
        v.graph = this;
        v.next = null;
        v.prev = vTail;
        if (vTail != null) {
            vTail.next = v;
        } else {
            vHead = v;
        }
        vTail = v;
        verticesCount++;
    }

    /**
     * Unlink a vertex from the list of vertices.
     * 
     * @param v the vertex
     */
    protected void unlinkVertex(IntrusiveVertex v)
    {
        while (v.outHead != null) {
            unlinkEdge(v.outHead);
        }
        while (v.inHead != null) {
            unlinkEdge(v.inHead);
        }

        if (v.prev != null) {
            v.prev.next = v.next;
        }
        if (v.next != null) {
            v.next.prev = v.prev;
        }
        if (v == vHead) {
            vHead = v.next;
        }
        if (v == vTail) {
            vTail = v.prev;
        }
        verticesCount--;

        v.graph = null;
    }

    /**
     * Link an edge into the adjacency lists of its end-points.
     * 
     * @param source the source vertex
     * @param target the target vertex
     * @param e the edge
     */
    protected void linkEdge(IntrusiveVertex source, IntrusiveVertex target, IntrusiveEdge e)
    {
        e.graph = this;
        e.source = source;
        e.target = target;

        if (source == target) { // loop
            source.linkLoop(e);
        } else {
            source.linkOut(e);
            target.linkIn(e);
        }

        edgesCount++;
    }

    /**
     * Unlink an edge from the adjacency lists of its end-points
     * 
     * @param e the edge
     */
    protected void unlinkEdge(IntrusiveEdge e)
    {
        IntrusiveVertex source = e.source;
        IntrusiveVertex target = e.target;

        if (source == target) { // loops
            source.unlinkLoop(e);
        } else {
            source.unlinkOut(e);
            target.unlinkIn(e);
        }

        e.source = null;
        e.target = null;
        e.graph = null;

        edgesCount--;
    }

    /**
     * Ensures that the specified vertex exists in this graph, or else throws exception.
     *
     * @param v vertex
     *
     * @return <code>true</code> if this assertion holds.
     *
     * @throws NullPointerException if specified vertex is <code>null</code>.
     * @throws IllegalArgumentException if specified vertex does not exist in this graph.
     */
    protected boolean assertVertexExist(IntrusiveVertex v)
    {
        if (containsVertex(v)) {
            return true;
        } else if (v == null) {
            throw new NullPointerException();
        } else {
            throw new IllegalArgumentException("no such vertex in graph: " + v.toString());
        }
    }

    /**
     * Check if an edge is between certain endpoints, taking into account whether the graph is
     * directed or not.
     * 
     * @param source the source vertex
     * @param target the target vertex
     * @param e the edge
     * @return true if the edge is between the given end-points taking into account whether the
     *         graph is directed or not, false otherwise.
     */
    protected boolean isEdgeEqual(IntrusiveVertex source, IntrusiveVertex target, IntrusiveEdge e)
    {
        if (directed) {
            return e.source == source && e.target == target;
        } else {
            return e.source == source && e.target == target
                || e.source == target && e.target == source;
        }
    }

    private void addEdgeToExtraIndex(
        IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex, IntrusiveEdge e)
    {
        Pair<IntrusiveVertex, IntrusiveVertex> p = directed ? Pair.of(sourceVertex, targetVertex)
            : UnorderedPair.of(sourceVertex, targetVertex);
        Set<IntrusiveEdge> edges = extraIndex.get(p);
        if (edges == null) {
            edges = new LinkedHashSet<>();
            extraIndex.put(p, edges);
        }
        edges.add(e);
    }

    private void removeEdgeFromExtraIndex(IntrusiveEdge e)
    {
        Pair<IntrusiveVertex, IntrusiveVertex> p =
            directed ? Pair.of(e.source, e.target) : UnorderedPair.of(e.source, e.target);
        Set<IntrusiveEdge> edges = extraIndex.get(p);
        if (edges != null) {
            edges.remove(e);
        }
    }

    private Set<IntrusiveEdge> getEdgesFromExtraIndex(
        IntrusiveVertex sourceVertex, IntrusiveVertex targetVertex)
    {
        Pair<IntrusiveVertex, IntrusiveVertex> p = directed ? Pair.of(sourceVertex, targetVertex)
            : UnorderedPair.of(sourceVertex, targetVertex);
        Set<IntrusiveEdge> edges = extraIndex.get(p);
        if (edges == null) {
            edges = new LinkedHashSet<>();
            extraIndex.put(p, edges);
        }
        return edges;
    }

    /**
     * Saves the state of this {@code IntrusiveGraph} instance to a stream (that is, serializes it).
     * 
     * @param s the output stream
     * @throws IOException in case of an I/O error
     */
    private void writeObject(ObjectOutputStream s)
        throws IOException
    {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write directed or not
        s.writeBoolean(directed);
        // Write whether to maintain extra index
        s.writeBoolean(extraIndex != null);

        // Write vertices
        s.writeInt(verticesCount);
        for (IntrusiveVertex v : vertexSet()) {
            s.writeObject(v);
        }

        // Write edges
        s.writeInt(edgesCount);
        for (IntrusiveEdge e : edgeSet()) {
            s.writeObject(e.source);
            s.writeObject(e.target);
            if (Double.compare(e.weight, Graph.DEFAULT_EDGE_WEIGHT) == 0) {
                s.writeBoolean(false);
            } else {
                s.writeBoolean(true);
                s.writeDouble(e.weight);
            }
        }
    }

    /**
     * Reconstitutes this {@code IntrusiveGraph} instance from a stream (that is, deserializes it).
     * 
     * @param s the input stream
     * @throws IOException in case of an I/O error
     * @throws ClassNotFoundException in case the stream contains some unknown class
     */
    private void readObject(ObjectInputStream s)
        throws IOException, ClassNotFoundException
    {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read directed or not
        this.directed = s.readBoolean();
        // Read whether to maintain extra index
        this.extraIndex = s.readBoolean() ? new HashMap<>() : null;
        this.edges = new EdgeSet();
        this.vertices = new VertexSet();

        // Read vertices
        int totalVertices = s.readInt();
        for (int i = 0; i < totalVertices; i++) {
            linkVertex((IntrusiveVertex) s.readObject());
        }

        // Read edges
        int totalEdges = s.readInt();
        for (int i = 0; i < totalEdges; i++) {
            IntrusiveVertex source = (IntrusiveVertex) s.readObject();
            IntrusiveVertex target = (IntrusiveVertex) s.readObject();
            IntrusiveEdge e = ef.createEdge(source, target);
            if (s.readBoolean()) {
                e.weight = s.readDouble();
            }
            addEdge(source, target, e);
        }
    }

    class VertexSet
        extends AbstractSet<IntrusiveVertex>
        implements Serializable
    {
        private static final long serialVersionUID = 5075611440135082305L;

        @Override
        public Iterator<IntrusiveVertex> iterator()
        {
            return new VertexItr();
        }

        @Override
        public int size()
        {
            return verticesCount;
        }

        @Override
        public boolean contains(Object o)
        {
            return containsVertex(TypeUtil.uncheckedCast(o, null));
        }
    }

    class EdgeSet
        extends AbstractSet<IntrusiveEdge>
        implements Serializable
    {
        private static final long serialVersionUID = 1187589595498823817L;

        @Override
        public Iterator<IntrusiveEdge> iterator()
        {
            return new EdgeItr();
        }

        @Override
        public int size()
        {
            return edgesCount;
        }

        @Override
        public boolean contains(Object o)
        {
            return containsEdge(TypeUtil.uncheckedCast(o, null));
        }
    }

    class VertexItr
        implements Iterator<IntrusiveVertex>
    {
        private IntrusiveVertex it;

        /**
         * The modCount value that the iterator believes that the backing List should have. If this
         * expectation is violated, the iterator has detected concurrent modification.
         */
        int expectedModCount = modCount;

        public VertexItr()
        {
            this.it = vHead;
        }

        @Override
        public boolean hasNext()
        {
            return it != null;
        }

        @Override
        public IntrusiveVertex next()
        {
            checkForComodification();
            IntrusiveVertex v = it;
            it = it.next;
            return v;
        }

        final void checkForComodification()
        {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    class EdgeItr
        implements Iterator<IntrusiveEdge>
    {
        private Iterator<IntrusiveVertex> vIt;
        private Iterator<IntrusiveEdge> eIt;

        /**
         * The modCount value that the iterator believes that the backing List should have. If this
         * expectation is violated, the iterator has detected concurrent modification.
         */
        int expectedModCount = modCount;

        public EdgeItr()
        {
            this.vIt = new VertexItr();
            this.eIt = null;
        }

        @Override
        public boolean hasNext()
        {
            while (true) {
                if (eIt != null && eIt.hasNext()) {
                    return true;
                }
                if (!vIt.hasNext()) {
                    return false;
                }
                eIt = vIt.next().getOutLoopEdgeIterator();
            }
        }

        @Override
        public IntrusiveEdge next()
        {
            checkForComodification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return eIt.next();
        }

        final void checkForComodification()
        {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

}
