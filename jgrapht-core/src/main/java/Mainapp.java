import java.awt.BorderLayout;
import org.jgrapht.Graph;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jgrapht.alg.drawing.model.Point2D;
import org.jgrapht.alg.drawing.model.LayoutModel2D;
import org.jgrapht.alg.drawing.model.MapLayoutModel2D;
import org.jgrapht.alg.drawing.model.Points;
import org.jgrapht.alg.drawing.model.Box2D;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.util.SupplierUtil;
import org.jgrapht.util.VertexToIntegerMapping;
/**
 *
 * @author Admin
 */
public class Mainapp {
    public static void main(String [] args)
    {
    Box2D Db=new Box2D(500,400);
    MapLayoutModel2D<Integer> mlayoutmodel = new MapLayoutModel2D<Integer>(Db);
    SimpleDirectedGraph<Integer,DefaultEdge> sgraph = new SimpleDirectedGraph<Integer,DefaultEdge>(SupplierUtil.createIntegerSupplier(),SupplierUtil.createDefaultEdgeSupplier(),false);
   
   // Scanner input=new Scanner(System.in);
     ArrayList<String> vertices=new ArrayList<String>();
    /*System.out.println("Enter vertex name or quit to exit:");
    String korifi=input.nextLine();
    while(!korifi.equals("quit"))
    {
        vertices.add(korifi);
        System.out.println("Enter vertex name or quit to exit:");
        korifi=input.nextLine();
    }*/
    vertices.add("a");
    vertices.add("b");
    vertices.add("d");
    vertices.add("e");
   // input.close();
    VertexToIntegerMapping<String> vertexmap=new VertexToIntegerMapping<String>(vertices);
    Map<String,Integer> vmap=vertexmap.getVertexMap();
    for(String s: vmap.keySet())
    {
        System.out.println(s+","+vmap.get(s));
        sgraph.addVertex(vmap.get(s));
    }
    String source="a",target="b";
    sgraph.addEdge(vmap.get(source),vmap.get(target));
    source="a";
    target="d";
    sgraph.addEdge(vmap.get(source),vmap.get(target));
     source="b";
    target="d";
    sgraph.addEdge(vmap.get(source),vmap.get(target));
     source="a";
    target="e";
    sgraph.addEdge(vmap.get(source),vmap.get(target));
     source="b";
    target="e";
    sgraph.addEdge(vmap.get(source),vmap.get(target));
    ALGdrawing<Integer ,DefaultEdge> alg34 =new ALGdrawing<Integer ,DefaultEdge>(sgraph,mlayoutmodel,0.0002,0.01);
    JPanel p = new JPanel(new BorderLayout());
        JFrame frame = new JFrame();
        Box2D drawableArea = mlayoutmodel.getDrawableArea();
        double width = drawableArea.getWidth();
        double height = drawableArea.getHeight();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize((int) width, (int) height);
        double []x=alg34.getX();
        double []y=alg34.getY();
        frame.getContentPane().add(new Line(x, y, sgraph));
        frame.setVisible(true);

    
    }
    
}
