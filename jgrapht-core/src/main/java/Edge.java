import org.jgrapht.graph.DefaultEdge;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
public class Edge <V> extends DefaultEdge{
    protected V source;
    protected V destination;
     protected int weight;
    public Edge(V s,V d){
        source=s;
        destination=d;
        weight=1;
    }
    public V getsource(){
        return source;
    }

    public V getDestination() {
        return destination;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
   
    
    
}
