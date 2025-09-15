package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

public class SQLiteAccess {

    private final QueryRunner queryRunner;

    public SQLiteAccess(DataSource dataSource) {
        this.queryRunner = new QueryRunner(dataSource);
    }

    public List<String> getStudents(String tableName) {
        List<Map<String, Object>> result = null;
        try {
            result = queryRunner.query(
                    "SELECT * FROM students", new MapListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> students = result.stream()
                .map(user -> (String) user.get("firstName"))
                .collect(Collectors.toList());

        return students;
    }

    private void doc() {
        // QueryRunner queryRunner = new QueryRunner(null);

        // Select all - returns List<Map<String, Object>>
        // List<Map<String, Object>> users = queryRunner.query(
        // "SELECT * FROM users", new MapListHandler());

        // Select one record
        // Map<String, Object> user = queryRunner.query(
        // "SELECT * FROM users WHERE id = ?", new MapHandler(), yup);

        // Count query
        // Long count = queryRunner.query(
        // "SELECT COUNT(*) FROM users", new ScalarHandler<Long>());
    }

    /**
     * Initializes a database with the schema this application is expecting
     */
    public static DataSource initializeSQLiteDatabase() {
        return null;
    }

}
