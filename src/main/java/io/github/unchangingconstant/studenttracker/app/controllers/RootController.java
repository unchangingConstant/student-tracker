package io.github.unchangingconstant.studenttracker.app.controllers;

import java.io.IOException;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.StudentTrackerApp;
import io.github.unchangingconstant.studenttracker.app.Controller;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.MenuItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


/*
 * Will "manage" all GUI windows
 */
public class RootController implements Controller {

    @FXML
    private MenuItem recordManagerMenuItem;

    @Inject
    public RootController()   {
    }

    @Override
    public void initialize() {
        recordManagerMenuItem.setOnAction(actionEvent -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/view/pages/database_manager_page.fxml"));
                fxmlLoader.setControllerFactory(StudentTrackerApp.appContext::getInstance);
                Scene scene = new Scene(fxmlLoader.load(), 960, 540);
                Stage stage = new Stage();
                stage.setTitle(StudentTrackerApp.TITLE);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
