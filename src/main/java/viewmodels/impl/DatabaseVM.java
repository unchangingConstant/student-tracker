package viewmodels.impl;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import models.Model;
import models.impl.StudentDatabaseModel;
import viewmodels.ViewModel;

public class DatabaseVM implements ViewModel {

    @FXML
    private TableColumn<StudentDatabaseModel, String> nameColumn;
    @FXML
    private TableColumn<StudentDatabaseModel, Short> subjectsColumn;

    private StudentDatabaseModel model;

    @Override
    public void setModel(Model model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setModel'");
    }

}
