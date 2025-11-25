package edu.univ.erp.domain;

import java.time.LocalDateTime;

public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private String status;
    private LocalDateTime enrolledAt;
    
    private String courseCode;
    private String courseTitle;
    private int courseCredits;
    private String sectionDayTime;
    private String sectionRoom;
    private String instructorName;
    private String rollNumber;
    private int instructorId;

    public Enrollment() {}

    public Enrollment(int enrollmentId, int studentId, int sectionId, String status, String rollNumber) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.status = status;
        this.rollNumber = rollNumber;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(int courseCredits) {
        this.courseCredits = courseCredits;
    }

    public String getSectionDayTime() {
        return sectionDayTime;
    }

    public void setSectionDayTime(String sectionDayTime) {
        this.sectionDayTime = sectionDayTime;
    }

    public String getSectionRoom() {
        return sectionRoom;
    }

    public void setSectionRoom(String sectionRoom) {
        this.sectionRoom = sectionRoom;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public void setInstructorId(int instructorId) {
    this.instructorId = instructorId;
}
public String getRollNumber() {
    return rollNumber;
}

public void setRollNumber(String rollNumber) {
    this.rollNumber = rollNumber;
}

    @Override
public String toString() {
    return "Enrollment{" +
            "rollNo='" + rollNumber + '\'' +
            ", courseCode='" + courseCode + '\'' +
            ", sectionId=" + sectionId +
            '}';
}

}
