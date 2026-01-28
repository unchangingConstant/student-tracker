CREATE TABLE IF NOT EXISTS visits (
    visit_id INTEGER PRIMARY KEY AUTOINCREMENT,
    start_time INTEGER NOT NULL,
    duration INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);