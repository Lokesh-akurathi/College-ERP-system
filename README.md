# University ERP System

A desktop application built with Java Swing and JDBC for managing university courses, enrollments, and grades.

## 🖥️ Important Note
**This is a Desktop GUI Application** that requires a graphical display environment (X11/Windows/macOS). It **cannot run in a web browser** or headless cloud environment like Replit. To use this application, please clone the repository and run it locally on your computer.

## Features

- **Role-based Access Control**: Student, Instructor, and Admin roles with specific permissions
- **Dual Database Architecture**: Separate Auth DB for credentials and ERP DB for academic data
- **Secure Authentication**: BCrypt password hashing following UNIX shadow file principles
- **Maintenance Mode**: System-wide read-only mode for maintenance operations
- **Student Features**: Course registration, timetable view, grade viewing, transcript export
- **Instructor Features**: Grade entry, final grade computation, class statistics
- **Admin Features**: User management, course/section management, maintenance mode control

## Prerequisites

- Java 11 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

## Database Setup

1. Create two PostgreSQL databases:
   - `auth_db` - for user authentication
   - `erp_db` - for academic data

2. Run the SQL scripts to create tables and seed data:
   ```bash
   psql -U postgres -d auth_db -f database/auth_db_schema.sql
   psql -U postgres -d erp_db -f database/erp_db_schema.sql
   ```

## Configuration

Edit `src/main/resources/database.properties` with your database connection details:

```properties
auth.db.url=jdbc:postgresql://localhost:5432/auth_db
auth.db.username=postgres
auth.db.password=yourpassword

erp.db.url=jdbc:postgresql://localhost:5432/erp_db
erp.db.username=postgres
erp.db.password=yourpassword
```

## Building and Running

```bash
# Compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="edu.univ.erp.Main"

# Or build a JAR
mvn clean package
java -jar target/university-erp-1.0-SNAPSHOT.jar
```

## Default Accounts

- **Admin**: username: `admin1`, password: `admin123`
- **Instructor**: username: `inst1`, password: `inst123`
- **Student 1**: username: `stu1`, password: `stu123`
- **Student 2**: username: `stu2`, password: `stu123`

## Project Structure

```
src/main/java/edu/univ/erp/
├── Main.java                    # Application entry point
├── ui/                          # Swing UI components
│   ├── common/                  # Shared UI components
│   ├── auth/                    # Login screens
│   ├── student/                 # Student screens
│   ├── instructor/              # Instructor screens
│   └── admin/                   # Admin screens
├── domain/                      # Domain models
├── service/                     # Business logic layer
├── data/                        # ERP DB access layer
├── auth/                        # Authentication layer
│   ├── store/                   # Auth DB access
│   ├── hash/                    # Password hashing
│   └── session/                 # Session management
├── access/                      # Access control
└── util/                        # Utilities (CSV, PDF, validation)
```

## Testing

See `docs/TestPlan.md` for complete testing instructions.

## License

Academic project for educational purposes.
