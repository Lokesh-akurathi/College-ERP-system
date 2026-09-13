package edu.univ.erp.auth;

import edu.univ.erp.auth.session.SessionManager;
import edu.univ.erp.auth.store.AuthStore;
import edu.univ.erp.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock private AuthStore authStore;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(authStore, SessionManager.getInstance());
    }

    @Test
    void login_unknownUser_returnsIncorrectCredentials() {
        when(authStore.findByUsername("nonexistent")).thenReturn(null);

        LoginStatus status = authService.login("nonexistent", "password123");
        assertThat(status).isEqualTo(LoginStatus.INCORRECT_CREDENTIALS);
    }

    @Test
    void getFailedLoginAttempts_userNotFound_returnsZero() {
        when(authStore.findByUsername("unknown")).thenReturn(null);

        int attempts = authService.getFailedLoginAttempts("unknown");
        assertThat(attempts).isEqualTo(0);
    }

    @Test
    void getRemainingLockoutDuration_unlockedUser_returnsZero() {
        User user = new User(1, "activeUser", "STUDENT", "ACTIVE");
        when(authStore.findByUsername("activeUser")).thenReturn(user);

        Duration duration = authService.getRemainingLockoutDuration("activeUser");
        assertThat(duration).isEqualTo(Duration.ZERO);
    }
}
