package io.github.unchangingconstant.studenttracker.app.gui.controllers.pages;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.gui.Controller;
import io.github.unchangingconstant.studenttracker.app.gui.components.OngoingVisitView;
import io.github.unchangingconstant.studenttracker.app.gui.viewmodels.SessionViewModel;
import javafx.fxml.FXML;

public class SessionPageController implements Controller {

    @FXML
    private OngoingVisitView ongoingVisitsView;

    private SessionViewModel viewModel;

    @Inject
    public SessionPageController(SessionViewModel viewModel)  {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize() {
        viewModel.bindToModelProperty(ongoingVisitsView.itemsProperty());
    }

}
