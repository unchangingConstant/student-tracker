package io.github.unchangingconstant.studenttracker.setup;

import java.io.IOException;

import org.jdbi.v3.core.Jdbi;

import io.github.unchangingconstant.studenttracker.utils.ResourceLoader;

public class DatabaseSetup {
    // TODO OMG READ section 1.14.3 OF JDBI DOCS!!!!
    public static void setUpDatabase() {
        Jdbi jdbi = Jdbi.create("jdbc:sqlite:database.db");
        String studentTable = ResourceLoader.loadResource("/sql/schema/studentTable.sql");
        String visitTable = ResourceLoader.loadResource("/sql/schema/visitTable.sql");
        String ongoingVisitTable = ResourceLoader.loadResource("/sql/schema/ongoingVisitTable.sql");
        jdbi.withHandle(handle -> handle.execute(studentTable));
        jdbi.withHandle(handle -> handle.execute(visitTable));
        jdbi.withHandle(handle -> handle.execute(ongoingVisitTable));
    }

}
