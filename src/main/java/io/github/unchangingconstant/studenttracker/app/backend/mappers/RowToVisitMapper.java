package io.github.unchangingconstant.studenttracker.app.backend.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;

public class RowToVisitMapper implements RowMapper<Visit> {

        @Override // time to hang myself for writing this
        public Visit map(ResultSet rs, StatementContext ctx) throws SQLException {
                return Visit.builder()
                        .visitId(rs.getInt("visit_id"))
                        .studentId(rs.getInt("student_id"))
                        .startTime(Instant.ofEpochMilli(rs.getLong("start_time")))
                        .endTime(Instant.ofEpochMilli(rs.getLong("end_time")))
                        .build();
        }

}
