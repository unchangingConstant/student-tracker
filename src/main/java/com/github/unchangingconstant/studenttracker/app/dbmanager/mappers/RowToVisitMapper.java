package com.github.unchangingconstant.studenttracker.app.dbmanager.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.github.unchangingconstant.studenttracker.app.entities.Visit;

public class RowToVisitMapper implements RowMapper<Visit> {

        @Override
        public Visit map(ResultSet rs, StatementContext ctx) throws SQLException {
                return Visit.builder()
                        .visitId(rs.getInt("visit_id"))
                        .studentId(rs.getInt("student_id"))
                        .startTime(Instant.ofEpochMilli(rs.getLong("start_time")))
                        .duration(rs.getInt("duration"))
                        .build();
        }

}
