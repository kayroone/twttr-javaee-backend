package de.openknowledge.jwe.infrastructure.security;

import org.mindrot.jbcrypt.BCrypt;

import javax.enterprise.context.ApplicationScoped;

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
    public String hashPassword(String plainTextPassword) {

        String salt = BCrypt.gensalt();

        return BCrypt.hashpw(plainTextPassword, salt);
    }

    /**
     * Checks a password against a stored hash using BCrypt.
     *
     * @param plainTextPassword The password as string.
     * @param hashedPassword The password as hash.
     * @return true if the hash matches the password or false if not.
     */
    public boolean checkPassword(String plainTextPassword, String hashedPassword) {

        if (null == hashedPassword || !hashedPassword.startsWith("$2a$")) {
            throw new RuntimeException("Hashed password is invalid");
        }

        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}