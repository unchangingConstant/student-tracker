package io.github.unchangingconstant.studenttracker.app.viewmodels;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.models.StudentModel;
import io.github.unchangingconstant.studenttracker.app.models.StudentTableModel;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.IllegalDatabaseOperationException;
import io.github.unchangingconstant.studenttracker.app.services.AttendanceService.InvalidDatabaseEntryException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

@Singleton
public class DatabaseManagerViewModel {

    private StudentTableModel model;
    private AttendanceService service;

    /*
     * When edittingEnabled is true, the StudentEditor is visible. Otherwise, the addStudent button is visible
     */
    private SimpleBooleanProperty editingEnabled = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty editingEnabledProperty() {return editingEnabled;}

    /*
     * When editting enabled is off, all of this student's properties will be set to null (but not the properties themselves)
     */
    private ReadOnlyObjectProperty<StudentModel> currentEditedStudent = new SimpleObjectProperty<>(
        new StudentModel(null, "", "", null, 1));
    public ReadOnlyObjectProperty<StudentModel> currentEditedStudentProperty() {return currentEditedStudent;}

    @Inject
    public DatabaseManagerViewModel(StudentTableModel model, AttendanceService service)   {
        this.model = model;
        this.service = service;
        editingEnabled.addListener((obs, oldVal, newVal) -> resetEditedStudent());
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

    public void onEditStudentAction(StudentModel student)  {
        editingEnabled.set(true);
    }

    public void onAddStudentButtonAction() {
        editingEnabled.set(true);
    }

    public void onSaveAction()  {
        StudentModel student = currentEditedStudent.get();
        if (student.getStudentId().getValue() == null) {
            try {
                service.insertStudent(student.getFullLegalName().get(), student.getPrefName().get(), student.getSubjects().get());
            } catch (InvalidDatabaseEntryException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else if (model.containsStudentWithId(student.getStudentId().getValue())){ 
            // If the user just updates a student's info
        }
        resetEditedStudent();
        editingEnabled.set(false);
    }

    private void resetEditedStudent()   {
        currentEditedStudent.get().getFullLegalName().set("");
        currentEditedStudent.get().getPrefName().set("");
    }

}
