package viewmodels.impl;

import domainentities.Student;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import models.impl.AttendanceDatabaseModel;
import viewmodels.ViewModel;

public class DatabaseVM implements ViewModel<AttendanceDatabaseModel> {

    protected AttendanceDatabaseModel model;

    @FXML
    protected ListView<Student> studentList;

    @Override
    public void setModel(AttendanceDatabaseModel model) {
        this.model = model;
        model.bind(studentList.itemsProperty());
    }

}
