package edu.univ.erp.domain;

public class Instructor {
    private int userId;
    private String department;
    private String username;

    public Instructor() {}

    public Instructor(int userId, String department) {
        this.userId = userId;
        this.department = department;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "userId=" + userId +
                ", department='" + department + '\'' +
                '}';
    }
}
