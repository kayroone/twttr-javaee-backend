package de.openknowledge.jwe.domain.model.user;

import org.mockito.Mockito;

/**
 * Test {@link User} class.
 */

public class TestUser {

    private static final String DEFAULT_USER_NAME = "jw";
    private static final String DEFAULT_USER_PASSWORD = "password";

    public static User newDefaultUser() {

        User user = Mockito.spy(User.newBuilder()
                .withId(1L)
                .withUsername(DEFAULT_USER_NAME)
                .withPassword(DEFAULT_USER_PASSWORD)
                .withRole(UserRole.USER)
                .build());

        Mockito.doReturn(1L).when(user).getId();

        return user;
    }

    public static String getDefaultUserPassword() {

        return DEFAULT_USER_PASSWORD;
    }
}