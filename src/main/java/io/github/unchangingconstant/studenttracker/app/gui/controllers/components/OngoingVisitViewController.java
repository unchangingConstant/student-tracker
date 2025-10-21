package io.github.unchangingconstant.studenttracker.app.gui.controllers.components;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;
import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.util.Duration;

public class OngoingVisitViewController implements Controller {

    @FXML
    private TableView<Visit> ongoingVisitView;

    private Timeline timeline;

    @Inject
    public OngoingVisitViewController() {

    }

    @Override
    public void initialize() {

    }

}
