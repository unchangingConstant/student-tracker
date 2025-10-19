CREATE TABLE IF NOT EXISTS visits (
    visit_id INTEGER PRIMARY KEY AUTOINCREMENT,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    student_id INTEGER,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT
);