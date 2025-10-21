package io.github.unchangingconstant.studenttracker.app.controllers.components;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class OngoingVisitViewController implements Controller {

    @FXML
    private TableView<String[]> ongoingVisitView;

    @Inject
    public OngoingVisitViewController() {

    }

    @Override
    public void initialize() {
    }

}
