package mappers.impl;

import java.util.HashMap;
import java.util.Map;

import domainentities.Student;
import mappers.Mapper;

public class StudentToMapMapper implements Mapper<Student, Map<String, Object>> {

    @Override
    public Map<String, Object> map(Student src) {

        Map<String, Object> map = new HashMap<>();

        map.put("firstName", src.getFirstName());
        map.put("lastName", src.getLastName());
        map.put("email", src.getMiddleName());
        map.put("age", src.getSubjects());
        map.put("id", src.getStudentId());

        return map;
    }

}
