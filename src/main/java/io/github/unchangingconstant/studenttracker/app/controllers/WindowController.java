package io.github.unchangingconstant.studenttracker.app.controllers;

import java.io.IOException;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.StudentTrackerApp;
import io.github.unchangingconstant.studenttracker.app.Controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.MenuItem;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;


/*
 * Will "manage" all GUI windows
 */
@Singleton
public class WindowController {

    private Stage recordManager;
    private Stage exportDialog;

    @Inject
    public WindowController()   {
    }

    public void openExportDialog() {
        if (exportDialog != null) {
            exportDialog.toFront();
            return;
        };
        exportDialog = createStage("/view/pages/export_dialog.fxml");
        exportDialog.setAlwaysOnTop(true);
        exportDialog.setOnCloseRequest(windowEvent -> {
            onCloseExportDialog();
        });
    }

    public void openRecordManager() {
        if (recordManager != null) {
            recordManager.toFront();
            return;
        };
        recordManager = createStage("/view/pages/record_manager_page.fxml");
        recordManager.setOnCloseRequest(windowEvent -> {
            onCloseRecordManager();
        });
    }

    private void onCloseExportDialog() {
        exportDialog = null;
    }

    private void onCloseRecordManager() {
        recordManager = null;
    }

    private Stage createStage(String fxmlPath) {
        Stage stage = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxmlPath));
            fxmlLoader.setControllerFactory(StudentTrackerApp.appContext::getInstance);
            Scene scene = new Scene(fxmlLoader.load(), 960, 540);
            stage = new Stage();
            stage.setTitle(StudentTrackerApp.TITLE);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;
    }

}
