package io.github.unchangingconstant.studenttracker;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import io.github.unchangingconstant.studenttracker.models.impl.AttendanceDatabaseModel;
import io.github.unchangingconstant.studenttracker.viewmodels.impl.RootVM;

import org.sqlite.SQLiteDataSource;

import io.github.unchangingconstant.studenttracker.buildutils.DatabaseInitializer;
import io.github.unchangingconstant.studenttracker.dao.AttendanceDAO;

// TODO, is sqlobject a necessary dependency?
/**
 * TODO Consider more event driven approach, should DAO be accessible via a
 * service and send events to the model? Avoids hard-coded model-dao sync
 */
public class StudentTrackerApp extends Application {

    private static String databaseLocation;

    @Override
    public void start(Stage stage) throws Exception {

        String classpath = System.getProperty("java.class.path");
        String[] classPathValues = classpath == null ? new String[] { "Null classpath (huh?)" }
                : classpath.split(File.pathSeparator);
        System.out.println(Arrays.toString(classPathValues));

        String modulepath = System.getProperty("jdk.module.path");
        String[] modulePathValues = modulepath == null ? new String[] { "Null modulepath" }
                : modulepath.split(File.pathSeparator);
        System.out.println(Arrays.toString(modulePathValues));

        URL location = getClass().getResource("/fxml/root.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        SQLiteDataSource dataSource = DatabaseInitializer
                .initializeDatabase(String.format("%s/database.db", System.getProperty("user.dir")));

        AttendanceDAO dao = AttendanceDAO.getAttendanceDAO(dataSource);
        AttendanceDatabaseModel model = new AttendanceDatabaseModel(dao);
        Parent root = fxmlLoader.load();
        ((RootVM) fxmlLoader.getController()).setModel(model);

        Scene scene = new Scene(root, 300, 275);
        stage.setTitle("StudentTracker");
        stage.setScene(scene);
        stage.show();
    }

}