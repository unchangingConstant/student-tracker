package com.github.unchangingconstant.studenttracker.app.mappers.domain;

import com.github.unchangingconstant.studenttracker.app.entities.OngoingVisit;
import com.github.unchangingconstant.studenttracker.app.entities.Student;
import org.apache.commons.lang3.tuple.Pair;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RowToStudentOngoingVisitPairMapper implements RowMapper<Pair<Student, OngoingVisit>> {
    @Override
    public Pair<Student, OngoingVisit> map(ResultSet rs, StatementContext ctx) throws SQLException {
        return Pair.of(
            new RowToStudentMapper().map(rs, ctx),
            new RowToOngoingVisitMapper().map(rs, ctx)
        );
    }
}
