package edu.univ.erp.auth;

import edu.univ.erp.auth.hash.PasswordHasher;
import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.domain.User;

public class AuthService {
    private final AuthStore authStore;
    private final SessionManager sessionManager;

    public AuthService() {
        this.authStore = new AuthStore();
        this.sessionManager = SessionManager.getInstance();
    }

    public boolean login(String username, String password) {
        User user = authStore.findByUsername(username);
        
        if (user == null || !user.isActive()) {
            return false;
        }

        String passwordHash = authStore.getPasswordHash(user.getUserId());
        if (passwordHash == null) {
            return false;
        }

        if (PasswordHasher.verifyPassword(password, passwordHash)) {
            sessionManager.setCurrentUser(user);
            authStore.updateLastLogin(user.getUserId());
            return true;
        }

        return false;
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
}
