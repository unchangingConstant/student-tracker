package io.github.unchangingconstant.studenttracker.app.gui.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.backend.services.AttendanceService.IllegalDatabaseOperationException;
import io.github.unchangingconstant.studenttracker.app.gui.models.StudentTableModel;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;

public class DatabaseManagerViewModel {

    private StudentTableModel model;
    private AttendanceService service;

    @Inject
    public DatabaseManagerViewModel(StudentTableModel model, AttendanceService service)   {
        this.model = model;
        this.service = service;
    }

    public void onDeleteAction(Integer id) {
        try {
            service.deleteStudent(id);
        } catch (IllegalDatabaseOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void bindToStudentTable(ObjectProperty<ObservableList<Student>> itemsProperty) {
        model.bind(itemsProperty);
    }

}
