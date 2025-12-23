package io.github.unchangingconstant.studenttracker;

import java.net.URL;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.unchangingconstant.studenttracker.gui.WindowController;
import io.github.unchangingconstant.studenttracker.guice.DAOModule;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class StudentTrackerApp extends Application {

        public static final Injector appContext = Guice.createInjector(new DAOModule());
        public static final WindowController windowController = appContext.getInstance(WindowController.class);
        public static final String TITLE = "Student Tracker";

        @Override
        public void start(Stage stage) throws Exception {

                URL location = getClass().getResource("/view/pages/attendance_dashboard_page.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(location);
                fxmlLoader.setControllerFactory(appContext::getInstance);
                Parent root = fxmlLoader.load();

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