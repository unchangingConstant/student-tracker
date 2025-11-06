package io.github.unchangingconstant.studenttracker.app.backend.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import io.github.unchangingconstant.studenttracker.app.backend.entities.OngoingVisit;

public class RowToOngoingVisitMapper implements RowMapper<OngoingVisit>{

    @Override
    public OngoingVisit map(ResultSet rs, StatementContext ctx) throws SQLException {
        return OngoingVisit.builder()
            .studentId(rs.getInt("student_id"))
            .startTime(Instant.ofEpochMilli(rs.getLong("start_time")))
            .studentName(String.format("%s%s %s", rs.getString("first_name"),
                rs.getString("middle_name").equals("") ? "" : " " + rs.getString("middle_name").charAt(0) + ".",
                rs.getString("last_name")))
            .build();
    }

}
