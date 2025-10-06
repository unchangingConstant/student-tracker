package io.github.unchangingconstant.studenttracker.app.domainentities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

// TODO consider using java records to remove lombok dependency?
// Check ALL annotations for necessity
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor // Necessary for DAO mapping, they must have a default constructor!!!
@AllArgsConstructor
public class Student {

    @NonNull
    private String firstName;
    @NonNull
    private String lastName;
    private String middleName;
    private short subjects;
    // Keep this nullable for data validation
    // If student hasn't been assigned an Id, should be null
    private Integer studentId;

}
