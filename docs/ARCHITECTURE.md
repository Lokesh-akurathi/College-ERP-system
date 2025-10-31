# University ERP System - Architecture Documentation

## Overview
This document explains the system architecture, design decisions, and implementation details.

## Dual-Database Architecture

### Conceptual Design (From Specification)
The specification requires:
1. **Auth DB**: Stores user authentication data (users_auth table)
2. **ERP DB**: Stores all academic data (students, courses, enrollments, grades, etc.)
3. **Linking**: Both DBs linked via `user_id` foreign key

### Replit Implementation
**Important Note**: Replit provides a single PostgreSQL database instance. Our implementation:
- Uses **one physical database** containing all tables
- Maintains **logical separation** through DatabaseConfig API:
  - `getAuthConnection()` - conceptually for auth operations
  - `getErpConnection()` - conceptually for ERP operations
- Both methods currently return connections to the same database
- This allows JOINs between users_auth and other tables to work

### Why This Approach?
1. **Replit Constraint**: Only one PostgreSQL database available
2. **Specification Compliance**: Maintains conceptual separation in code
3. **Future Flexibility**: Easy to split into two physical databases by:
   - Creating separate auth_db and erp_db
   - Updating `DatabaseConfig.java` connection strings
   - Removing JOINs with users_auth (fetch separately)

### Production Deployment Considerations
For true dual-database deployment:

**Option 1: Keep Single Database** (Simpler)
- Use single PostgreSQL instance
- Separate tables logically
- ✅ Current implementation works as-is
- ✅ JOINs work efficiently
- ❌ Less security isolation

**Option 2: Split into Two Databases** (More Secure)
- Create separate auth_db and erp_db
- Requires code changes:
  ```java
  // Instead of:
  SELECT s++.*, u.username FROM students s JOIN users_auth u ...
  
  // Do:
  Student s = studentStore.findById(userId);  // ERP DB
  String username = authStore.getUsernameById(userId);  // Auth DB
  s.setUsername(username);
  ```
- ✅ Better security isolation
- ✅ Meets specification literally
- ❌ More network round-trips
- ❌ No cross-database JOINs

## Package Structure

### Layer Separation

```
edu.univ.erp/
├── Main.java                   # Entry point
├── ui/                         # Presentation Layer (Swing)
│   ├── auth/                   # Login screens
│   ├── student/                # Student panels
│   ├── instructor/             # Instructor panels
│   ├── admin/                  # Admin panels
│   └── common/                 # Shared UI components
├── service/                    # Business Logic Layer
│   ├── UserService             # User management logic
│   ├── EnrollmentService       # Registration/drop logic
│   ├── GradeService            # Grading logic
│   ├── CourseService           # Course/section management
│   └── SettingsService         # Maintenance mode
├── data/                       # Data Access Layer (DAO pattern)
│   ├── StudentStore            # Student CRUD
│   ├── InstructorStore         # Instructor CRUD
│   ├── CourseStore             # Course CRUD
│   ├── SectionStore            # Section CRUD
│   ├── EnrollmentStore         # Enrollment CRUD
│   ├── GradeStore              # Grade CRUD
│   └── SettingsStore           # Settings CRUD
├── auth/                       # Authentication Layer
│   ├── AuthService             # Login/logout logic
│   ├── hash/PasswordHasher     # BCrypt hashing
│   ├── store/AuthStore         # Auth DB access
│   └── session/SessionManager  # Session state
├── access/                     # Authorization Layer
│   └── AccessControl           # Permission checks
├── domain/                     # Domain Models (POJOs)
│   ├── User
│   ├── Student
│   ├── Instructor
│   ├── Course
│   ├── Section
│   ├── Enrollment
│   ├── Grade
│   └── Settings
└── util/                       # Utilities
    ├── DatabaseConfig          # Database connections
    ├── CSVExporter             # CSV export
    └── ValidationHelper        # Input validation
```

### Layer Responsibilities

**UI Layer (ui/)**
- Swing components (JFrame, JPanel, JTable)
- User input capture
- Data display
- NO direct database access
- Calls service layer only

**Service Layer (service/)**
- Business logic
- Transaction management
- Access control enforcement
- Maintenance mode checks
- Validation
- Calls data layer

**Data Layer (data/)**
- JDBC operations
- SQL queries
- CRUD operations
- Result set mapping
- Connection management
- NO business logic

**Auth Layer (auth/)**
- Authentication
- Password hashing/verification
- Session management
- User state

**Access Layer (access/)**
- Role checking
- Permission verification
- Maintenance mode state

## Security Architecture

### Password Security

**Hashing Algorithm**: BCrypt (12 rounds)
```java
// PasswordHasher.java
String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
boolean valid = BCrypt.checkpw(plainPassword, storedHash);
```

**Storage**:
- ✅ Passwords stored ONLY in users_auth table
- ✅ Only hashed values stored
- ✅ No plain-text anywhere
- ✅ Follows UNIX shadow file principle

**Password Requirements**:
- Minimum 6 characters
- No complexity requirements (can be enhanced)

### Session Management

**Singleton Pattern**:
```java
SessionManager.getInstance().setCurrentUser(user);
User current = SessionManager.getInstance().getCurrentUser();
```

**Session State**:
- Stores current logged-in user
- Available application-wide
- Cleared on logout
- Not persistent (in-memory only)

### Access Control

**Role-Based Permissions**:
```java
if (!AccessControl.canAccessAdminFeatures()) {
    return AccessControl.getAccessDeniedMessage();
}
```

**Permission Matrix**:

| Feature | Admin | Instructor | Student |
|---------|-------|------------|---------|
| Create Users | ✅ | ❌ | ❌ |
| Manage Courses | ✅ | ❌ | ❌ |
| Manage Sections | ✅ | ❌ | ❌ |
| Enter Grades | ✅ | ✅ (own) | ❌ |
| Register Sections | ❌ | ❌ | ✅ |
| View Own Data | ✅ | ✅ | ✅ |
| Maintenance Toggle | ✅ | ❌ | ❌ |

**Maintenance Mode**:
```java
if (!AccessControl.canModify()) {
    return AccessControl.getMaintenanceDenialMessage();
}
```

When ON:
- Admin: Full access ✅
- Instructor: Read-only ❌
- Student: Read-only ❌

## Database Schema

### Auth Tables

```sql
CREATE TABLE users_auth (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'INSTRUCTOR', 'ADMIN')),
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### ERP Tables

```sql
-- Student profiles
CREATE TABLE students (
    user_id INTEGER PRIMARY KEY,  -- Links to users_auth.user_id
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    program VARCHAR(100) NOT NULL,
    year INTEGER NOT NULL CHECK (year >= 1 AND year <= 5)
);

-- Instructor profiles
CREATE TABLE instructors (
    user_id INTEGER PRIMARY KEY,  -- Links to users_auth.user_id
    department VARCHAR(100) NOT NULL
);

-- Course catalog
CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    code VARCHAR(20) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    credits INTEGER NOT NULL CHECK (credits > 0)
);

-- Course sections
CREATE TABLE sections (
    section_id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL REFERENCES courses(course_id),
    instructor_id INTEGER NOT NULL,  -- Links to users_auth.user_id
    day_time VARCHAR(100) NOT NULL,
    room VARCHAR(50) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    semester VARCHAR(20) NOT NULL,
    year INTEGER NOT NULL
);

-- Student enrollments
CREATE TABLE enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL,  -- Links to users_auth.user_id
    section_id INTEGER NOT NULL REFERENCES sections(section_id),
    status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'DROPPED')),
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Grade records
CREATE TABLE grades (
    grade_id SERIAL PRIMARY KEY,
    enrollment_id INTEGER NOT NULL REFERENCES enrollments(enrollment_id),
    component VARCHAR(20) NOT NULL,  -- 'QUIZ', 'MIDTERM', 'ENDSEM', 'FINAL'
    score DOUBLE PRECISION CHECK (score >= 0 AND score <= 100),
    final_grade VARCHAR(2)  -- 'A', 'B', 'C', 'D', 'F'
);

-- System settings
CREATE TABLE settings (
    key VARCHAR(50) PRIMARY KEY,
    value VARCHAR(255)
);
```

### Relationships

```
users_auth (user_id)
    ├──> students (user_id)
    ├──> instructors (user_id)
    └──> sections (instructor_id)

courses (course_id)
    └──> sections (course_id)

sections (section_id)
    └──> enrollments (section_id)

enrollments (enrollment_id)
    └──> grades (enrollment_id)
```

## Business Logic

### Grading System

**Components**:
- Quiz: 20% weight
- Midterm: 30% weight
- End-Sem: 50% weight

**Calculation**:
```java
double finalScore = (quiz * 0.20) + (midterm * 0.30) + (endsem * 0.50);
String grade = computeLetterGrade(finalScore);
```

**Letter Grades**:
```java
if (score >= 90) return "A";
if (score >= 80) return "B";
if (score >= 70) return "C";
if (score >= 60) return "D";
return "F";
```

### Enrollment Validation

```java
// Duplicate check
if (enrollmentStore.exists(studentId, sectionId)) {
    return "Already registered";
}

// Capacity check
if (section.isFull()) {
    return "Section full";
}

// Success
enrollment.create();
```

### Maintenance Mode

**Storage**: `settings` table
```sql
INSERT INTO settings (key, value) VALUES ('maintenance_on', 'false');
```

**Toggle**:
```java
settingsStore.setSetting("maintenance_on", "true");  // Enable
settingsStore.setSetting("maintenance_on", "false"); // Disable
```

**Check**:
```java
boolean isOn = AccessControl.isMaintenanceMode();
if (isOn && !AccessControl.isAdmin()) {
    // Block modification
}
```

## Data Flow Examples

### Student Registration Flow

```
1. Student clicks "Register" in UI
   ↓
2. CourseCatalogPanel.registerForSection()
   ↓
3. EnrollmentService.registerForSection(sectionId)
   ├─ Check: AccessControl.canModify()
   ├─ Check: EnrollmentStore.exists()
   ├─ Check: SectionStore.isFull()
   └─ Create: EnrollmentStore.create()
   ↓
4. Return success/error message
   ↓
5. UI displays message
```

### Grade Computation Flow

```
1. Instructor clicks "Compute Final Grades"
   ↓
2. GradeEntryPanel.computeFinalGrades()
   ↓
3. For each enrollment:
   GradeService.computeFinalGrade(enrollmentId)
   ├─ Check: AccessControl.canModify()
   ├─ Check: Instructor owns section
   ├─ Fetch: All component grades
   ├─ Validate: All components present
   ├─ Calculate: Final score
   ├─ Determine: Letter grade
   └─ Save: Final grade record
   ↓
4. Return success/error
   ↓
5. UI refreshes grade display
```

## Design Patterns

### Singleton
- `SessionManager`: Single instance of user session
- Ensures one session per application

### DAO (Data Access Object)
- All `*Store` classes
- Encapsulates database operations
- Separates data access from business logic

### MVC (Model-View-Controller)
- Model: `domain/*` classes
- View: `ui/*` classes
- Controller: `service/*` classes

### Service Layer Pattern
- Business logic centralized in services
- UI and data layers decoupled
- Transaction boundaries managed

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 11+ |
| UI Framework | Swing | Built-in |
| Database | PostgreSQL | 12+ |
| JDBC Driver | PostgreSQL JDBC | 42.7.1 |
| Password Hashing | jBCrypt | 0.4 |
| CSV Export | OpenCSV | 5.9 |
| PDF Generation | Apache PDFBox | 2.0.30 |
| Build Tool | Maven | 3.6+ |

## Future Enhancements

### Recommended Improvements
1. **True Dual-Database Deployment**
   - Separate physical databases
   - Remove cross-database JOINs
   - Fetch user data separately

2. **Enhanced Security**
   - Password complexity requirements
   - Login attempt limiting
   - Session timeout
   - HTTPS for database connections

3. **Additional Features**
   - Course prerequisites
   - Email notifications
   - Grade history
   - Attendance tracking
   - Report generation (PDF)

4. **UI Improvements**
   - Sortable/filterable tables
   - Search functionality
   - Date pickers
   - Progress indicators

5. **Testing**
   - Unit tests (JUnit)
   - Integration tests
   - UI tests (AssertJ Swing)

## Performance Considerations

### Database Connections
- Connection pooling (consider HikariCP)
- Statement caching
- Batch operations for bulk updates

### Current Limitations
- No connection pooling
- New connection per operation
- Acceptable for small-scale deployment
- Not suitable for high concurrency

### Scaling Recommendations
- Implement connection pooling
- Add caching layer (Redis)
- Optimize SQL queries
- Add database indexes

## Deployment

### Local Deployment
1. Install Java 11+
2. Install PostgreSQL
3. Create database
4. Run schema scripts
5. Build with Maven
6. Run JAR file

### Server Deployment
- Requires X11 forwarding or VNC
- Desktop environment needed
- Not suitable for cloud/headless servers
- Consider web UI for production

## Conclusion

This architecture provides:
- ✅ Clear separation of concerns
- ✅ Secure authentication
- ✅ Role-based access control
- ✅ Maintainable codebase
- ✅ Extensible design

