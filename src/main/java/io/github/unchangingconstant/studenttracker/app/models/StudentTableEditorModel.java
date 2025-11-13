package io.github.unchangingconstant.studenttracker.app.models;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;

/*
 * Unlike the StudentTableModel (which may not exist at some point in the future), this model supports direct changes
 * to it. When its state differs from the database state, it is marked as "dirty". To submit the changes made to this table to the database, 
 * you commit the changes.
 */
@Singleton
public class StudentTableEditorModel {

    private AttendanceService attendanceService;

    private SimpleBooleanProperty dirty = new SimpleBooleanProperty(false); 
    // Stores all changes not yet committed to the database in oldest-first order
    private List<StudentTableChange> changes;
    private SimpleListProperty<StudentModel> studentCache;

    @Inject
    public StudentTableEditorModel(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public void commitChanges() {
    }

    /*
     * Class represents an uncommitted change
     */
    private class StudentTableChange    {
        private Integer studentId;
        private ChangeType change;
    }

    private enum ChangeType {
        UPDATE, INSERT, DELETE
    }

}
