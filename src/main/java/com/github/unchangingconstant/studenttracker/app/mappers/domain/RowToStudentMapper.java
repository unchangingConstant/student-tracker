package com.github.unchangingconstant.studenttracker.app.mappers.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.github.unchangingconstant.studenttracker.app.entities.Student;

public class RowToStudentMapper implements RowMapper<Student> {

    @Override
    public Student map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Student.builder()
            .fullName(rs.getString("full_name"))
            .preferredName(rs.getString("preferred_name"))
            .studentId(rs.getInt("student_id"))
            .visitTime(rs.getInt("visit_time"))
            .dateAdded(Instant.ofEpochMilli(rs.getLong("date_added")))
            .build();
    }
}
