package de.openknowledge.jwe.domain.model.user;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

/**
 * Test {@link User} class.
 */

public class TestUser {

    private static final String DEFAULT_USER_NAME = "jw";
    private static final String DEFAULT_USER_PASSWORD = "password";

    public static User newDefaultUser() {

        Tweet likedTweet = Mockito.mock(Tweet.class);

        Mockito.doReturn(1L).when(likedTweet).getId();

        Set<Tweet> likes = new HashSet<>();
        likes.add(likedTweet);

        return Mockito.spy(User.newBuilder()
                .withId(1L)
                .withUsername(DEFAULT_USER_NAME)
                .withPassword(DEFAULT_USER_PASSWORD)
                .withRole(UserRole.USER)
                .withLikes(likes)
                .build());
    }

    public static String getDefaultUserPassword() {

        return DEFAULT_USER_PASSWORD;
    }
}