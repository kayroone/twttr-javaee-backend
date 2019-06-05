package de.openknowledge.jwe.domain.model.user;

import org.mockito.Mockito;

/**
 * Test {@link User} class.
 */

public class TestUser {

    private static final String DEFAULT_USER_NAME = "jw";
    private static final String DEFAULT_USER_PASSWORD = "password";

    public static User newDefaultUser() {

        return Mockito.spy(User.newBuilder()
                .withId(1L)
                .withUsername(DEFAULT_USER_NAME)
                .withPassword(DEFAULT_USER_PASSWORD)
                .withRole(UserRole.USER)
                .build());
    }

    public static User newUserWithId(final Long userId) {

        return newUserWithId(DEFAULT_USER_NAME, userId);
    }

    public static User newUserWithId(String username, final Long userId) {

        return Mockito.spy(User.newBuilder()
                .withId(userId)
                .withUsername(username)
                .withPassword(DEFAULT_USER_PASSWORD)
                .withRole(UserRole.USER)
                .build());
    }

    public static String getDefaultUserPassword() {

        return DEFAULT_USER_PASSWORD;
    }
}