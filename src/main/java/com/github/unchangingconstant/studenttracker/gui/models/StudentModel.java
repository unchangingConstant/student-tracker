package com.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;

import com.github.unchangingconstant.studenttracker.app.entities.Student;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

@Getter
public class StudentModel {
    
    private final SimpleObjectProperty<Integer> studentId;
    private final SimpleStringProperty fullName;
    private final SimpleStringProperty prefName;
    private final SimpleObjectProperty<Instant> dateAdded;
    private final SimpleObjectProperty<Integer> visitTime;

    // Using SimpleObjectProperty<Integer> instead of SimpleIntegerProperty because the latter
    // doesn't allow null values
    public StudentModel(Integer studentId, String fullName, String prefName, Instant dateAdded, Integer visitTime)   {
        this.studentId = new SimpleObjectProperty<>(studentId);
        this.fullName = new SimpleStringProperty(fullName);
        this.prefName = new SimpleStringProperty(prefName);
        this.dateAdded = new SimpleObjectProperty<Instant>(dateAdded);
        this.visitTime = new SimpleObjectProperty<Integer>(visitTime);
    }

    public static StudentModel map(Student entity) {
        return new StudentModel(
            entity.getStudentId(),
            entity.getFullName(),
            entity.getPreferredName(),
            entity.getDateAdded(),
            entity.getVisitTime()
        );
    }

    /*
     * Collection bindings will need this to know which entities are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StudentModel) {
            return (((StudentModel) obj).getStudentId().get().equals(studentId.get()));
        }
        return false;
    }

}
