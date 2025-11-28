# University ERP System - Architecture Documentation

## Overview
This document explains the system architecture, design decisions, and implementation details.

## Dual-Database Architecture

### Conceptual Design (From Specification)
The specification requires:
1. **Auth DB**: Stores user authentication data (users_auth table)
2. **ERP DB**: Stores all academic data (students, courses, enrollments, grades, etc.)

- `getAuthConnection()` - get connection to auth db
- `getErpConnection()` - get connection to erp db

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
│   └── ThemeConstants/         # Fixing the Colour Scheme for UI
├── service/                    # Business Logic Layer
│   ├── UserService             # User management logic
│   ├── EnrollmentService       # Registration/drop logic
│   ├── GradeService            # Grading logic
│   ├── CourseService           # Course/section management
│   └── SettingsService         # Maintenance mode
│   ├── Admin Service           # manage more important functions of Course/section
│   └── Student Service         # get data related to student
│   └── UserService             # users related     
├── data/                       # Data Access Layer (DAO pattern)
│   ├── StudentStore            # Student CRUD-Create,Read,Update,Delete
│   ├── InstructorStore         # Instructor CRUD
│   ├── CourseStore             # Course CRUD
│   ├── SectionStore            # Section CRUD
│   ├── EnrollmentStore         # Enrollment CRUD
│   ├── GradeStore              # Grade CRUD
│   └── SettingsStore           # Settings CRUD
│   └── GradingStore            # Grading criteria CRUD
├── auth/                       # Authentication Layer
│   ├── AuthService             # Login/logout logic
│   ├── hash/PasswordHasher     # BCrypt hashing
│   ├── store/AuthStore         # Auth DB access
│   └── session/SessionManager  # Session state
│   └── LoginStatus             # Enums for login state
├── access/                     # Authorization Layer
│   └── AccessControl           # Permission checks
├── domain/                     # Contain data classes(cource,enrollment grade,etc..) 
│   ├── User
│   ├── Student
│   ├── Instructor
│   ├── Course
│   ├── Section
│   ├── Enrollment
│   ├── Grade
│   └── Settings
│   └── Grading Criteria
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
- Passwords stored ONLY in users_auth table
- Only hashed values stored
- No plain-text anywhere
- Follows UNIX shadow file principle

### Session Management

```java
SessionManager.getInstance().setCurrentUser(user);
User current = SessionManager.getInstance().getCurrentUser();
```

**Session State**:
- Stores current logged-in user
- Available application-wide
- Cleared on logout
- Not persistent(in-memory only)

### Access Control

**Role-Based Permissions**:
```java
if (!AccessControl.canAccessAdminFeatures()) {
    return AccessControl.getAccessDeniedMessage();
}
```

**Maintenance Mode**:
```java
if (!AccessControl.canModify()) {
    return AccessControl.getMaintenanceDenialMessage();
}
```

When ON:
- Admin: Full access 
- Instructor: Read-only
- Student: Read-only

## Business Logic

### Grading System
- Instructor can add custom grading criteria for his section.
- Letter grades condition is kept consistent across all cources.

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

### Data Access Object
- All `*Store` classes
- Encapsulates database operations
- Separates data access from business logic

### MVC (Model-View-Controller)
- Model: `domain/*` classes
- View: `ui/*` classes
- Controller: `service/*` classes


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

## Deployment

### Local Deployment
1. Install Java 11+
2. Install PostgreSQL
3. Create database
4. Run schema scripts
5. Build with Maven
6. Run JAR file

