package io.github.unchangingconstant.studenttracker;

import java.net.URL;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.github.unchangingconstant.studenttracker.config.DatabaseModule;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
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
                Injector injector = Guice.createInjector(new DatabaseModule());

                URL location = getClass().getResource("/view/main.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(location);
                fxmlLoader.setControllerFactory(injector::getInstance);
                Parent root = fxmlLoader.load();

                Scene scene = new Scene(root, 960, 540);
                stage.setTitle("StudentTracker");
                stage.setScene(scene);
                stage.show();
        }

}