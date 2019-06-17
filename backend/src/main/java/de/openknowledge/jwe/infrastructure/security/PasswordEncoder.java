package de.openknowledge.jwe.infrastructure.security;

import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.ApplicationScoped;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * bCrypt password encoder.
 */
@ApplicationScoped
public class PasswordEncoder {

    /**
     * Hashes a password using BCrypt.
     *
     * @param plainTextPassword The password as string.
     * @return The password as hash.
     */
    public String hashPassword(final String plainTextPassword) {

        notNull(plainTextPassword, "plainTextPassword must not be null");

        String salt = BCrypt.gensalt();

        return BCrypt.hashpw(plainTextPassword, salt);
    }

    /**
     * Checks a password against a stored hash using BCrypt.
     *
     * @param plainTextPassword The password as string.
     * @param hashedPassword    The password as hash.
     * @return true if the hash matches the password or false if not.
     */
    public boolean checkPassword(final String plainTextPassword, final String hashedPassword) {

        notNull(plainTextPassword, "plainTextPassword must not be null");
        notNull(hashedPassword, "hashedPassword must not be null");

        if (!hashedPassword.startsWith("$2a$")) {
            throw new RuntimeException("Hashed password is invalid");
        }

        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
