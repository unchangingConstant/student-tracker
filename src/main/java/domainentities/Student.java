package domainentities;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder // not necessary. Stupid overhead imo
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
