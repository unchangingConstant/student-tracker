package io.github.unchangingconstant.studenttracker.app.backend.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import io.github.unchangingconstant.studenttracker.app.backend.entities.Visit;

public class RowToVisitMapper implements RowMapper<Visit> {

        @Override // time to hang myself for writing this
        public Visit map(ResultSet rs, StatementContext ctx) throws SQLException {
                return Visit.builder().visitId(rs.getInt("visit_id")).studentId(rs.getInt("student_id"))
                                .startTime(rs.getTimestamp("start_time").toLocalDateTime())
                                .endTime(rs.getTimestamp("end_time").toLocalDateTime())
                                .studentName(String.format("%s%s %s", rs.getString("first_name"),
                                                rs.getString("middle_name").equals(null) ? ""
                                                                : " " + rs.getString("middle_name").charAt(0) + ".",
                                                rs.getString("last_name")))
                                .build();
        }

}
