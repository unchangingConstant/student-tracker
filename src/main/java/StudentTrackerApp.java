import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StudentTrackerApp extends Application {

    @Override
    public void start(Stage stage) {
        // String javaVersion = System.getProperty("java.version");
        // String javafxVersion = System.getProperty("javafx.version");
        // Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " +
        // javaVersion + ".");
        // Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setTitle("StudentTracker");

        // Creates grid to organize form
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER); // aligns the gridpane to the center of its parent component
        // manages gaps between rows and columns
        grid.setHgap(10);
        grid.setVgap(10);
        // manages gaps between grid edges and parent component edges
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Enter new student");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        // --------

        // Creates form to add new student
        Label studentName = new Label("Student Name:");
        grid.add(studentName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label subjects = new Label("Subjects:");
        grid.add(subjects, 0, 2);

        TextField pwBox = new TextField();
        grid.add(pwBox, 1, 2);

        // --------

        // creates button that registers student
        Button btn = new Button("Register Student");
        HBox hbBtn = new HBox(10); // sets different spacing from other form components
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT); // aligns box with bottom right corner of parent component
        hbBtn.getChildren().add(btn); // button is now child of HBox
        grid.add(hbBtn, 1, 4);
        // -------

        // does something when button is pressed

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                actiontarget.setFill(Color.FIREBRICK);
                actiontarget.setText("Sign in button pressed");
            }
        });

        // -------

        Scene scene = new Scene(grid, 300, 275);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}