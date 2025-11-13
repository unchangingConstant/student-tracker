package io.github.unchangingconstant.studenttracker.app.viewmodels;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.IllegalDatabaseOperationException;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.InvalidDatabaseEntryException;
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

    public void onSaveAction(StudentModel student)  {
        if (student.getStudentId().get() == -1) {
            try {
                service.insertStudent(student.getFirstName().get(), student.getMiddleName().get(), student.getLastName().get(), student.getSubjects().get());
            } catch (InvalidDatabaseEntryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else { // If the user just updates a student's info
        }
    }

    public void onDeleteAction(Integer id) {
        try {
            service.deleteStudent(id);
        } catch (IllegalDatabaseOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void bindToStudentTable(ObjectProperty<ObservableList<StudentModel>> itemsProperty) {
        model.bind(itemsProperty);
    }

}
