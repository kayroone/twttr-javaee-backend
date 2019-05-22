package de.openknowledge.jwe.domain.model.user;

import org.mockito.Mockito;

import java.util.Collections;

/**
 * Test {@link User} class.
 */

public class TestUser {

    public static User newDefaultUser() {

        User user = Mockito.spy(User.newBuilder()
                .withId(1L)
                .withUsername("test")
                .withPassword("foobar")
                .withRole(Collections.singleton(UserRole.USER))
                .build());

        Mockito.doReturn(1L).when(user).getId();

        return user;
    }
}