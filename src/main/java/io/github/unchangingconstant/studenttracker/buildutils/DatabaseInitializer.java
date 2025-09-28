package io.github.unchangingconstant.studenttracker.buildutils;

import org.sqlite.SQLiteDataSource;

/**
 * Stores all util methods relating to setting up in-memory / on disk databases
 * for development, testing, and deployment
 */
public class DatabaseInitializer {

    // TODO Change this to use maven profile to pull database file path
    public static SQLiteDataSource initializeDatabase(String databaseFilePath) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(String.format("jdbc:sqlite:%s", databaseFilePath));
        return dataSource;
    }

    public static SQLiteDataSource initializeInMemoryDatabase() {
        return initializeDatabase(":memory:");
    }

}
