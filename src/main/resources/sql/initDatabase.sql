CREATE TABLE IF NOT EXISTS students (
    studentId INTEGER PRIMARY KEY AUTOINCREMENT,
    firstName TEXT NOT NULL,
    middleName TEXT,
    lastName TEXT NOT NULL,
    subjects INTEGER,
);
