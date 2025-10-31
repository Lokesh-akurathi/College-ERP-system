# University ERP System - User Guide

## Table of Contents
1. [Introduction](#introduction)
2. [System Overview](#system-overview)
3. [Default User Accounts](#default-user-accounts)
4. [Student Features](#student-features)
5. [Instructor Features](#instructor-features)
6. [Admin Features](#admin-features)
7. [Maintenance Mode](#maintenance-mode)

## Introduction

The University ERP System is a comprehensive desktop application designed to manage university academic operations including course management, student enrollments, and grade tracking.

## System Overview

### Key Characteristics
- **Platform**: Java Swing Desktop Application
- **Database**: PostgreSQL with dual-architecture (Auth + ERP)
- **Security**: BCrypt password hashing, role-based access control
- **Roles**: Admin, Instructor, Student

### Architecture
```
┌─────────────────┐
│   Swing UI      │  (Login, Dashboards, Forms)
└────────┬────────┘
         │
┌────────┴────────┐
│  Service Layer  │  (Business Logic, Access Control)
└────────┬────────┘
         │
┌────────┴────────┐
│   Data Layer    │  (JDBC, Database Access)
└────────┬────────┘
         │
┌────────┴────────────────┐
│  PostgreSQL Databases   │
│  ├─ Auth DB             │  (users_auth)
│  └─ ERP DB              │  (students, courses, etc.)
└─────────────────────────┘
```

## Default User Accounts

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| Admin | admin1 | admin123 | Full system access |
| Instructor | inst1 | inst123 | Teaches 5 sections |
| Student | stu1 | stu123 | Enrolled in 2 courses |
| Student | stu2 | stu123 | Enrolled in 1 course |

## Student Features

### 1. Browse Course Catalog
- View all available course sections
- See course code, title, credits
- Check seat availability
- View instructor, schedule, and room details

**How to Use:**
1. Login as student
2. Click "Browse Courses" from sidebar
3. View all sections in table format
4. Select a section to register

### 2. Register for Sections
- Enroll in available course sections
- System validates:
  - Seat availability
  - No duplicate enrollments

**How to Use:**
1. Go to "Browse Courses"
2. Select desired section from table
3. Click "Register for Selected Section"
4. Confirmation message displays on success

**Validation Rules:**
- ❌ Cannot register for same section twice
- ❌ Cannot register for full sections
- ✅ Can register if seats available

### 3. Drop Sections
- Remove enrollment from a section

**How to Use:**
1. Click "My Registrations"
2. Select enrollment to drop
3. Click "Drop Selected Section"
4. Confirm the action

### 4. View Timetable
- See all enrolled courses with schedule details

**Display Includes:**
- Course code and title
- Day/Time
- Room location
- Instructor name

### 5. View Grades
- See assessment scores and final grades

**Grade Components:**
- Quiz (20% weight)
- Midterm (30% weight)
- End-Sem (50% weight)
- Final Score (calculated)
- Letter Grade (A, B, C, D, F)

**Grade Scale:**
- A: 90-100
- B: 80-89
- C: 70-79
- D: 60-69
- F: 0-59

### 6. Download Transcript
- Export academic record as CSV file

**How to Use:**
1. Click "Download Transcript"
2. Click "Download Transcript (CSV)"
3. Choose save location
4. File includes all course grades

## Instructor Features

### 1. View My Sections
- See all assigned teaching sections
- View enrollment counts
- Check section capacity

**Information Displayed:**
- Section ID
- Course code and title
- Schedule (day/time)
- Room assignment
- Enrolled/Capacity ratio
- Semester and year

### 2. Enter Grades
- Record student assessment scores

**How to Use:**
1. Click "Grade Entry"
2. Select section from dropdown
3. Enter scores in table cells:
   - Quiz column
   - Midterm column
   - End-Sem column
4. Click "Save Scores"

**Validation:**
- Scores must be 0-100
- Can only grade own sections
- Blocked in Maintenance Mode

### 3. Compute Final Grades
- Calculate weighted final scores

**How to Use:**
1. Ensure all components (Quiz, Midterm, End-Sem) have scores
2. Click "Compute Final Grades"
3. System calculates:
   - Final Score = (Quiz × 0.20) + (Midterm × 0.30) + (End-Sem × 0.50)
   - Letter Grade based on final score

**Requirements:**
- All three components must have scores
- Cannot compute partial grades

### 4. View Class Statistics
- Basic statistics displayed in grade entry view
- See enrolled count vs. capacity

## Admin Features

### 1. Manage Users
Create new users for all roles.

**Create Student:**
- Username
- Password (minimum 6 characters)
- Roll Number
- Program
- Year (1-5)

**Create Instructor:**
- Username
- Password
- Department

**Create Admin:**
- Username
- Password

**Validation:**
- Username: at least 3 characters, alphanumeric + underscore only
- Password: minimum 6 characters
- Unique usernames required

### 2. Manage Courses
- Create new courses
- View course catalog

**Course Fields:**
- Code (unique)
- Title
- Credits (1-10)

### 3. Manage Sections
- Create course sections
- Assign instructors
- Set capacity and schedule

**Section Fields:**
- Course (select from dropdown)
- Instructor (select from dropdown)
- Day/Time (e.g., "Mon/Wed 9:00-10:30")
- Room (e.g., "Room 101")
- Capacity (1-500 students)
- Semester (Spring/Fall/Summer)
- Year

### 4. Maintenance Mode
- Control system-wide access restrictions

**When Maintenance Mode is ON:**
- ✅ All users can login
- ✅ All users can view data
- ❌ Students cannot register/drop
- ❌ Instructors cannot enter grades
- ✅ Admins retain full access
- 🟠 Orange banner displays at top

**How to Toggle:**
1. Click "Maintenance Mode"
2. View current status
3. Click "Enable Maintenance Mode" or "Disable Maintenance Mode"
4. Banner updates throughout system

**Use Cases:**
- System maintenance
- Data cleanup
- Semester transitions
- Preventing changes during audits

## Maintenance Mode

### Visual Indicator
When maintenance mode is enabled, all users see an orange banner:
```
⚠ MAINTENANCE MODE - Read-Only Access
```

### Access Matrix

| Role | Login | View Data | Modify Data |
|------|-------|-----------|-------------|
| **Admin** | ✅ | ✅ | ✅ |
| **Instructor** | ✅ | ✅ | ❌ |
| **Student** | ✅ | ✅ | ❌ |

### Blocked Operations in Maintenance Mode

**For Students:**
- Register for sections
- Drop sections

**For Instructors:**
- Enter/modify scores
- Compute final grades

**Allowed Operations:**
- All viewing operations
- Login/logout
- Navigation
- Report generation

### Error Messages
When attempting blocked operations:
> "System is in Maintenance Mode. Only viewing is allowed."

## Best Practices

### For Students
1. Register for courses early (limited seats)
2. Download transcript regularly
3. Monitor grades throughout semester
4. Drop courses before deadline

### For Instructors
1. Enter grades promptly after assessments
2. Compute final grades at semester end
3. Verify all scores before computing finals
4. Use CSV export for record-keeping

### For Admins
1. Create users before semester starts
2. Set up courses and sections in advance
3. Assign instructors early
4. Use Maintenance Mode for:
   - Grade finalization periods
   - System updates
   - Data migrations
5. Disable Maintenance Mode promptly after maintenance

## Security Features

### Password Security
- BCrypt hashing (12 rounds)
- No plain-text storage
- Passwords stored only in Auth DB
- UNIX shadow file approach

### Access Control
- Role-based permissions
- Session management
- User cannot access other users' data
- Instructors limited to own sections

### Data Separation
- Authentication data isolated in Auth DB
- Academic data in ERP DB
- Linked via user_id foreign key

## Troubleshooting

### Cannot Login
- Verify username/password
- Check CAPS LOCK
- Try default credentials
- Contact administrator

### Cannot Register for Section
- Check if section is full
- Verify not already registered
- Ensure Maintenance Mode is OFF
- Check enrollment prerequisites

### Cannot Enter Grades
- Verify assigned to section
- Check Maintenance Mode status
- Ensure scores are 0-100
- Confirm student is enrolled

### Cannot Compute Final Grade
- Verify all components have scores
- Check Quiz, Midterm, and End-Sem are entered
- Ensure you are the section instructor

## Support

For technical issues or questions:
1. Check this user guide
2. Review SETUP_INSTRUCTIONS.md
3. Contact system administrator
4. Check application logs

## Appendix

### Keyboard Shortcuts
- Tab: Navigate fields
- Enter: Submit forms (in password fields)
- Escape: Close dialogs (standard)

### File Exports
- **Transcript**: CSV format
- **Grades**: Viewable in-app, exportable as CSV (future)

### System Requirements
- Java 11 or higher
- PostgreSQL 12+
- 4GB RAM minimum
- 1024x768 display minimum
