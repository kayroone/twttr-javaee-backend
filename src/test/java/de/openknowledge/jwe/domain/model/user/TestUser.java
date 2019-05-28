package de.openknowledge.jwe.domain.model.user;

import org.mockito.Mockito;

/**
 * Test {@link User} class.
 */

public class TestUser {

    public static User newDefaultUser() {

        User user = Mockito.spy(User.newBuilder()
                .withId(1L)
                .withUsername("jw")
                .withPassword("password")
                .withRole(UserRole.USER)
                .build());

        Mockito.doReturn(1L).when(user).getId();

        return user;
    }
}