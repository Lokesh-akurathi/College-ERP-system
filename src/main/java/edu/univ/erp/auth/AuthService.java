package edu.univ.erp.auth;

import edu.univ.erp.auth.hash.PasswordHasher;
import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.domain.User;
import java.time.Duration;
import java.time.LocalDateTime;

public class AuthService {
    private final AuthStore authStore;
    private final SessionManager sessionManager;

    public AuthService() {
        this.authStore = new AuthStore();
        this.sessionManager = SessionManager.getInstance();
    }

    public LoginStatus login(String username, String password) {
        User user = authStore.findByUsername(username);
        
        if (user == null) {
            
            return LoginStatus.INCORRECT_CREDENTIALS;
        }

 
        if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
            if (user.getLockoutUntil() != null && LocalDateTime.now().isBefore(user.getLockoutUntil())) {
               
                return LoginStatus.ACCOUNT_LOCKED_TIME;
            } else {
                
                authStore.resetLockout(user.getUserId());
                user.setStatus("ACTIVE");
                user.setFailedLoginAttempts(0);
            }
        }
        
        if (!user.isActive()) {
            return LoginStatus.INCORRECT_CREDENTIALS;
        }

        String passwordHash = authStore.getPasswordHash(user.getUserId());
        if (passwordHash == null) {
            return LoginStatus.INCORRECT_CREDENTIALS;
        }

        
        if (PasswordHasher.verifyPassword(password, passwordHash)) {
            
            if (user.getFailedLoginAttempts() > 0) {
                authStore.resetLoginAttempts(user.getUserId());
            }
            sessionManager.setCurrentUser(user);
            authStore.updateLastLogin(user.getUserId());
            return LoginStatus.SUCCESS;
        }

        
        int newAttempts = user.getFailedLoginAttempts() + 1;
        
        if (newAttempts >= 3) {
            LocalDateTime lockoutTime = LocalDateTime.now().plusMinutes(5);
            authStore.lockUser(user.getUserId(), lockoutTime);
           
            return LoginStatus.INCORRECT_CREDENTIALS; 
        } else {
            authStore.incrementLoginAttempts(user.getUserId());
            
            return LoginStatus.INCORRECT_CREDENTIALS; 
        }
    }

    public void logout() {
        sessionManager.logout();
    }

    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        String currentHash = authStore.getPasswordHash(currentUser.getUserId());
        if (currentHash == null || !PasswordHasher.verifyPassword(oldPassword, currentHash)) {
            return false;
        }

        String newHash = PasswordHasher.hashPassword(newPassword);
        return authStore.changePassword(currentUser.getUserId(), newHash);
    }
    
   
    public Duration getRemainingLockoutDuration(String username) {
        User user = authStore.findByUsername(username);
        if (user != null && "LOCKED".equalsIgnoreCase(user.getStatus()) && user.getLockoutUntil() != null) {
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(user.getLockoutUntil())) {
                return Duration.between(now, user.getLockoutUntil());
            }
        }
        return Duration.ZERO;
    }
    public int getFailedLoginAttempts(String username) {
    User user = authStore.findByUsername(username);
    return (user != null) ? user.getFailedLoginAttempts() : 0;
}
}