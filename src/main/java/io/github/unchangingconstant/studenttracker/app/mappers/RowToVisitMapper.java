package io.github.unchangingconstant.studenttracker.app.mappers;

import io.github.unchangingconstant.studenttracker.app.entities.Visit;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class RowToVisitMapper implements RowMapper<Visit> {

    @Override
    public Visit map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Visit.builder().build(); // TODO write
    }

}
