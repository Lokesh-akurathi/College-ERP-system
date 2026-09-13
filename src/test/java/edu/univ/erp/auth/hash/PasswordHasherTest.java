package edu.univ.erp.auth.hash;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherTest {

    @Test
    void hashPassword_generatesValidBcryptHash() {
        String plain = "mySecretPassword123";
        String hash = PasswordHasher.hashPassword(plain);

        assertThat(hash).isNotNull();
        assertThat(hash).startsWith("$2a$");
    }

    @Test
    void verifyPassword_correctPassword_returnsTrue() {
        String plain = "mySecretPassword123";
        String hash = PasswordHasher.hashPassword(plain);

        assertThat(PasswordHasher.verifyPassword(plain, hash)).isTrue();
    }

    @Test
    void verifyPassword_incorrectPassword_returnsFalse() {
        String plain = "mySecretPassword123";
        String wrong = "wrongPassword";
        String hash = PasswordHasher.hashPassword(plain);

        assertThat(PasswordHasher.verifyPassword(wrong, hash)).isFalse();
        assertThat(PasswordHasher.verifyPassword(plain, "invalid_hash_string")).isFalse();
    }
}
