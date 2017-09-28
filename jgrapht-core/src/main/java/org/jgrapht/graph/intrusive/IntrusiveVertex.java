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
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * The vertex of the instrusive graph.
 * 
 * @author Dimitrios Michail
 */
public class IntrusiveVertex
    implements Serializable
{
    private static final long serialVersionUID = 7210539707007353478L;

    transient IntrusiveGraph graph = null;

    transient IntrusiveVertex next = null;
    transient IntrusiveVertex prev = null;

    transient IntrusiveEdge outHead = null;
    transient IntrusiveEdge outTail = null;
    transient int outEdgeCount = 0;

    transient IntrusiveEdge inHead = null;
    transient IntrusiveEdge inTail = null;
    transient int inEdgeCount = 0;

    transient IntrusiveEdge loopHead = null;
    transient IntrusiveEdge loopTail = null;
    transient int loopCount = 0;

    transient Set<IntrusiveEdge> inEdgeSet;
    transient Set<IntrusiveEdge> outEdgeSet;
    transient Set<IntrusiveEdge> inOutEdgeSet;

    /**
     * Create a new intrusive vertex.
     */
    public IntrusiveVertex()
    {
        this.inEdgeSet = new InEdgeSet();
        this.outEdgeSet = new OutEdgeSet();
        this.inOutEdgeSet = new InOutEdgeSet();
    }

    Iterator<IntrusiveEdge> getOutEdgeIterator()
    {
        return new OutEdgeItr(outHead);
    }

    Iterator<IntrusiveEdge> getInEdgeIterator()
    {
        return new InEdgeItr();
    }

    Iterator<IntrusiveEdge> getLoopEdgeIterator()
    {
        return new OutEdgeItr(loopHead);
    }

    Iterator<IntrusiveEdge> getOutLoopEdgeIterator()
    {
        return new OutLoopEdgeItr();
    }

    Iterator<IntrusiveEdge> getInAndLoopEdgeIterator()
    {
        return new InLoopEdgeItr();
    }

    Iterator<IntrusiveEdge> getOutInAndLoopEdgeIterator()
    {
        return new OutInLoopEdgeItr();
    }

    void linkOut(IntrusiveEdge e)
    {
        e.outNext = null;
        e.outPrev = outTail;
        if (outTail == null) {
            outHead = e;
            outTail = e;
        } else {
            outTail.outNext = e;
            outTail = e;
        }
        outEdgeCount++;
    }

    void unlinkOut(IntrusiveEdge e)
    {
        if (e.outPrev != null) {
            e.outPrev.outNext = e.outNext;
        }
        if (e.outNext != null) {
            e.outNext.outPrev = e.outPrev;
        }
        if (e == outHead) {
            outHead = e.outNext;
        }
        if (e == outTail) {
            outTail = e.outPrev;
        }
        outEdgeCount--;
    }

    void linkIn(IntrusiveEdge e)
    {
        e.inNext = null;
        e.inPrev = inTail;
        if (inTail == null) {
            inHead = e;
            inTail = e;
        } else {
            inTail.inNext = e;
            inTail = e;
        }
        inEdgeCount++;
    }

    void unlinkIn(IntrusiveEdge e)
    {
        if (e.inPrev != null) {
            e.inPrev.inNext = e.inNext;
        }
        if (e.inNext != null) {
            e.inNext.inPrev = e.inPrev;
        }
        if (e == inHead) {
            inHead = e.inNext;
        }
        if (e == inTail) {
            inTail = e.inPrev;
        }
        inEdgeCount--;
    }

    void linkLoop(IntrusiveEdge e)
    {
        e.outNext = null;
        e.outPrev = loopTail;
        if (loopTail == null) {
            loopHead = e;
            loopTail = e;
        } else {
            loopTail.outNext = e;
            loopTail = e;
        }
        loopCount++;
    }

    void unlinkLoop(IntrusiveEdge e)
    {
        if (e.outPrev != null) {
            e.outPrev.outNext = e.outNext;
        }
        if (e.outNext != null) {
            e.outNext.outPrev = e.outPrev;
        }
        if (e == loopHead) {
            loopHead = e.outNext;
        }
        if (e == loopTail) {
            loopTail = e.outPrev;
        }
        loopCount--;
    }

    class OutEdgeSet
        extends AbstractSet<IntrusiveEdge>
        implements Serializable
    {
        private static final long serialVersionUID = 5075611440135082305L;

        @Override
        public Iterator<IntrusiveEdge> iterator()
        {
            return new OutLoopEdgeItr();
        }

        @Override
        public int size()
        {
            return outEdgeCount + loopCount;
        }

        @Override
        public boolean contains(Object o)
        {
            if (o instanceof IntrusiveEdge) {
                IntrusiveEdge e = (IntrusiveEdge) o;
                if (graph != e.graph) {
                    return false;
                }
                return e.source == IntrusiveVertex.this;
            } else {
                return false;
            }
        }
    }

    class InEdgeSet
        extends AbstractSet<IntrusiveEdge>
        implements Serializable
    {
        private static final long serialVersionUID = 5075611440135082305L;

        @Override
        public Iterator<IntrusiveEdge> iterator()
        {
            return new InLoopEdgeItr();
        }

        @Override
        public int size()
        {
            return inEdgeCount + loopCount;
        }

        @Override
        public boolean contains(Object o)
        {
            if (o instanceof IntrusiveEdge) {
                IntrusiveEdge e = (IntrusiveEdge) o;
                if (graph != e.graph) {
                    return false;
                }
                return e.target == IntrusiveVertex.this;
            } else {
                return false;
            }
        }
    }

    class InOutEdgeSet
        extends AbstractSet<IntrusiveEdge>
        implements Serializable
    {
        private static final long serialVersionUID = 5075611440135082305L;

        @Override
        public Iterator<IntrusiveEdge> iterator()
        {
            return new OutInLoopEdgeItr();
        }

        @Override
        public int size()
        {
            return outEdgeCount + inEdgeCount + loopCount;
        }

        @Override
        public boolean contains(Object o)
        {
            if (o instanceof IntrusiveEdge) {
                IntrusiveEdge e = (IntrusiveEdge) o;
                if (graph != e.graph) {
                    return false;
                }
                return e.source == IntrusiveVertex.this || e.target == IntrusiveVertex.this;
            } else {
                return false;
            }
        }
    }

    class InLoopEdgeItr
        implements Iterator<IntrusiveEdge>
    {
        private IntrusiveEdge[] heads;
        private int curHead;
        private IntrusiveEdge it;

        /**
         * The modCount value that the iterator believes that the backing List should have. If this
         * expectation is violated, the iterator has detected concurrent modification.
         */
        int expectedModCount = graph.modCount;

        public InLoopEdgeItr()
        {
            heads = new IntrusiveEdge[2];
            heads[0] = inHead;
            heads[1] = loopHead;
            curHead = 0;
            if (heads[curHead] == null) {
                curHead++;
            }
            it = heads[curHead];
        }

        @Override
        public boolean hasNext()
        {
            return it != null;
        }

        @Override
        public IntrusiveEdge next()
        {
            checkForComodification();
            if (it == null) {
                throw new NoSuchElementException();
            }
            IntrusiveEdge v = it;
            if (curHead == 0) {
                it = it.inNext;
            } else {
                it = it.outNext;
            }
            if (it == null && curHead < heads.length - 1) {
                it = heads[++curHead];
            }
            return v;
        }

        final void checkForComodification()
        {
            if (graph.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    class OutLoopEdgeItr
        implements Iterator<IntrusiveEdge>
    {
        private IntrusiveEdge[] heads;
        private int curHead;
        private IntrusiveEdge it;

        /**
         * The modCount value that the iterator believes that the backing List should have. If this
         * expectation is violated, the iterator has detected concurrent modification.
         */
        int expectedModCount = graph.modCount;

        public OutLoopEdgeItr()
        {
            heads = new IntrusiveEdge[2];
            heads[0] = outHead;
            heads[1] = loopHead;
            curHead = 0;
            if (heads[curHead] == null) {
                curHead++;
            }
            it = heads[curHead];
        }

        @Override
        public boolean hasNext()
        {
            return it != null;
        }

        @Override
        public IntrusiveEdge next()
        {
            checkForComodification();
            if (it == null) {
                throw new NoSuchElementException();
            }
            IntrusiveEdge v = it;
            it = it.outNext;
            if (it == null && curHead < heads.length - 1) {
                it = heads[++curHead];
            }
            return v;
        }

        final void checkForComodification()
        {
            if (graph.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    class InEdgeItr
        implements Iterator<IntrusiveEdge>
    {
        private IntrusiveEdge it;

        /**
         * The modCount value that the iterator believes that the backing List should have. If this
         * expectation is violated, the iterator has detected concurrent modification.
         */
        int expectedModCount = graph.modCount;

        public InEdgeItr()
        {
            this.it = inHead;
        }

        @Override
        public boolean hasNext()
        {
            return it != null;
        }

        @Override
        public IntrusiveEdge next()
        {
            checkForComodification();
            if (it == null) {
                throw new NoSuchElementException();
            }
            IntrusiveEdge v = it;
            it = it.inNext;
            return v;
        }

        final void checkForComodification()
        {
            if (graph.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    class OutEdgeItr
        implements Iterator<IntrusiveEdge>
    {
        private IntrusiveEdge it;

        /**
         * The modCount value that the iterator believes that the backing List should have. If this
         * expectation is violated, the iterator has detected concurrent modification.
         */
        int expectedModCount = graph.modCount;

        public OutEdgeItr(IntrusiveEdge head)
        {
            this.it = head;
        }

        @Override
        public boolean hasNext()
        {
            return it != null;
        }

        @Override
        public IntrusiveEdge next()
        {
            checkForComodification();
            if (it == null) {
                throw new NoSuchElementException();
            }
            IntrusiveEdge v = it;
            it = it.outNext;
            return v;
        }

        final void checkForComodification()
        {
            if (graph.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    class OutInLoopEdgeItr
        implements Iterator<IntrusiveEdge>
    {
        private IntrusiveEdge[] heads;
        private int curHead;
        private IntrusiveEdge it;

        /**
         * The modCount value that the iterator believes that the backing List should have. If this
         * expectation is violated, the iterator has detected concurrent modification.
         */
        int expectedModCount = graph.modCount;

        public OutInLoopEdgeItr()
        {
            heads = new IntrusiveEdge[3];
            heads[0] = outHead;
            heads[1] = inHead;
            heads[2] = loopHead;
            curHead = 0;
            if (heads[curHead] == null) {
                curHead++;
            }
            if (heads[curHead] == null) {
                curHead++;
            }
            it = heads[curHead];
        }

        @Override
        public boolean hasNext()
        {
            return it != null;
        }

        @Override
        public IntrusiveEdge next()
        {
            checkForComodification();
            if (it == null) {
                throw new NoSuchElementException();
            }
            IntrusiveEdge v = it;
            switch (curHead) {
            case 0:
                it = it.outNext;
                break;
            case 1:
                it = it.inNext;
                break;
            case 2:
                it = it.outNext;
                break;
            }
            if (it == null && curHead < heads.length - 1) {
                it = heads[++curHead];
            }
            if (it == null && curHead < heads.length - 1) {
                it = heads[++curHead];
            }
            return v;
        }

        final void checkForComodification()
        {
            if (graph.modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

}
