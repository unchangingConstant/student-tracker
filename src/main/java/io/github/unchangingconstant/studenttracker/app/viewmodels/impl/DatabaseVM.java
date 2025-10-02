package io.github.unchangingconstant.studenttracker.viewmodels.impl;

import io.github.unchangingconstant.studenttracker.domainentities.Student;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import io.github.unchangingconstant.studenttracker.models.impl.AttendanceDatabaseModel;
import io.github.unchangingconstant.studenttracker.viewmodels.ViewModel;

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
