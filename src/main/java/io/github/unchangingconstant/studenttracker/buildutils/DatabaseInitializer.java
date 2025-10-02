package io.github.unchangingconstant.studenttracker.buildutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.sqlite.SQLiteDataSource;

/**
 * Stores all util methods relating to setting up in-memory / on disk databases
 * for development, testing, and deployment
 * 
 * TODO clean this the fuck up,
 */
public class DatabaseInitializer {

    private static String initDatabase = "sql/initDatabase.sql";

    // TODO Change this to use maven profile to pull database file path
    public static SQLiteDataSource initializeDatabase(String databaseFilePath) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(String.format("jdbc:sqlite:%s", databaseFilePath));
        createTables(dataSource);
        return dataSource;
    }

    // public static SQLiteDataSource initializeInMemoryDatabase() {
    // SQLiteDataSource dataSource = new SQLiteDataSource();
    // dataSource.setUrl("jdbc:sqlite::memory:");
    // createTables(dataSource);
    // return dataSource;
    // }

    private static void createTables(SQLiteDataSource dataSource) {
        try {
            Connection conn = dataSource.getConnection();
            // TODO USE RESOURCES FOR THIS
            conn.createStatement()
                    .execute(
                            "CREATE TABLE IF NOT EXISTS students (studentId INTEGER PRIMARY KEY AUTOINCREMENT, firstName TEXT NOT NULL, middleName TEXT, lastName TEXT NOT NULL, subjects INTEGER);");
            ResultSet rs = conn.getMetaData().getTables(null, null, "%", null);
            System.out.println("Tables in database:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString("TABLE_NAME"));
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
