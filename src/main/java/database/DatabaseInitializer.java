package database;

import org.sqlite.SQLiteDataSource;

/**
 * Stores all util methods relating to setting up in-memory / on disk databases
 * for development, testing, and deployment
 */
public class DatabaseInitializer {

    public static SQLiteDataSource initializeDatabase(String databaseFilePath) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(String.format("jdbc:sqlite:%s", databaseFilePath));
        return dataSource;
    }

    public static SQLiteDataSource initializeInMemoryDatabase() {
        return initializeDatabase(":memory:");
    }

}
