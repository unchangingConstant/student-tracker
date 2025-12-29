package com.github.unchangingconstant.studenttracker.app.mappers.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import com.github.unchangingconstant.studenttracker.app.domain.VisitDomain;

public class RowToVisitMapper implements RowMapper<VisitDomain> {

        @Override // time to hang myself for writing this
        public VisitDomain map(ResultSet rs, StatementContext ctx) throws SQLException {
                return VisitDomain.builder()
                        .visitId(rs.getInt("visit_id"))
                        .studentId(rs.getInt("student_id"))
                        .startTime(Instant.ofEpochMilli(rs.getLong("start_time")))
                        .endTime(Instant.ofEpochMilli(rs.getLong("end_time")))
                        .build();
        }

}
