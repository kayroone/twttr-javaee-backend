package de.jwiegmann.twttr.domain.user;

import org.mockito.Mockito;

/**
 * Test {@link User} class.
 */

public class TestUser {

    private static final String DEFAULT_USER_NAME = "jw";
    private static final String DEFAULT_USER_PASSWORD = "jw";

    public static User newDefaultUser() {

        return Mockito.spy(User.newBuilder()
                .withId(1L)
                .withUsername(DEFAULT_USER_NAME)
                .withPassword(DEFAULT_USER_PASSWORD)
                .withRole(UserRole.USER)
                .build());
    }

    public static User newDefaultUserWithId(final Long userId) {

        return newDefaultUserWithId(DEFAULT_USER_NAME, userId);
    }

    public static User newDefaultUserWithId(String username, final Long userId) {

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

    public static String getDefaultUsername() {

        return DEFAULT_USER_NAME;
    }
}
