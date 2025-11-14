CREATE TABLE IF NOT EXISTS students (
    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_legal_name TEXT NOT NULL,
    preferred_name TEXT NOT NULL,
    subjects INTEGER NOT NULL,
    date_added INTEGER NOT NULL
);

