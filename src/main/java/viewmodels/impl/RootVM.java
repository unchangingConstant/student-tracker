package viewmodels.impl;

import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import models.impl.AttendanceDatabaseModel;
import viewmodels.ViewModel;
import javafx.fxml.FXML;

public class RootVM implements ViewModel<AttendanceDatabaseModel> {

    private AttendanceDatabaseModel model;

    @FXML
    protected Pane currentPage;
    @FXML
    protected ListView<String> databasePage;
    @FXML
    protected GridPane registerPage;
    @FXML
    protected DatabaseVM databasePageController;
    @FXML
    protected RegisterVM registerPageController;

    @Override
    public void setModel(AttendanceDatabaseModel model) {
        this.model = model;
        databasePageController.setModel(model);
        registerPageController.setModel(model);
    }

}