CREATE TABLE IF NOT EXISTS visits (
    visit_id INTEGER PRIMARY KEY AUTOINCREMENT,
    start_time INTEGER NOT NULL,
    end_time INTEGER,
    student_id INTEGER,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT
);