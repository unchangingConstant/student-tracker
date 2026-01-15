package com.github.unchangingconstant.studenttracker.app.mappers.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.github.unchangingconstant.studenttracker.app.domain.OngoingVisit;

public class RowToOngoingVisitMapper implements RowMapper<OngoingVisit>{

    @Override
    public OngoingVisit map(ResultSet rs, StatementContext ctx) throws SQLException {
        return OngoingVisit.builder()
            .studentId(rs.getInt("student_id"))
            .startTime(Instant.ofEpochMilli(rs.getLong("start_time")))
            .studentName(rs.getString("full_legal_name"))
            .subjects(rs.getInt("subjects"))
            .build();
    }

}
