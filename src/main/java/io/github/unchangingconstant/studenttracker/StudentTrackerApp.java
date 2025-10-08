package io.github.unchangingconstant.studenttracker;

import java.net.URL;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.unchangingconstant.studenttracker.config.DAOModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * TODO Consider more event driven approach, should DAO be accessible via a
 * service and send events to the model? Avoids hard-coded model-dao sync
 */
public class StudentTrackerApp extends Application {

        @Override
        public void start(Stage stage) throws Exception {
                Injector injector = Guice.createInjector(new DAOModule());

                URL location = getClass().getResource("/view/root.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(location);
                fxmlLoader.setControllerFactory(injector::getInstance);
                Parent root = fxmlLoader.load();

                Scene scene = new Scene(root, 300, 275);
                stage.setTitle("StudentTracker");
                stage.setScene(scene);
                stage.show();
        }

}