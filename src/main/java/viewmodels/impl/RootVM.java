package viewmodels.impl;

import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import models.Model;
import models.impl.AttendanceDatabaseModel;
import viewmodels.ViewModel;
import domainentities.Student;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class RootVM implements ViewModel<ObservableList<Student>> {

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
    public void setModel(Model<ObservableList<Student>> model) {
        this.model = (AttendanceDatabaseModel) model;
        databasePageController.setModel(model);
        registerPageController.setModel(model);
    }

}