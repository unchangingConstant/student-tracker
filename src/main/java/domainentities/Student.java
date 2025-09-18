package domainentities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

// Check ALL annotations for necessity
@Data
@Builder
// TODO potentially necessary for dao mapping? Double check, read up on mappers
@NoArgsConstructor
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

    // TODO Delete once view logic is implemented on frontend
    public String toString() {
        return String.format("%s %s", firstName, lastName);
    }

}
