
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Admin
 */
import java.awt.*; 
import javax.swing.*; 
import java.awt.geom.Line2D; 
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.graph.DefaultEdge;
public class Line<V,E> extends JComponent { 
    double[] x;
    double[] y;
    Graph<V, E> graph;
    public Line(double [] x,double[] y,Graph<V,E> graph)
    {
        this.x=x;
        this.y=y;
        this.graph=graph;
    }
       @Override
    public void paint(Graphics g) 
    { 
        int i,j=0;
        i=0;
        
        for(V source:graph.vertexSet())
        {
            g.drawOval((int)x[i]+1,(int)y[i]+1,20,20);
            g.fillOval((int)x[i]+1,(int)y[i]+1,20,20);
            g.drawString(source.toString(), (int)x[i]+1,(int)y[i]-4);
          for(V target:graph.vertexSet())
          {
            if(graph.containsEdge(source, target))
            {
                // draw and display the line 
                g.drawLine((int)x[i]+1,(int)y[i]+1,(int)x[j]+1,(int)y[j]+1); 
            }
            j++;
          }
        i++;
        j=0;
        }
     
    } 
    
}
