package com.github.unchangingconstant.studenttracker;

import com.github.unchangingconstant.studenttracker.gui.WindowManager;
import com.github.unchangingconstant.studenttracker.guice.DAOModule;
import com.github.unchangingconstant.studenttracker.guice.QRScanModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class StudentTrackerApp extends Application {

        public static final Injector appContext = 
                Guice.createInjector(
                        new DAOModule(),
                        new QRScanModule()
                );
        public static final String TITLE = "Student Tracker";

        @Override
        public void start(Stage stage) throws Exception {
                WindowManager windowManager = appContext.getInstance(WindowManager.class);
                // When main window is closed, stop application
                stage.onCloseRequestProperty().set(windowEvent -> {
                        if (windowEvent.getEventType().equals(WindowEvent.WINDOW_CLOSE_REQUEST))    {
                                Platform.exit();
                        }
                });
                windowManager.openMainPage(stage);
        }

}