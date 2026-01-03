package com.github.unchangingconstant.studenttracker.gui;

import java.io.IOException;
import java.net.URL;

import com.github.unchangingconstant.studenttracker.StudentTrackerApp;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Will manage all GUI windows and serve as the entry point to the GUI
 */
@Singleton
public class WindowManager {

    private Stage recordManager;
    private Stage exportPage;

    @Inject
    public WindowManager()   {
    }

    public void openMainPage(Stage stage) {
        Parent root = loadFXML("/view/pages/attendance_dashboard_page.fxml");
        Scene scene = provideScene(root, 100, 100);  
        stage.setScene(scene);
        stage.setTitle(StudentTrackerApp.TITLE);
        stage.setMaximized(true);
        stage.show();
    }

    public void openExportPage() {
        if (exportPage != null) {
            exportPage.toFront();
            return;
        }
        Scene scene = provideScene(loadFXML("/view/pages/export_page.fxml"), 640, 900);
        exportPage = provideStage();
        exportPage.setScene(scene);
        exportPage.show();
        exportPage.setOnCloseRequest(windowEvent -> {
            onCloseExportPage();
        });
    }

    public void closeExportPage() {
        exportPage.close();
    }

    public void openRecordManager() {
        if (recordManager != null) {
            recordManager.toFront();
            return;
        };
        Scene scene = provideScene(loadFXML("/view/pages/record_manager_page.fxml"), 960, 540);
        recordManager = provideStage();
        recordManager.setScene(scene);
        recordManager.show();
        recordManager.setOnCloseRequest(windowEvent -> {
            onCloseRecordManager();
        });
    }

    private void onCloseExportPage() {
        exportPage = null;
    }

    private void onCloseRecordManager() {
        recordManager = null;
    }

    private Parent loadFXML(String url) {
        URL location = getClass().getResource(url);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(location);
        fxmlLoader.setControllerFactory(StudentTrackerApp.appContext::getInstance);
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Ensures global CSS is applied to all scenes
    private Scene provideScene(Parent root, int width, int height) {
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add("/view/global.css");
        return scene;
    }

    private Stage provideStage() {
        Stage stage = new Stage();
        stage.setTitle(StudentTrackerApp.TITLE);
        return stage;
    }

}
