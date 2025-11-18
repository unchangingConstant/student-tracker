package io.github.unchangingconstant.studenttracker.app.controllers;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/*
 * Will "manage" all GUI windows
 */
public class RootController implements Controller {

    @FXML
    private AnchorPane root;

    @Inject
    public RootController()   {
    }

    @Override
    public void initialize() {
        Stage.getWindows().addListener(new ListChangeListener<Window>() {
            @Override
            public void onChanged(Change<? extends Window> c) {
                c.next(); // ???? This line makes it work for some reason
                // If the main window is closed, close all windows
                // TODO there was to be a better way of knowing when the main window has closed
                if (c.wasRemoved() && c.getRemoved().contains(root.getScene().getWindow())) {
                    Platform.exit();
                }
            }
        });
    }

}
