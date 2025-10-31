package edu.univ.erp;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private static final int LOG_ROUNDS = 12;

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        // Hardcoded input
        String originalPassword = "stu123";
        String hashed = hashPassword(originalPassword);
        System.out.println("Hashed password: " + hashed);
        String testhash ="$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYVkYEqUQKu";
        String test2 = "$2a$12$v03/8A0lewTceJbV3ow7cOsygxujKapftME70yyjnMt7/AftV.OJW";

        // Hardcoded verification
        String attemptPassword = "stu123";
        boolean match = verifyPassword(attemptPassword, hashed);
        System.out.println("Password match: " + match);
    }
}