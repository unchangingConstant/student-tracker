package io.github.unchangingconstant.studenttracker.setup;

import java.io.IOException;

import org.jdbi.v3.core.Jdbi;

import io.github.unchangingconstant.studenttracker.utils.ScriptLoader;

public class DatabaseSetup {
    // TODO OMG READ section 1.14.3 OF JDBI DOCS!!!!
    public static void setUpDatabase() {
        Jdbi jdbi = Jdbi.create("jdbc:sqlite:database.db");
        String createSchema = ScriptLoader.loadSqlScript("/sql/schema.sql");
        jdbi.withHandle(handle -> handle.execute(createSchema));
    }
}
