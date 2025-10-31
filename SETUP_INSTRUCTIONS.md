# University ERP System - Setup Instructions

## Overview
This is a **Java Swing Desktop Application** for managing university courses, enrollments, and grades. It features role-based access control (Admin, Instructor, Student) and maintenance mode functionality.

## Important Note
⚠️ **This is a desktop GUI application** that requires a graphical display environment (X11/Windows/macOS). It cannot run properly in a web browser or headless environment.

## Database Setup (Already Configured in Replit)
The application uses PostgreSQL with the following tables:
- **users_auth** - User authentication (username, password hash, role)
- **students** - Student profiles
- **instructors** - Instructor profiles  
- **courses** - Course catalog
- **sections** - Course sections
- **enrollments** - Student enrollments
- **grades** - Grade records
- **settings** - System settings (maintenance mode)

## Running Locally

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- PostgreSQL 12+

### Steps
1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd university-erp
   ```

2. **Set up PostgreSQL**
   ```bash
   # Create databases (if using separate DBs)
   createdb auth_db
   createdb erp_db
   
   # Or use single database and run schema
   psql -d your_database -f database/auth_db_schema.sql
   psql -d your_database -f database/erp_db_schema.sql
   ```

3. **Configure Database Connection**
   Edit `src/main/resources/database.properties` or set environment variables:
   ```properties
   DATABASE_URL=jdbc:postgresql://localhost:5432/your_database
   PGUSER=your_username
   PGPASSWORD=your_password
   ```

4. **Build the Project**
   ```bash
   mvn clean compile
   mvn package
   ```

5. **Run the Application**
   ```bash
   # Option 1: Using Maven
   mvn exec:java -Dexec.mainClass="edu.univ.erp.Main"
   
   # Option 2: Using JAR
   java -jar target/university-erp-1.0-SNAPSHOT.jar
   ```

## Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin1 | admin123 |
| Instructor | inst1 | inst123 |
| Student 1 | stu1 | stu123 |
| Student 2 | stu2 | stu123 |

## Features by Role

### Student Features
- ✅ Browse course catalog with available seats
- ✅ Register for sections (with capacity validation)
- ✅ Drop sections
- ✅ View personal timetable
- ✅ View grades (Quiz, Midterm, End-Sem, Final)
- ✅ Download transcript (CSV format)

### Instructor Features
- ✅ View assigned sections
- ✅ Enter assessment scores (Quiz, Midterm, End-Sem)
- ✅ Compute final grades using weighted formula:
  - Quiz: 20%
  - Midterm: 30%
  - End-Sem: 50%
- ✅ View class statistics

### Admin Features
- ✅ Create users (Students, Instructors, Admins)
- ✅ Manage courses (create, view)
- ✅ Manage sections (create, assign instructors)
- ✅ Toggle Maintenance Mode
  - When ON: Students/Instructors can only view data
  - Visual banner displayed throughout the system

## Architecture

### Package Structure
```
src/main/java/edu/univ/erp/
├── Main.java                    # Application entry point
├── ui/                          # Swing UI components
│   ├── auth/                    # Login screens
│   ├── student/                 # Student panels
│   ├── instructor/              # Instructor panels
│   ├── admin/                   # Admin panels
│   └── common/                  # Shared UI components
├── domain/                      # Data models (POJOs)
├── service/                     # Business logic layer
├── data/                        # Database access layer
├── auth/                        # Authentication
│   ├── hash/                    # BCrypt password hashing
│   ├── store/                   # Auth database access
│   └── session/                 # Session management
├── access/                      # Access control & permissions
└── util/                        # Utilities (CSV export, validation)
```

### Security Features
- ✅ BCrypt password hashing (UNIX shadow-style)
- ✅ Separate authentication layer
- ✅ Role-based access control
- ✅ Maintenance mode enforcement
- ✅ Input validation
- ✅ No passwords stored in ERP database

### Dual Database Architecture
- **Auth DB**: User credentials, roles, password hashes
- **ERP DB**: All academic data (students, courses, enrollments, grades)
- Linked via `user_id` foreign key

## Grading System
- **Components**: Quiz, Midterm, End-Sem
- **Weights**: 20%, 30%, 50%
- **Letter Grades**:
  - A: 90-100
  - B: 80-89
  - C: 70-79
  - D: 60-69
  - F: 0-59

## Sample Data
The system comes pre-populated with:
- 1 Admin user
- 1 Instructor (teaching 5 sections)
- 2 Students (with active enrollments)
- 5 Courses (CSE101, CSE201, CSE301, MTH101, PHY101)
- 5 Sections for Spring 2025
- Sample grades for student enrollments

## Testing Checklist

### Login & Authentication
- [ ] Admin login with admin1/admin123
- [ ] Instructor login with inst1/inst123
- [ ] Student login with stu1/stu123
- [ ] Wrong password is rejected

### Student Operations
- [ ] Browse course catalog
- [ ] Register for section with available seats
- [ ] Cannot register for same section twice
- [ ] Cannot register for full section
- [ ] Drop section successfully
- [ ] View timetable
- [ ] View grades
- [ ] Download transcript CSV

### Instructor Operations
- [ ] View assigned sections
- [ ] Enter quiz scores
- [ ] Enter midterm scores
- [ ] Enter end-sem scores
- [ ] Compute final grades
- [ ] Cannot modify other instructor's sections

### Admin Operations
- [ ] Create new student
- [ ] Create new instructor
- [ ] Create new admin
- [ ] Create new course
- [ ] Create new section
- [ ] Assign instructor to section
- [ ] Toggle maintenance mode ON
- [ ] Toggle maintenance mode OFF

### Maintenance Mode
- [ ] Banner displays when ON
- [ ] Student cannot register/drop when ON
- [ ] Instructor cannot enter grades when ON
- [ ] Admin can still modify when ON
- [ ] Normal operation resumes when OFF

## Technical Stack
- **Language**: Java 11
- **UI Framework**: Swing
- **Database**: PostgreSQL (JDBC)
- **Password Hashing**: BCrypt
- **Build Tool**: Maven
- **CSV Export**: OpenCSV
- **PDF Generation**: Apache PDFBox

## Project Submission Components

1. ✅ Working Application
2. ✅ Sample Data (pre-seeded)
3. ✅ Documentation (README, this file)
4. ✅ Database Schemas (auth_db_schema.sql, erp_db_schema.sql)
5. ✅ Source Code (fully modular architecture)
6. ✅ Testing Instructions

## Troubleshooting

### Database Connection Issues
```bash
# Check PostgreSQL is running
pg_isready

# Verify connection details
psql -h localhost -U postgres -d your_database
```

### Compilation Errors
```bash
# Clean and rebuild
mvn clean
mvn compile
```

### GUI Not Displaying
- Ensure you're running on a system with graphical display
- Check Java AWT/Swing is properly installed
- Try running with: `java -Djava.awt.headless=false -jar target/university-erp-1.0-SNAPSHOT.jar`

## License
Academic project for educational purposes.
