package io.github.unchangingconstant.studenttracker.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.sqlite.SQLiteDataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import io.github.unchangingconstant.studenttracker.app.dao.DatabaseDAO;

public class DAOModule extends AbstractModule {

    @Provides
    // TODO AHHHHH CAST IT INTO THE FIRE, DESTROY IT!!!!!
    public static DatabaseDAO provideDatabaseDAO() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(String.format("jdbc:sqlite:%s", "database.db"));
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
        return Jdbi.create(dataSource).installPlugin(new SqlObjectPlugin()).onDemand(DatabaseDAO.class);
    }

}
