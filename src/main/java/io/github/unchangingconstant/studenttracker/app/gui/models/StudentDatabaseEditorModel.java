package io.github.unchangingconstant.studenttracker.app.gui.models;

import com.google.inject.Inject;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class StudentDatabaseEditorModel {
    
    private final SimpleListProperty<Student> editorModel = new SimpleListProperty<>(FXCollections.observableArrayList());
    private StudentTableModel studentTableModel;

    @Inject
    public StudentDatabaseEditorModel(StudentTableModel studentTableModel) {
        this.studentTableModel = studentTableModel;
        
    }

}
