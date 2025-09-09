import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentTrackerApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.getProperty("java.class.path");
        Parent root = FXMLLoader.load(getClass().getResource("pages/student_tracker.fxml"));

        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}