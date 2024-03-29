package de.jwiegmann.twttr.domain.tweet;

import de.jwiegmann.twttr.domain.user.TestUser;
import org.mockito.Mockito;

import java.time.LocalDateTime;

/** Test {@link Tweet} class. */
@SuppressWarnings("ALL")
public class TestTweet {

  public static Tweet newDefaultTweet() {

    Tweet tweet =
        Mockito.spy(
            Tweet.newBuilder()
                .withAuthor(TestUser.newDefaultUser())
                .withMessage("Today is a good day!")
                .withPostTime(LocalDateTime.now())
                .build());

    Mockito.doReturn(1L).when(tweet).getId();

    return tweet;
  }

  public static Tweet newDefaultTweetWithId(Long id) {

    Tweet tweet =
        Mockito.spy(
            Tweet.newBuilder()
                .withId(id)
                .withAuthor(TestUser.newDefaultUserWithId(id))
                .withMessage("Today is a good day!")
                .withPostTime(LocalDateTime.now())
                .build());

    Mockito.doReturn(id).when(tweet).getId();

    return tweet;
  }
}
