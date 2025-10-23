package io.github.unchangingconstant.studenttracker.app.entities;

import static org.instancio.Select.field;

import java.time.temporal.ChronoUnit;
import org.instancio.Instancio;
import org.instancio.InstancioApi;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;

/**
 * When inserting Instant objects into the data base, they get truncated down to
 * millisecond-precision. This raises a problem in tests. We don't care that the
 * database truncates Instant objects. But when we compare database results with
 * Instant objects, they are considered unequal becuase created Instant objects
 * have nanosecond-precision.
 * 
 * It's lengthy to do this code all the time, so it's a seperate util class now
 */
public class StudentTestUtil {

    public static InstancioApi<Student> student() {
        return Instancio.of(Student.class)
                .generate(field(Student::getDateAdded), gen -> gen.temporal().instant()
                        .truncatedTo(ChronoUnit.MILLIS));
    }

}
