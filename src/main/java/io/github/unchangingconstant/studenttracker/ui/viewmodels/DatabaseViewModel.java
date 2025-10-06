package io.github.unchangingconstant.studenttracker.app.viewmodels;

import io.github.unchangingconstant.studenttracker.app.domainentities.Student;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import io.github.unchangingconstant.studenttracker.app.models.impl.AttendanceDatabaseModel;

public class DatabaseViewModel {

    protected AttendanceDatabaseModel model;

    @FXML
    protected ListView<Student> studentList;

    public void setModel(AttendanceDatabaseModel model) {
        this.model = model;
        model.bind(studentList.itemsProperty());
    }

}
