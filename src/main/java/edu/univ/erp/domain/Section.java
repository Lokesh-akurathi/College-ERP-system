// package edu.univ.erp.domain;

// public class Section {
//     private int sectionId;
//     private int courseId;
//     private int instructorId;
//     private String dayTime;
//     private String room;
//     private int capacity;
//     private String semester;
//     private int year;
//     private int enrolled;
    
//     private String courseCode;
//     private String courseTitle;
//     private int courseCredits;
//     private String instructorName;

//     public Section() {}

//     public Section(int sectionId, int courseId, int instructorId, String dayTime, 
//                    String room, int capacity, String semester, int year) {
//         this.sectionId = sectionId;
//         this.courseId = courseId;
//         this.instructorId = instructorId;
//         this.dayTime = dayTime;
//         this.room = room;
//         this.capacity = capacity;
//         this.semester = semester;
//         this.year = year;
//     }

//     public int getSectionId() {
//         return sectionId;
//     }

//     public void setSectionId(int sectionId) {
//         this.sectionId = sectionId;
//     }

//     public int getCourseId() {
//         return courseId;
//     }

//     public void setCourseId(int courseId) {
//         this.courseId = courseId;
//     }

//     public int getInstructorId() {
//         return instructorId;
//     }

//     public void setInstructorId(int instructorId) {
//         this.instructorId = instructorId;
//     }

//     public String getDayTime() {
//         return dayTime;
//     }

//     public void setDayTime(String dayTime) {
//         this.dayTime = dayTime;
//     }

//     public String getRoom() {
//         return room;
//     }

//     public void setRoom(String room) {
//         this.room = room;
//     }

//     public int getCapacity() {
//         return capacity;
//     }

//     public void setCapacity(int capacity) {
//         this.capacity = capacity;
//     }

//     public String getSemester() {
//         return semester;
//     }

//     public void setSemester(String semester) {
//         this.semester = semester;
//     }

//     public int getYear() {
//         return year;
//     }

//     public void setYear(int year) {
//         this.year = year;
//     }

//     public int getEnrolled() {
//         return enrolled;
//     }

//     public void setEnrolled(int enrolled) {
//         this.enrolled = enrolled;
//     }

//     public String getCourseCode() {
//         return courseCode;
//     }

//     public void setCourseCode(String courseCode) {
//         this.courseCode = courseCode;
//     }

//     public String getCourseTitle() {
//         return courseTitle;
//     }

//     public void setCourseTitle(String courseTitle) {
//         this.courseTitle = courseTitle;
//     }

//     public int getCourseCredits() {
//         return courseCredits;
//     }

//     public void setCourseCredits(int courseCredits) {
//         this.courseCredits = courseCredits;
//     }

//     public String getInstructorName() {
//         return instructorName;
//     }

//     public void setInstructorName(String instructorName) {
//         this.instructorName = instructorName;
//     }

//     public boolean isFull() {
//         return enrolled >= capacity;
//     }

//     public int getAvailableSeats() {
//         return capacity - enrolled;
//     }

//     @Override
//     public String toString() {
//         return "Section{" +
//                 "sectionId=" + sectionId +
//                 ", courseCode='" + courseCode + '\'' +
//                 ", dayTime='" + dayTime + '\'' +
//                 ", room='" + room + '\'' +
//                 ", enrolled=" + enrolled + "/" + capacity +
//                 '}';
//     }
// }
package edu.univ.erp.domain;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private int sectionId;
    private int courseId;
    private int instructorId;

    // --- NEW structured times list ---
    private List<SectionTime> times = new ArrayList<>();

    // Keep old string for UI/legacy display (optional)
    private String dayTime;

    private String room;
    private int capacity;
    private String semester;
    private int year;
    private int enrolled;

    private String courseCode;
    private String courseTitle;
    private int courseCredits;
    private String instructorName;

    public Section() {}

    public Section(int sectionId, int courseId, int instructorId,
                   List<SectionTime> times, String room, int capacity,
                   String semester, int year) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.times = times;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    // -----------------------------
    // Getters / Setters
    // -----------------------------

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    // --- NEW structured times ---
    public List<SectionTime> getTimes() {
        return times;
    }

    public void setTimes(List<SectionTime> times) {
        this.times = times;
        updateDayTimeString();  // Refresh legacy string
    }

    // Optional helper to auto-rebuild old string
    private void updateDayTimeString() {
        if (times == null || times.isEmpty()) {
            this.dayTime = "";
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (SectionTime t : times) {
            sb.append(t.toString()).append(", ");
        }
        this.dayTime = sb.substring(0, sb.length() - 2); // remove last comma
    }

    // Legacy support (for UI)
    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(int enrolled) {
        this.enrolled = enrolled;
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

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    // -----------------------------------
    // Helpers
    // -----------------------------------

    public boolean isFull() {
        return enrolled >= capacity;
    }

    public int getAvailableSeats() {
        return capacity - enrolled;
    }

    @Override
    public String toString() {
        return "Section{" +
                "sectionId=" + sectionId +
                ", courseCode='" + courseCode + '\'' +
                ", times=" + times +
                ", room='" + room + '\'' +
                ", enrolled=" + enrolled + "/" + capacity +
                '}';
    }
}
