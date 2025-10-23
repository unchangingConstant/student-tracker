package io.github.unchangingconstant.studenttracker.app.backend.entities;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

// Check ALL annotations for necessity
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
    private Integer subjects;
    @NonNull
    private Instant dateAdded;

}
