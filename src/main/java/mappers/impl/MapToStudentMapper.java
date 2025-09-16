package mappers.impl;

import java.util.Map;

import domainentities.Student;
import lombok.NonNull;
import mappers.Mapper;

// TODO Consider using reflection for this stuff, better testing!!! or not
public class MapToStudentMapper implements Mapper<Map<String, Object>, Student> {

    // Holy shit
    // btw, create subjects field in database
    @Override
    public Student map(Map<String, Object> src) {
        return Student.builder().firstName((@NonNull String) src.get("firstName"))
                .middleName((String) src.get("middleName"))
                .lastName((@NonNull String) src.get("lastName")).studentId((Integer) src.get("studentId"))
                .subjects((short) 1).build();
    }

}
