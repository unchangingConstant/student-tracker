package io.github.unchangingconstant.studenttracker.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.sqlite.SQLiteDataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;

public class DAOModule extends AbstractModule {

    @Provides
    @Singleton
    // TODO AHHHHH CAST IT INTO THE FIRE, DESTROY IT!!!!!
    public static DatabaseDAO provideDatabaseDAO() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(String.format("jdbc:sqlite:%s", "database.db"));
        try {
            Connection conn = dataSource.getConnection();
            // TODO USE RESOURCES FOR THIS
            conn.createStatement()
                    .execute(
                            "CREATE TABLE IF NOT EXISTS students (" + //
                                    "    student_id INTEGER PRIMARY KEY AUTOINCREMENT," + //
                                    "    first_name TEXT NOT NULL," + //
                                    "    middle_name TEXT," + //
                                    "    last_name TEXT NOT NULL," + //
                                    "    subjects INTEGER" + //
                                    ");" + //
                                    "CREATE TABLE IF NOT EXISTS visits (" + //
                                    "    visit_id INTEGER PRIMARY KEY AUTOINCREMENT," + //
                                    "    start_time DATETIME NOT NULL," + //
                                    "    end_time DATETIME," + //
                                    "    student_id INTEGER," + //
                                    "    FOREIGN KEY (student_id) REFERENCES Students(student_id)" + //
                                    ");" + //
                                    "");
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
        return Jdbi.create(dataSource).installPlugin(new SqlObjectPlugin()).onDemand(DatabaseDAO.class);
    }

    @Provides
    public String provideSchemaSQLString() {
        return null;
    }

}
