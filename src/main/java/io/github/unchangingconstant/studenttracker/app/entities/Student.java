package io.github.unchangingconstant.studenttracker.app.entities;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

// TODO consider using java records to remove lombok dependency?
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
