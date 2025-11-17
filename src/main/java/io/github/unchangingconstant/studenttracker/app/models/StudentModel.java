package io.github.unchangingconstant.studenttracker.app.models;

import java.time.Instant;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

@Getter
public class StudentModel {
    
    private final SimpleObjectProperty<Integer> studentId;
    private final SimpleStringProperty fullLegalName;
    private final SimpleStringProperty prefName;
    private final SimpleObjectProperty<Instant> dateAdded;
    private final SimpleObjectProperty<Integer> subjects;

    // Using SimpleObjectProperty<Integer> instead of SimpleIntegerProperty because the latter
    // doesn't allow null values
    public StudentModel(Integer studentId, String fullLegalName, String prefName, Instant dateAdded, Integer subjects)   {
        this.studentId = new SimpleObjectProperty<>(studentId);
        this.fullLegalName = new SimpleStringProperty(fullLegalName);
        this.prefName = new SimpleStringProperty(prefName);
        this.dateAdded = new SimpleObjectProperty<Instant>(dateAdded);
        this.subjects = new SimpleObjectProperty<Integer>(subjects);
    }

}
