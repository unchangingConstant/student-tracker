package dao;

import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

// TODO add helper to avoid try catch blocks
public class AttendanceDatabaseAccess {

    // private final QueryRunner queryRunner;

    public AttendanceDatabaseAccess(DataSource dataSource) {
        // this.queryRunner = new QueryRunner(dataSource);
    }

    public List<Map<String, Object>> getAllStudents() {
        List<Map<String, Object>> students = null;
        // try {
        // students = queryRunner.query(
        // "SELECT * FROM students", new MapListHandler());
        // } catch (SQLException e) {
        // e.printStackTrace();
        // }

        return students;
    }

    public Integer insertStudent(Map<String, Object> row) {
        Integer id = null;
        // try {
        // id = queryRunner.insert(
        // "INSERT INTO students (firstName, middleName, lastName) VALUES (?, ?, ?)",
        // new ScalarHandler<Integer>(), row.get("firstName"),
        // row.get("middleName"), row.get("lastName"));
        // } catch (SQLException e) {
        // throw new RuntimeException(e);
        // }
        return id;
    }

    public void deleteStudent(Integer studentId) {
        throw new UnsupportedOperationException();
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
