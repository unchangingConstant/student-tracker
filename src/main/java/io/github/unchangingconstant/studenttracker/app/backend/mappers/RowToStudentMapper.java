package io.github.unchangingconstant.studenttracker.app.backend.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Student;

public class RowToStudentMapper implements RowMapper<Student> {

    @Override
    public Student map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Student.builder().firstName(rs.getString("first_name")).middleName(rs.getString("middle_name"))
                .lastName(rs.getString("last_name")).studentId(rs.getInt("student_id"))
                .subjects(rs.getInt("subjects"))
                .dateAdded(rs.getObject("date_added", LocalDateTime.class).atZone(ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.systemDefault()))
                .build();
    }

}
