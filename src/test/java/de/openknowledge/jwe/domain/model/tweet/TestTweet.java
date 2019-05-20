package de.openknowledge.jwe.domain.model.tweet;

import de.openknowledge.jwe.domain.model.user.TestUser;
import org.mockito.Mockito;

import java.time.LocalDateTime;

/**
 * Test {@link Tweet} class.
 */

public class TestTweet {

    public static Tweet newDefaultTweet() {

        return Mockito.spy(Tweet.newBuilder()
                .withAuthor(TestUser.newDefaultUser())
                .withMessage("Today is a good day!")
                .withPostTime(LocalDateTime.now())
                .build());
    }
}
