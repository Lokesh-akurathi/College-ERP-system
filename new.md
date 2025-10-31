# University ERP System

## Overview

This is a Java Swing desktop application for managing university academic operations including courses, enrollments, and grades. The system features role-based access control (Admin, Instructor, Student), secure authentication with BCrypt password hashing, and a maintenance mode for system-wide operations.

**Critical Constraint**: This is a GUI desktop application requiring a graphical display environment (X11/Windows/macOS). It cannot run in headless or web browser environments.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Application Layer
- **Framework**: Java Swing for desktop GUI components
- **Build Tool**: Maven for dependency management and build automation
- **Java Version**: Java 11 or higher

### Authentication & Authorization
- **Security Model**: Role-based access control with three roles (Admin, Instructor, Student)
- **Password Storage**: BCrypt hashing following UNIX shadow file principles
- **Session Management**: User sessions maintained through the GUI application lifecycle
- **Access Control**: Permission-based restrictions enforced at the service layer

### Database Architecture

**Design Philosophy**: The system is designed with a conceptual dual-database architecture that separates authentication concerns from academic data, but the implementation is flexible to support both single and dual physical database deployments.

**Current Implementation** (Replit Environment):
- Single PostgreSQL database containing all tables
- Logical separation maintained through DatabaseConfig API:
  - `getAuthConnection()` - for authentication operations
  - `getErpConnection()` - for ERP/academic operations
- Both methods currently return connections to the same physical database
- This allows SQL JOINs between authentication and academic tables

**Database Schema**:
- **users_auth**: User credentials (username, password hash, role)
- **students**: Student profile information
- **instructors**: Instructor profile information
- **courses**: Course catalog
- **sections**: Course sections with scheduling
- **enrollments**: Student course registrations
- **grades**: Assessment and grade records
- **settings**: System configuration (maintenance mode flags)

**Rationale**: The single-database approach was chosen for Replit deployment simplicity while maintaining code-level separation. The architecture allows easy migration to separate physical databases by updating connection strings in DatabaseConfig without changing business logic.

### Data Access Layer
- **Technology**: JDBC for direct database connectivity
- **Pattern**: Repository/Store pattern for data access abstraction
- **Connection Management**: Separate conceptual connections for Auth DB and ERP DB operations
- **Transaction Handling**: Service layer coordinates multi-table operations

### Business Logic Layer
- **Service Classes**: Encapsulate business rules and orchestrate data operations
- **Access Control**: Services enforce role-based permissions before data access
- **Maintenance Mode**: System-wide read-only mode controlled through settings table

### Presentation Layer
- **UI Framework**: Java Swing components (JFrame, JPanel, JTable, etc.)
- **Dashboard Pattern**: Role-specific dashboards (StudentDashboard, InstructorDashboard, AdminDashboard)
- **Form Validation**: Input validation at UI level before service layer processing

## External Dependencies

### Database
- **PostgreSQL** 12 or higher - Primary data store for both authentication and academic data
- **JDBC Driver** - PostgreSQL JDBC driver for database connectivity

### Build & Runtime
- **Maven** 3.6+ - Dependency management and build automation
- **Java Runtime** - Java 11+ JRE/JDK required for execution

### Security Libraries
- **BCrypt** - Password hashing library (likely jBCrypt or similar Java implementation)

### Environment Requirements
- **Graphical Display**: X11, Windows GUI, or macOS windowing system required
- **Not Compatible With**: Headless servers, web browsers, or cloud IDEs without display forwarding

### Configuration
- **database.properties** file or environment variables:
  - `DATABASE_URL` / `auth.db.url` / `erp.db.url`
  - `PGUSER` / database username credentials
  - `PGPASSWORD` / database password credentials