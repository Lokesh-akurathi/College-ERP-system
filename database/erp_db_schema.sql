-- ERP Database Schema
-- This database stores all academic data

DROP TABLE IF EXISTS grades CASCADE;
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS sections CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS instructors CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS settings CASCADE;

CREATE TABLE students (
    user_id INTEGER PRIMARY KEY,
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    program VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL CHECK (year >= 1 AND year <= 5)
);

CREATE TABLE instructors (
    user_id INTEGER PRIMARY KEY,
    department VARCHAR(100) NOT NULL
);

CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    credits INTEGER NOT NULL CHECK (credits > 0)
);

CREATE TABLE sections (
    section_id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL REFERENCES courses(course_id),
    instructor_id INTEGER NOT NULL,
    day_time VARCHAR(100) NOT NULL,
    room VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    semester VARCHAR(20) NOT NULL,
    year INTEGER NOT NULL
);

CREATE TABLE enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,
    section_id INTEGER NOT NULL REFERENCES sections(section_id),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DROPPED')),
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, section_id, status)
);

CREATE TABLE grades (
    grade_id SERIAL PRIMARY KEY,
    enrollment_id INTEGER NOT NULL REFERENCES enrollments(enrollment_id),
    component VARCHAR(20) NOT NULL,
    score DOUBLE PRECISION CHECK (score >= 0 AND score <= 100),
    final_grade VARCHAR(2)
);

CREATE TABLE settings (
    key VARCHAR(50) PRIMARY KEY,
    value VARCHAR(255)
);

-- Sample data
INSERT INTO students (user_id, roll_no, program, year) VALUES
(3, 'STU2025001', 'Computer Science', 2),
(4, 'STU2025002', 'Electrical Engineering', 1);

INSERT INTO instructors (user_id, department) VALUES
(2, 'Computer Science');

INSERT INTO courses (code, title, credits) VALUES
('CSE101', 'Introduction to Programming', 4),
('CSE201', 'Data Structures', 4),
('CSE301', 'Database Systems', 3),
('MTH101', 'Calculus I', 4),
('PHY101', 'Physics I', 3);

INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year) VALUES
(1, 2, 'Mon/Wed 9:00-10:30', 'Room 101', 50, 'Monsoon', 2025),
(2, 2, 'Tue/Thu 11:00-12:30', 'Room 102', 40, 'Monsoon', 2025),
(3, 2, 'Mon/Wed 14:00-15:30', 'Room 103', 35, 'Monsoon', 2025),
(4, 2, 'Tue/Thu 9:00-10:30', 'Room 104', 60, 'Monsoon', 2025),
(5, 2, 'Fri 14:00-17:00', 'Lab 201', 30, 'Monsoon', 2025);

INSERT INTO enrollments (student_id, section_id, status) VALUES
(3, 1, 'ACTIVE'),
(3, 2, 'ACTIVE'),
(4, 1, 'ACTIVE');

INSERT INTO grades (enrollment_id, component, score, final_grade) VALUES
(1, 'QUIZ', 85.0, NULL),
(1, 'MIDTERM', 78.0, NULL),
(1, 'ENDSEM', 82.0, NULL),
(1, 'FINAL', 81.4, 'B');

INSERT INTO settings (key, value) VALUES
('maintenance_on', 'false');
