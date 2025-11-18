package edu.univ.erp.domain;

public class SectionTime {

    private String dayOfWeek;   // "Mon", "Tue", "Wed", ...
    private String startTime;   // "09:00"
    private String endTime;     // "10:30"

    public SectionTime() {}

    public SectionTime(String dayOfWeek, String startTime, String endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // ---------- GETTERS / SETTERS ----------

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // ---------- Utility: toString() for UI table or dropdown ----------

    @Override
    public String toString() {
        return dayOfWeek + " " + startTime + "-" + endTime;
    }
}
