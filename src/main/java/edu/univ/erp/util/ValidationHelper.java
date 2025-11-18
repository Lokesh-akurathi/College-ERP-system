package edu.univ.erp.util;

public class ValidationHelper {

    public static boolean isValidUsername(String username) {
        return username != null && username.trim().length() >= 3 && username.matches("[a-zA-Z0-9_]+");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isValidCredits(int credits) {
        return credits > 0 && credits <= 10;
    }

    public static boolean isValidCapacity(int capacity) {
        return capacity > 0 && capacity <= 500;
    }

    public static boolean isValidYear(int year) {
        return year >= 1 && year <= 5;
    }

    public static boolean isValidScore(double score) {
        return score >= 0 && score <= 100;
    }

    public static String validateCourseData(String code, String title, int credits) {
        if (code == null || code.trim().isEmpty()) {
            return "Course code is required.";
        }
        if (title == null || title.trim().isEmpty()) {
            return "Course title is required.";
        }
        if (!isValidCredits(credits)) {
            return "Credits must be between 1 and 10.";
        }
        return null;
    }

    public static String validateSectionData(String dayTime, String room, int capacity) {
        //if (dayTime == null || dayTime.trim().isEmpty()) {
            //return "Day/Time is required.";
        //}
        if (room == null || room.trim().isEmpty()) {
            return "Room is required.";
        }
        if (!isValidCapacity(capacity)) {
            return "Capacity must be between 1 and 500.";
        }
        return null;
    }

    public static String validateUserData(String username, String password) {
        if (!isValidUsername(username)) {
            return "Username must be at least 3 characters and contain only letters, numbers, and underscores.";
        }
        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters.";
        }
        return null;
    }
}
