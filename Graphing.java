import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javax.imageio.ImageIO;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;


// https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm

public class Graphing extends Application {

    static int n = 5;   // order of meanders/perfectmatchings : exists to make sure each column is wide enough
    // static int width = 2*n*PerfectMatching.d + 2*n*(PerfectMatching.x+1);
    static int width = 2*n*(PerfectMatching.x+3);
    static int height = 200;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(Graphing.class, args);
    }
    


    @Override
    public void start(Stage stage) {
// Use a border pane as the root for scene
        BorderPane border = new BorderPane();
        ScrollPane s1 = new ScrollPane();
        s1.setPrefSize(1700, 900);
        
        // // uncomment the below lines and comment out the third line in order to have a ScrollPane instead of BorderPane
        s1.setContent(addPMGridPane());
        border.setCenter(s1);

        // s1.setContent(addMeandersGridPane());
        // border.setCenter(s1);
 

        Scene scene = new Scene(border);
        stage.setScene(scene);
        // stage.setTitle("Layout Sample");
        stage.show();
        // WritableImage snapshot = border.snapshot(new SnapshotParameters(), null);
        // saveToFile(snapshot);
    }



    public static void saveToFile(WritableImage wim) {
        File file = new File("graphics/output.png");
        BufferedImage bim = SwingFXUtils.fromFXImage(wim, null);
        try {
            ImageIO.write(bim, "png", file);
        } catch (Exception e) {}
      }


    private GridPane addMeandersGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 0, 0, 0));
        grid.setGridLinesVisible(true);

        Meander[][] mbo = Meander.allMeandersSeparatedByOrder(n);
        int numColumns = mbo[n/2].length;
        for (int i = 0; i < mbo.length; i++) { // for each level
            System.out.println(mbo[i].length);
            for (int j = 0; j < mbo[i].length; j++) { // for each meander on that level
                Pane pane = new Pane();
                HashSet<Shape> shapes = meanderDraw(mbo[i][j]);
                pane.getChildren().addAll(shapes);
                grid.add(pane, j, i);
            }
        }

        for (int i = 0; i < numColumns; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(width, width, Double.MAX_VALUE));
        }
        for (int i = 0; i < n; i++) {
            grid.getRowConstraints().add(new RowConstraints(height, height, Double.MAX_VALUE));
        }

        return grid;
    }


    /*
     * Creates a grid for the center region with four columns and three rows
     */
    private GridPane addPMGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10); // might have to adjust these to get the spacing right (one of the final steps)
        grid.setPadding(new Insets(0, 0, 0, 0)); // order is clockwise from top (T R B L). shouldn't have to use this, it's just padding between the edge of the picture and the outside edge of the grid
        grid.setGridLinesVisible(true); // turn off later

        // the new stuff... ****************************************************************************************************************************************************************************
        
        int[] levelCounters = new int[n]; // to keep track of how many perfect matchings have been added to each level
        String[] cnStrings = Catalan.CnStrings(n); // string representation of each perfect matching
        int[] levels = Catalan.NarayanaRow(n); // list of number of arrays on each level
        int numColumns = levels[n/2];

        // this is where the magic happens
        for (String str : cnStrings) {
            Pane pane = new Pane();
            PerfectMatching pm = new PerfectMatching(str);
            HashSet<Shape> shapes = pmDraw(pm);
            pane.getChildren().addAll(shapes);
            int lev = pm.level() - 1;
            int i = levelCounters[pm.level()-1];
            // grid.add(pane, levelCounters[lev] + (numColumns - levels[lev]) / 2, lev*2); // for every other row
            grid.add(pane, levelCounters[lev] + (numColumns - levels[lev]) / 2, lev);
            levelCounters[pm.level()-1]++;
        }
        // end magic

        for (int i = 0; i < numColumns; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(width, width, Double.MAX_VALUE));
        }

        for (int i = 0; i < 2*n - 1; i++) {
            grid.getRowConstraints().add(new RowConstraints(height, height, Double.MAX_VALUE));
        }

        Pane pane = new Pane();
        pane.setPrefSize(150, 150); // leaving this out will give the minimum size possible to fit everything that you add in. I think.
        Rectangle rect = new Rectangle(40.0, 40.0);
        rect.setFill(Color.BLUE);
        rect.relocate(50, 50);
        Label label = new Label("Go!");
        label.relocate(0, 0); // coordinates for top-left corner of label

        Line line = new Line();
        line.setStartX(0);
        line.setStartY(20);
        int offset = 6;
        line.setEndX(10 * offset);
        line.setEndY(10 * offset);
        
        // pane.getChildren().addAll(rect, label, line);
        // grid.add(pane, 1, 2); // **********************************************


        Line line2 = new Line();
        line2.setStartX(0);
        line2.setStartY(0);
        line2.setEndX(20);
        line2.setEndY(20);
        // pane.getChildren().addAll(line2); // you can add to pane before or after adding pane to grid.

        

        PerfectMatching[] pms = new PerfectMatching[5];
        pms[0] = new PerfectMatching("101010");
        pms[1] = new PerfectMatching("111000");
        pms[2] = new PerfectMatching("11001100");
        HashSet<Shape> pmShapes = pmDraw(pms[2]);

        Meander[] mds = new Meander[5];
        mds[0] = new Meander(new PerfectMatching("101100"), new PerfectMatching("110010"));
        mds[1] = new Meander(new PerfectMatching("110100"), new PerfectMatching("101010"));
        mds[2] = new Meander(new PerfectMatching("111000"), new PerfectMatching("111000"));
        // HashSet<Shape> mdShapes = meanderDraw(mds[0]);
        // pane.getChildren().addAll(pmShapes);

        // pane.getChildren().addAll(mdShapes);
        // System.out.println(mds[1].top.level());
        // System.out.println(mds[1].bottom.level());
        return grid;
    }



    private HashSet<Shape> pmDraw(PerfectMatching pm) {
        HashSet<Shape> set = new HashSet<Shape>();
        for (int i = 0; i < pm.order()*2; i++) {
            Circle circle = new Circle(PerfectMatching.x*(i+1) - PerfectMatching.d / 2, height / 2 - PerfectMatching.d / 2, PerfectMatching.d);
            circle.setFill(null);
            circle.setStroke(Color.BLACK);
            set.add(circle);
        }

        int[][] arcs = pm.arcs;
        int[][] arcsArray = new int[pm.order()][4];
        for (int i = 0; i < pm.order(); i++) {
            arcsArray[i][0] = (arcs[i][0]+1)*PerfectMatching.x; // top-left x
            arcsArray[i][2] = (Math.abs(arcs[i][1]-arcs[i][0]))*PerfectMatching.x; // width
            arcsArray[i][3] = arcsArray[i][2]/2; // height
            arcsArray[i][1] = height / 2 - arcsArray[i][3] / 2 - PerfectMatching.d / 2; // top-left y
        }
        for (int i = 0; i < arcsArray.length; i++) {
            Arc arc = new Arc(arcsArray[i][0] - 5 + arcsArray[i][2] / 2, arcsArray[i][1] - 10 + arcsArray[i][3] / 2, arcsArray[i][2] / 2, arcsArray[i][3] / 2, 0, 180); // maybe the manual offsets should depend on PerfectMatching.d
            arc.setFill(null);
            arc.setStroke(Color.BLACK);
            set.add(arc);
        }

        return set;
    }



    private HashSet<Shape> meanderDraw(Meander md) {
        HashSet<Shape> set = new HashSet<Shape>();
        for (int i = 0; i < md.order*2; i++) {
            Circle circle = new Circle(PerfectMatching.x*(i+1) - PerfectMatching.d / 2, height / 2 - PerfectMatching.d / 2, PerfectMatching.d);
            circle.setFill(null);
            circle.setStroke(Color.BLACK);
            set.add(circle);
        }
        PerfectMatching[] top_bottom = {md.top, md.bottom};
        for (int j = 0; j <= 1; j++) {
            PerfectMatching pm = top_bottom[j];
            int[][] arcs = pm.arcs;
            int[][] arcsArray = new int[pm.order()][4];
            for (int i = 0; i < pm.order(); i++) {
                arcsArray[i][0] = (arcs[i][0]+1)*PerfectMatching.x; // top-left x
                arcsArray[i][2] = (Math.abs(arcs[i][1]-arcs[i][0]))*PerfectMatching.x; // width
                arcsArray[i][3] = arcsArray[i][2]/2; // height
                arcsArray[i][1] = height / 2 - arcsArray[i][3] / 2 - PerfectMatching.d / 2; // top-left y
            }
            for (int i = 0; i < arcsArray.length; i++) {
                Arc arc = new Arc(arcsArray[i][0] - 5 + arcsArray[i][2] / 2, arcsArray[i][1] - 10 + arcsArray[i][3] / 2, arcsArray[i][2] / 2, arcsArray[i][3] / 2, 0, 180); // maybe the manual offsets should depend on PerfectMatching.d
                arc.setFill(null);
                arc.setStroke(Color.BLACK);
                if (j == 1) {
                    arc.getTransforms().add(new Rotate(180, arc.getCenterX(), arc.getCenterY() + PerfectMatching.d)); // this will perfectly flip it for the bottom half
                }
                set.add(arc);
            }
        }
        return set;
    }
}