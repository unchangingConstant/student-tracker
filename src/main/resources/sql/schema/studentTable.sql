CREATE TABLE IF NOT EXISTS students (
    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    preferred_name TEXT NOT NULL,
    visit_time INTEGER NOT NULL,
    date_added INTEGER NOT NULL
);
