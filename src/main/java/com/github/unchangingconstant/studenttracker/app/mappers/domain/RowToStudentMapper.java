package com.github.unchangingconstant.studenttracker.app.mappers.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.github.unchangingconstant.studenttracker.app.domain.StudentDomain;

public class RowToStudentMapper implements RowMapper<StudentDomain> {

    @Override
    public StudentDomain map(ResultSet rs, StatementContext ctx) throws SQLException {
        return StudentDomain.builder()
            .fullLegalName(rs.getString("full_legal_name"))
            .prefName(rs.getString("preferred_name"))
            .studentId(rs.getInt("student_id"))
            .subjects(rs.getInt("subjects"))
            .dateAdded(Instant.ofEpochMilli(rs.getLong("date_added")))
            .build();
    }

}
