package io.github.unchangingconstant.studenttracker;

import java.net.URL;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.unchangingconstant.studenttracker.app.services.ExportCSVService;
import io.github.unchangingconstant.studenttracker.config.DatabaseModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * TODO Consider more event driven approach, should DAO be accessible via a
 * service and send events to the model? Avoids hard-coded model-dao sync
 */
public class StudentTrackerApp extends Application {

        public static final Injector appContext = Guice.createInjector(new DatabaseModule());
        public static final String TITLE = "Student Tracker";

        @Override
        public void start(Stage stage) throws Exception {

                URL location = getClass().getResource("/view/root.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(location);
                fxmlLoader.setControllerFactory(appContext::getInstance);
                Parent root = fxmlLoader.load();

                ExportCSVService.exportStudentVisitsCSV(8, "");

                Scene scene = new Scene(root, 960, 540);
                stage.setTitle(TITLE);
                stage.setScene(scene);

                // When main window is closed, stop application
                stage.onCloseRequestProperty().set(windowEvent -> {
                        if (windowEvent.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST))    {
                                Platform.exit();
                        }
                });

                stage.show();
        }

}