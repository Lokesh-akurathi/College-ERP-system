package edu.univ.erp.domain;

public class Instructor {
    private int userId;
    private String department;
    private String username;
    private String salutation;
    private String firstName;
    private String lastName;

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
    public String getFullName() {
    return (salutation != null ? salutation + " " : "") + firstName + " " + lastName;
}
    public String getSalutation() { return salutation; }
    public void setSalutation(String salutation) { this.salutation = salutation; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    @Override
    public String toString() {
        return getFullName();
    }
}
