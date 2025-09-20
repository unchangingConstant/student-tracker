import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.impl.AttendanceDatabaseModel;
import viewmodels.impl.RootVM;

import org.sqlite.SQLiteDataSource;

import dao.AttendanceDAO;
import database.DatabaseInitializer;

// TODO, is sqlobject a necessary dependency?
public class StudentTrackerApp extends Application {

    private static String databaseLocation;

    @Override
    public void start(Stage stage) throws Exception {

        URL location = getClass().getResource("fxml/root.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        SQLiteDataSource dataSource = DatabaseInitializer.initializeDatabase(databaseLocation);
        AttendanceDAO dao = AttendanceDAO.getAttendanceDAO(dataSource);
        AttendanceDatabaseModel model = new AttendanceDatabaseModel(dao);
        Parent root = fxmlLoader.load();
        ((RootVM) fxmlLoader.getController()).setModel(model);

        Scene scene = new Scene(root, 300, 275);
        stage.setTitle("StudentTracker");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // create setup-type method?
        if (args.length == 0) {
            // TODO better exception type?
            throw new RuntimeException("Select a run mode for the application, 'dev' or 'prod'");
        }
        if (args[0].equals("dev")) {
            databaseLocation = "database/database.db"; // TODO improve file path specification method
        } else if (args[0].equals("prod")) {
            throw new RuntimeException("prod environment not yet implemented");
            // Set up .student-tracker folder and shih
        } else {
            throw new RuntimeException("No valid run mode has been set for the application");
        }

        launch();
        // create cleanup-type method
    }

}