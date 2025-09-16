package viewmodels.impl;

import domainentities.Student;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import models.Model;
import models.impl.AttendanceDatabaseModel;
import viewmodels.ViewModel;

public class DatabaseVM implements ViewModel<ObservableList<Student>> {

    protected AttendanceDatabaseModel model;

    @FXML
    protected ListView<Student> studentList;

    @Override
    public void setModel(Model<ObservableList<Student>> model) {
        this.model = (AttendanceDatabaseModel) model;
        model.bind(studentList.itemsProperty());
    }

}
