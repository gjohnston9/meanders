import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javax.imageio.ImageIO;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;


// https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm

public class Graphing extends Application {

    static int n = 5;   // order of meanders/perfectmatchings : exists to make sure each column is wide enough
    // static int width = 2*n*PerfectMatching.d + 2*n*(PerfectMatching.x+1);
    static int width = 2*n*(PerfectMatching.x+3);
    static int height = 200;
    static int emptyRowHeight = height * 4;

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
        s1.setPrefSize(1700, 700);
        GridPane grid = addMeandersGridPane();
        s1.setContent(grid);
        border.setCenter(grid); // uncomment for gridpane
        // border.setCenter(s1); // uncomment for scrollpane

        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.show();

        // screenshot
        // SnapshotParameters snap = new SnapshotParameters();
        // snap.setViewport(new Rectangle2D(0, 0, grid.getWidth(), grid.getHeight()));
        // WritableImage snapshot = border.snapshot(snap, null);
        // saveToFile(snapshot, "output");

        // splitAndSave(border, grid.getHeight(), grid.getWidth(), "split", 500, 500);

        BufferedImage[][] images = splitIntoBufferedImages(border, grid.getHeight(), grid.getWidth(), 2000, 2000);
        joinAndSaveSplitImages(images, 2000, 2000, "combined2");
    }


    

    /**
     * Split the canvas into a grid of BufferedImages and return the array of images
     *
     * @param border source of picture
     * @param height height of borderpane (first argument)
     * @param width width of borderpane (first argument)
     * @param splitSizeHeight height of each individual BufferedImage
     * @param splitSizeWidth width of each individual BufferedImage
     *
     * @return array of BufferedImages; images[i][j] is at position (y=i, x=j) in the grid of images that make up the original picture
     */
    public static BufferedImage[][] splitIntoBufferedImages(BorderPane border, double height, double width, int splitSizeHeight, int splitSizeWidth) {
        int y = (int) Math.ceil( (float) height / splitSizeHeight);
        int x = (int) Math.ceil( (float) width / splitSizeWidth);
        // System.out.println("y is " + y + " and x is " + x + ".");
        BufferedImage[][] images = new BufferedImage[y][x];
        System.out.println("splitting");
        for (int i = 0; i < y; i++) {
            System.out.println("i is " + i);
            for (int j = 0; j < x; j++) {
                System.out.println("  j is " + j);
                SnapshotParameters snap = new SnapshotParameters();
                snap.setViewport(new Rectangle2D(j * splitSizeWidth, i * splitSizeHeight, splitSizeWidth, splitSizeHeight));
                WritableImage snapshot = border.snapshot(snap, null);
                images[i][j] = SwingFXUtils.fromFXImage(snapshot, null);

                // extra testing stuff~~~~~~~~~~
                // File outputFile = new File("C:/Users/greg9/Dropbox/meanders/meanders/graphicsTest/" + "y" + i + "x" + j + ".png");
                // try {
                //     ImageIO.write(images[i][j], "png", outputFile);
                // } catch (IOException e) {
                //     throw new RuntimeException(e);
                // }
            }
        }
        return images;
    }


    /**
     * Combine images in a BufferedImage array into a single iamge, and save the image.
     *
     * @param images array of BufferedImages 
     * @param height height of each individual image
     * @param width width of each individual image
     * @param filename filename for the saved image
     */
    public static void joinAndSaveSplitImages(BufferedImage[][] images, double height, double width, String filename) {
        int totalHeight = (int) height * images.length;
        int totalWidth = (int) width * images[0].length;
        BufferedImage newImage = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        // Color oldColor = g2.getColor();
        // g2.setPaint(Color.WHITE);
        // g2.fillRect(0, 0, totalWidth, totalHeight);
        // g2.setColor(oldColor);
        System.out.println("joining and saving");
        for (int i = 0; i < images.length; i++) {
            System.out.println("i is " + i);
            for (int j = 0; j < images[0].length; j++) {
                System.out.println("  j is " + j);
                BufferedImage image = images[i][j];
                g2.drawImage(image, null, j * (int) width, i * (int) height);
            }
        }
        g2.dispose();
        File outputFile = new File("D:/Dropbox/meanders/meanders/graphicsTest/" + filename + ".png");
        try {
            ImageIO.write(newImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("done");
    }




    public static void saveToFile(Image image, String filename) {
        File outputFile = new File("C:/Users/greg9/Dropbox/meanders/meanders/graphicsTest/" + filename + ".png");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * Instead of saving a single picture of the entire canvas, split it into pieces and save them.
     * Use the gridpane, not the scrollpane, above - using the scrollpane will result in only what's shown on the screen being saved,
     * and that probably won't be the entire picture.
     *
     * @param border source of picture
     * @param height height of borderpane (first argument)
     * @param width width of borderpane (first argument)
     * @param filename base name of saved pictures to save
     * @param splitSizeHeight height of each individual saved picture
     * @param splitSizeWidth width of each individual saved picture
     */
    public static void splitAndSave(BorderPane border, double height, double width, String filename, int splitSizeHeight, int splitSizeWidth) {
        for (int i = 0; i < height; i += splitSizeHeight) {
            // i = Math.min(i, height);
            for (int j = 0; j < width; j += splitSizeWidth) {
                // j = Math.min(j, width);    
                SnapshotParameters snap = new SnapshotParameters();
                snap.setViewport(new Rectangle2D(j, i, splitSizeWidth, splitSizeHeight));
                WritableImage snapshot = border.snapshot(snap, null);

                String name = "y" + i + "x" + j + filename;
                saveToFile(snapshot, name);
            }
        }
    }


    private GridPane addMeandersGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(new Insets(0, 0, 0, 0));
        grid.setGridLinesVisible(true);

        HashMap<Meander, int[]> locations = new HashMap<>(); // as each Meander is added to a pane in the grid, this HashMap keeps track of that pane's location in the grid

        Meander[][] mbl = Meander.allMeandersSeparatedByLevel(n);
        int numColumns = mbl[n/2].length;
        for (int i = 0; i < mbl.length; i++) { // for each level
            // System.out.println(mbl[i].length);
            for (int j = 0; j < mbl[i].length; j++) { // for each meander on that level
                Pane pane = new Pane();
                HashSet<Shape> shapes = meanderDraw(mbl[i][j]);
                pane.getChildren().addAll(shapes);
                int column = j + (numColumns - mbl[i].length) / 2;
                int row = 2*i;
                grid.add(pane, column, row);
                locations.put(mbl[i][j], new int[] {column, row});
            }
        }



        for (int i = 0; i < numColumns; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(width, width, Double.MAX_VALUE));
        }
        
        for (int i = 0; i < 2*n-1; i++) {
            // original
            // grid.getRowConstraints().add(new RowConstraints(height, height, Double.MAX_VALUE));

            // new: rows with perfect matchings have the assigned height; the rows in between these rows (containing no perfect matchings) have
            // their height reduced by some factor (emptyRowHeight, defined at the top)
            if (i % 2 == 0) {
                grid.getRowConstraints().add(new RowConstraints(height, height, Double.MAX_VALUE));
            } else {
                grid.getRowConstraints().add(new RowConstraints(emptyRowHeight, emptyRowHeight, Double.MAX_VALUE));
            }
        }

        for (int i = 1; i < mbl.length; i++) {
            for (int j = 0; j < mbl[i].length; j++) {
                Meander md = mbl[i][j];
                for (Meander md2 : mbl[i-1]) { // for each Meander on the level above the Meander currently being considered
                    if (Meander.connected01(md, md2)) { // if they are connected by a pair of 01 moves, draw a line between them
                        int[] mdLocation = locations.get(md);
                        int[] md2Location = locations.get(md2);

                        Pane p = new Pane();
                        p.getChildren().add(connectDoubleSpaced(mdLocation[0], mdLocation[1], md2Location[0], md2Location[1]));
                        grid.add(p, mdLocation[0], mdLocation[1]);
                    }
                }
            }
        }
        // Pane p = new Pane();
        // p.getChildren().add(connectDoubleSpaced(0, 2, 1, 0));
        // grid.add(p, 0, 2);

        return grid;
    }

    /**
    * create and return line going between two panes in GridPane
    * y1 must be less than y2
    * these lines should only go across one empty row (i.e. startY - endY = emptyRowHeight)
    *
    * @param x1 column index of first pane
    * @param y1 row index of first pane
    * @param x2 column index of second pane
    * @param y2 row index of second pane
    *
    * @return line - to be added to first pane (pane in column x1, row y1)
    */
    private Line connectDoubleSpaced(int x1, int y1, int x2, int y2) {
        Line line = new Line();
        line.setStartX( ((float) width) / 2); // middle of pane
        line.setStartY(0); // top of pane
        line.setEndX(line.getStartX() + width * (x2 - x1));
        line.setEndY(-1 * emptyRowHeight);
        return line;
    }


    /*
     * Creates a grid for the center region with four columns and three rows
     */
    private GridPane addPMGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0); // might have to adjust these to get the spacing right (one of the final steps)
        grid.setPadding(new Insets(0, 0, 0, 0)); // order is clockwise from top (T R B L). shouldn't have to use this, it's just padding between the edge of the picture and the outside edge of the grid
        grid.setGridLinesVisible(true); // turn off later
        
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
            grid.add(pane, levelCounters[lev] + (numColumns - levels[lev]) / 2, lev*2); // for every other row
            // grid.add(pane, levelCounters[lev] + (numColumns - levels[lev]) / 2, lev); // for every row
            levelCounters[pm.level()-1]++; // indicate that the next perfect matching placed on the same level should be placed in the column
            // next to the column in which this perfect matching was placed
        }
        // end magic


        // the following is just for spacing: making sure that each of the columns is [width] wide, and each row is [height] tall
        for (int i = 0; i < numColumns; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(width, width, Double.MAX_VALUE));
        }

        for (int i = 0; i < 2*n-1; i++) {
            // original
            // grid.getRowConstraints().add(new RowConstraints(height, height, Double.MAX_VALUE));

            // emptyRowHeight = height;
            // new: rows with perfect matchings have the assigned height; the rows in between these rows (containing no perfect matchings) have
            // their height reduced by some factor (emptyRowHeight, defined at the top)
            if (i % 2 == 0) {
                grid.getRowConstraints().add(new RowConstraints(height, height, Double.MAX_VALUE));
            } else {
                grid.getRowConstraints().add(new RowConstraints(emptyRowHeight, emptyRowHeight, Double.MAX_VALUE));
            }
        }

        Pane p = new Pane();
        p.getChildren().add(connectDoubleSpaced(0, 2, 1, 0));
        grid.add(p, 0, 2);

        // just testing shapes and stuff
        Pane pane = new Pane();
        pane.setPrefSize(300, 300); // leaving this out will give the minimum size possible to fit everything that you add in. I think.
        Rectangle rect = new Rectangle(40.0, 40.0);
        rect.setFill(Color.BLUE);
        rect.relocate(50, 50);
        Label label = new Label("Go!");
        label.relocate(0, 0); // coordinates for top-left corner of label

        Line line = new Line();
        line.setStartX(50);
        line.setStartY(0);
        line.setEndX(450);
        line.setEndY(450);
        
        pane.getChildren().add(line);
        // grid.add(pane, 1, 1); // ********************************************** uncomment this line to add the above pane to the grid
        

        // testing PM and meander stuff
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