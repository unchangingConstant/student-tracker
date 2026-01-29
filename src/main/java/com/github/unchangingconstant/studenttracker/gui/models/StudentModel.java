package com.github.unchangingconstant.studenttracker.gui.models;

import java.time.Instant;

import com.github.unchangingconstant.studenttracker.app.entities.Student;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

@Getter
public class StudentModel {
    
    private final SimpleObjectProperty<Integer> studentId;
    private final SimpleStringProperty fullName;
    private final SimpleStringProperty prefName;
    private final SimpleObjectProperty<Instant> dateAdded;
    private final SimpleIntegerProperty visitTime;

    // Using SimpleObjectProperty<Integer> instead of SimpleIntegerProperty because the latter
    // doesn't allow null values
    public StudentModel(Integer studentId, String fullName, String prefName, Instant dateAdded, Integer visitTime)   {
        this.studentId = new SimpleObjectProperty<>(studentId);
        this.fullName = new SimpleStringProperty(fullName);
        this.prefName = new SimpleStringProperty(prefName);
        this.dateAdded = new SimpleObjectProperty<Instant>(dateAdded);
        this.visitTime = new SimpleIntegerProperty(visitTime);
    }

    public StudentModel(Student entity) {
        this(
            entity.getStudentId(),
            entity.getFullName(),
            entity.getPreferredName(),
            entity.getDateAdded(),
            entity.getVisitTime()
        );
    }

    public void update(Student entity) {
        studentId.set(entity.getStudentId());
        fullName.set(entity.getFullName());
        prefName.set(entity.getPreferredName());
        dateAdded.set(entity.getDateAdded());
        visitTime.set(entity.getVisitTime());
    }

    public void set(StudentModel model) {
        studentId.set(model.getStudentId().get());
        fullName.set(model.getFullName().get());
        prefName.set(model.getPrefName().get());
        dateAdded.set(model.getDateAdded().get());
        visitTime.set(model.getVisitTime().get());
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
