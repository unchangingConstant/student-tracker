import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.impl.SQLModel;
import viewmodels.impl.RootVM;

public class StudentTrackerApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        URL location = getClass().getResource("fxml/root.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        Parent root = fxmlLoader.load();
        ((RootVM) fxmlLoader.getController()).setModel(new SQLModel(null));
        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}