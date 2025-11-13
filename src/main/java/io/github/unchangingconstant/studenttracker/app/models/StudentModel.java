package io.github.unchangingconstant.studenttracker.app.models;

import java.time.Instant;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;

@Getter
public class StudentModel {
    
    private final ReadOnlyIntegerProperty studentId;
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty middleName;
    private final SimpleStringProperty lastName;
    private final ReadOnlyObjectProperty<Instant> dateAdded;
    private final SimpleIntegerProperty subjects;

    // a studentId of -1 means the student is not in the database 
    public StudentModel(Integer studentId, String firstName, String middleName, String lastName, Instant dateAdded, Integer subjects)   {
        this.studentId = new SimpleIntegerProperty(studentId == null ? -1 : studentId);
        this.firstName = new SimpleStringProperty(firstName);
        this.middleName = new SimpleStringProperty(middleName);
        this.lastName = new SimpleStringProperty(lastName);
        this.dateAdded = new SimpleObjectProperty<Instant>(dateAdded);
        this.subjects = new SimpleIntegerProperty(subjects);
    }

}
