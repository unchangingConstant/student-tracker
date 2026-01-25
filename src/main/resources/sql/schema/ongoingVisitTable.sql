CREATE TABLE IF NOT EXISTS ongoing_visits (
    start_time INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);