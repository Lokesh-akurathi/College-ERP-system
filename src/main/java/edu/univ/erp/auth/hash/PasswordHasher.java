package edu.univ.erp.auth.hash;

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
}
