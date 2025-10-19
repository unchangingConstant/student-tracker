package io.github.unchangingconstant.studenttracker.app.entities;

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
    private short subjects;

}
