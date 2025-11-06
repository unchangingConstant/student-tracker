package io.github.unchangingconstant.studenttracker.app.backend.entities;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Student {

    @NonNull
    private Integer studentId;
    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    private String middleName;
    @NonNull
    private Integer subjects;
    @NonNull
    private Instant dateAdded;

    public String getFullName() {
        return firstName + " " + 
            (middleName == null ? "" : middleName + " ") + 
            lastName;
    }

}
