DROP TABLE IF EXISTS grades CASCADE;
DROP TABLE IF EXISTS grading_criteria CASCADE;
DROP TABLE IF EXISTS enrollments CASCADE;
DROP TABLE IF EXISTS section_times CASCADE;
DROP TABLE IF EXISTS sections CASCADE;
DROP TABLE IF EXISTS courses CASCADE;
DROP TABLE IF EXISTS instructors CASCADE;
DROP TABLE IF EXISTS students CASCADE;
DROP TABLE IF EXISTS settings CASCADE;

CREATE TABLE students (
    user_id INTEGER PRIMARY KEY,
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,    
    last_name VARCHAR(100) NOT NULL,     
    program VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL CHECK (year >= 1 AND year <= 5)
);
CREATE TABLE instructors (
    user_id INTEGER PRIMARY KEY,
    salutation VARCHAR(10),              
    first_name VARCHAR(100) NOT NULL,    
    last_name VARCHAR(100) NOT NULL,     
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
    room VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    semester VARCHAR(20) NOT NULL,
    year INTEGER NOT NULL
);

CREATE TABLE section_times (
    id SERIAL PRIMARY KEY,
    section_id INTEGER NOT NULL REFERENCES sections(section_id) ON DELETE CASCADE,
    day_of_week VARCHAR(10) NOT NULL CHECK (
        day_of_week IN ('Mon','Tue','Wed','Thu','Fri','Sat','Sun')
    ),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CHECK (end_time > start_time)
);

CREATE TABLE enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(user_id), 
    section_id INTEGER NOT NULL REFERENCES sections(section_id),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DROPPED')),
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(student_id, section_id, status)
);

CREATE TABLE grades (
    grade_id SERIAL PRIMARY KEY,
    enrollment_id INTEGER NOT NULL REFERENCES enrollments(enrollment_id),
    component VARCHAR(100) NOT NULL,
    score DOUBLE PRECISION CHECK (score >= 0 AND score <= 100),
    UNIQUE(enrollment_id, component)
);


CREATE TABLE grading_criteria (
    criteria_id SERIAL PRIMARY KEY,
    section_id INTEGER NOT NULL REFERENCES sections(section_id) ON DELETE CASCADE,
    component_name VARCHAR(100) NOT NULL,
    weight_percentage DECIMAL(5, 2) NOT NULL CHECK (weight_percentage > 0 AND weight_percentage <= 100),
    display_order INTEGER NOT NULL DEFAULT 0,
    UNIQUE(section_id, component_name) 
);

CREATE TABLE settings (
    key VARCHAR(50) PRIMARY KEY,
    value VARCHAR(255)
);

-- SAMPLE DATA UPDATES

INSERT INTO students (user_id, roll_no, first_name, last_name, program, year) VALUES
(3, 'STU2025001', 'Abhinav', 'Arya', 'Computer Science', 2),
(4, 'STU2025002', 'Mohit', 'Sharma', 'Electrical Engineering', 1);

INSERT INTO instructors (user_id, salutation, first_name, last_name, department) VALUES
(2, 'Prof.', 'Ashok', 'Mittal', 'Computer Science'),
(3, 'Dr.', 'Neha', 'Gupta', 'Mathematics');

INSERT INTO courses (code, title, credits) VALUES
('CSE101', 'Introduction to Programming', 4),
('CSE201', 'Data Structures', 4),
('CSE301', 'Database Systems', 3),
('MTH101', 'Calculus I', 4),
('PHY101', 'Physics I', 3);

INSERT INTO sections (course_id, instructor_id, room, capacity, semester, year) VALUES
(1, 2, 'Room 101', 50, 'Monsoon', 2025),
(2, 2, 'Room 102', 40, 'Monsoon', 2025),
(3, 2, 'Room 103', 35, 'Monsoon', 2025),
(4, 2, 'Room 104', 60, 'Monsoon', 2025),
(5, 2, 'Lab 201', 30, 'Monsoon', 2025);

INSERT INTO section_times (section_id, day_of_week, start_time, end_time) VALUES
(1, 'Mon', '09:00', '10:30'),
(1, 'Wed', '09:00', '10:30'),
(2, 'Tue', '11:00', '12:30'),
(2, 'Thu', '11:00', '12:30'),
(3, 'Mon', '14:00', '15:30'),
(3, 'Wed', '14:00', '15:30'),
(4, 'Tue', '09:00', '10:30'),
(4, 'Thu', '09:00', '10:30'),
(5, 'Fri', '14:00', '17:00');

INSERT INTO enrollments (student_id, section_id, status) VALUES
(3, 1, 'ACTIVE'),
(3, 2, 'ACTIVE'),
(4, 1, 'ACTIVE');

INSERT INTO grading_criteria (section_id, component_name, weight_percentage, display_order) VALUES
(1, 'Homework', 20.00, 10),
(1, 'Quiz', 15.00, 20),
(1, 'Midsem', 30.00, 30),
(1, 'Endsem', 35.00, 40);

INSERT INTO grading_criteria (section_id, component_name, weight_percentage, display_order) VALUES
(2, 'Lab Reports', 40.00, 10),
(2, 'Midsem', 30.00, 20),
(2, 'Endsem', 30.00, 30);


INSERT INTO settings (key, value) VALUES
('maintenance_on', 'false'),
('add_drop_period_on','true');
