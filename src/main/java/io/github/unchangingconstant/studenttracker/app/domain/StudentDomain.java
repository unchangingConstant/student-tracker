package io.github.unchangingconstant.studenttracker.app.domain;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class StudentDomain {

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
