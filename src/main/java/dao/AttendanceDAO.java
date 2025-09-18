package dao;

import java.util.List;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.sqlobject.config.RegisterFieldMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.sqlite.SQLiteDataSource;

import domainentities.Student;

// Read up on mappers, chapter 7 of JDBI docs
public interface AttendanceDAO {

    @SqlQuery("SELECT * FROM students")
    @RegisterFieldMapper(Student.class)
    public List<Student> getAllStudents();

    @SqlUpdate("INSERT INTO students (firstName, middleName, lastName) VALUES (?, ?, ?)")
    @RegisterFieldMapper(Student.class)
    public boolean insertStudent(Student student);

    @SqlUpdate("DELETE FROM students WHERE studentId=?")
    public boolean deleteStudent(Integer studentId);

    /**
     * Returns an instance of an implementation of this interface
     */
    public static AttendanceDAO getAttendanceDAO(SQLiteDataSource dataSource) {
        return Jdbi.create(dataSource).installPlugin(new SqlObjectPlugin()).onDemand(AttendanceDAO.class);
    }

}
