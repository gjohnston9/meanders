import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JComponent; 
import javax.swing.JFrame;

// assists in drawing a PerfectMatching and Meander
@SuppressWarnings("serial")
public class DrawFrame extends JComponent {
		
	static int height = 500;
	static int xOffset = 0;
	static int yOffset = 0;
	
	double[][][] ellipsesArrays = new double[2][][]; // list of ellipses for top ([0][..][..]) and bottom ([1][..][..])
	int[][][] arcsArrays = new int [2][][]; // list of arcs to draw for top ([0][..][..]) and bottom ([1][..][..])
	
	
	public DrawFrame(double[][] ellipseArray, int[][] arcsArray) {
		ellipsesArrays[0] = ellipseArray;
		arcsArrays[0] = arcsArray;
	}
	
	public DrawFrame(double[][] ellipseArray, int[][] arcsArray, double[][] ellipseArray2, int[][] arcsArray2) {		
		ellipsesArrays[0] = ellipseArray;
		ellipsesArrays[1] = ellipseArray2;
		arcsArrays[0] = arcsArray;
		arcsArrays[1] = arcsArray2;
	}
	
	
	public void paintComponent (Graphics g) {
	    Graphics2D g2 = (Graphics2D) g;
	    
	    for (int a = 0; a < 2; a++) {
	    	if (a == 1) { // flip before drawing bottom half (for drawing meander, rather than only a perfect matching)
	    	    int m1 = getHeight()/2;
	    	    g2.translate(0,m1);
	    	    g2.scale(1,-1);
	    	    g2.translate(0,-m1-40);
	    	}
	    	
	    	if (ellipsesArrays[a] != null) {
	    		for (int i = 0; i < ellipsesArrays[a].length; i++) {
	    			Ellipse2D.Double circle = new Ellipse2D.Double(ellipsesArrays[a][i][0], height/2 - ellipsesArrays[a][i][3]/2,
	    					ellipsesArrays[a][i][2], ellipsesArrays[a][i][3]);
	    			g2.draw(circle);
	    		}
	    		
	    		for (int i = 0; i < arcsArrays[a].length; i++) {
	    			g2.drawArc(arcsArrays[a][i][0], arcsArrays[a][i][1], arcsArrays[a][i][2], arcsArrays[a][i][3], 0, 180);
	    		}
	    	}
	    }
	    
	}
	
	public static void drawPerfectMatching(double[][] ellipseArray, int[][] arcsArray) {
		JFrame frame = new JFrame();
		DrawFrame component = new DrawFrame(ellipseArray, arcsArray);
	    frame.setSize(PerfectMatching.x * (ellipseArray.length + 1), height);
	    frame.setTitle("title");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocation(xOffset, yOffset); // when drawing multiple meanders, you can change the offset before drawing each one
		 // so the window don't overlap each other as they appear on the screen
	    
	    frame.add(component);
	    frame.setVisible(true);
	}
	
	public static void drawMeander(double[][] ellipseArray, int[][] arcsArray, double[][] ellipseArray2, int[][] arcsArray2) {
		JFrame frame = new JFrame();
		DrawFrame component = new DrawFrame(ellipseArray, arcsArray, ellipseArray2, arcsArray2);
	    frame.setSize(PerfectMatching.x * (ellipseArray.length + 1), height);
	    frame.setTitle("title");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setLocation(xOffset, yOffset); // when drawing multiple meanders, you can change the offset before drawing each one
	    									 // so the window don't overlap each other as they appear on the screen
	    frame.add(component);
	    frame.setVisible(true);
	}

}
