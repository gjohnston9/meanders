import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.control.Slider;

public class PMGenerator extends Application {

    int a = 1000;

	@Override
	public void start(Stage primaryStage) {
		Canvas canvas = new Canvas(a, a);
		final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
		draw(graphicsContext);
		graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		graphicsContext.setFill(Color.ALICEBLUE);
		graphicsContext.setStroke(Color.ALICEBLUE);

		VBox box = new VBox();
        box.setStyle("-fx-background-color: #336699;");
        box.setMaxWidth(440);
        box.setMinWidth(440);
        box.setAlignment(Pos.CENTER);

        GridPane root = new GridPane();
        root.setAlignment(Pos.CENTER);
        //root.setAlignment(canvas, Pos.TOP_RIGHT);
        root.getChildren().addAll(canvas, box);

		Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setTitle("dsadsadasdd");
        primaryStage.setScene(scene);
        primaryStage.show();
	}

	private void draw(GraphicsContext g) {
        double canvasWidth = g.getCanvas().getWidth();
        double canvasHeight = g.getCanvas().getHeight();
    }

    public static void main(String[] args) {
        System.out.println("hello");
    }
}