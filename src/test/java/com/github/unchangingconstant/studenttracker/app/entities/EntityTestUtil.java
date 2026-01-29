package com.github.unchangingconstant.studenttracker.app.entities;

import static org.instancio.Select.field;

import java.time.temporal.ChronoUnit;
import org.instancio.Instancio;
import org.instancio.InstancioApi;

/**
 * When inserting Instant objects into the database, they get truncated down to
 * millisecond-precision. This raises a problem in tests. We don't care that the
 * database truncates Instant objects. But when we compare database results with
 * Instant objects, they are considered unequal because created Instant objects
 * have nanosecond-precision.
 * 
 * It's lengthy to do this code all the time, so it's a separate util class now
 */
public class EntityTestUtil {

    public static InstancioApi<Student> student() {
        return Instancio.of(Student.class)
            .generate(field(Student::getDateAdded), gen -> gen.temporal().instant()
            .truncatedTo(ChronoUnit.MILLIS));
    }

    public static InstancioApi<Student> validStudent() {
        return student()
            .generate(field(Student::getFullName),
                gen -> gen.string().length(Student.FULL_NAME_MIN_LEN, Student.FULL_NAME_MAX_LEN))
            .generate(field(Student::getPreferredName),
                gen -> gen.string().length(Student.PREFERRED_NAME_MIN_LEN, Student.PREFERRED_NAME_MAX_LEN))
            .generate(field(Student::getVisitTime),
                gen -> gen.ints().range(0, 1).as(i -> Student.VISIT_TIME_ALLOWED_VALUES[i]));
    }

    public static InstancioApi<Visit> visit() {
        return Instancio.of(Visit.class)
            .generate(field(Visit::getStartTime), gen -> gen.temporal().instant()
            .truncatedTo(ChronoUnit.MILLIS));
    }

}
