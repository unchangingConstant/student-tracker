package io.github.unchangingconstant.studenttracker.app.mappers.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import io.github.unchangingconstant.studenttracker.app.domain.OngoingVisitDomain;

public class RowToOngoingVisitMapper implements RowMapper<OngoingVisitDomain>{

    @Override
    public OngoingVisitDomain map(ResultSet rs, StatementContext ctx) throws SQLException {
        return OngoingVisitDomain.builder()
            .studentId(rs.getInt("student_id"))
            .startTime(Instant.ofEpochMilli(rs.getLong("start_time")))
            .studentName(rs.getString("full_legal_name"))
            .build();
    }

}
