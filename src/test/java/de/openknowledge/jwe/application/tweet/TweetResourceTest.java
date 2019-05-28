package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.model.tweet.TestTweet;
import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.TweetRepository;
import de.openknowledge.jwe.domain.repository.UserRepository;
import de.openknowledge.jwe.infrastructure.security.TestPrincipal;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Test class for the resource {@link TweetResource}.
 */

public class TweetResourceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private TweetRepository tweetRepository = new TweetRepository();

    @Mock
    private UserRepository userRepository = new UserRepository();

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private TweetResource resource;

    @Test
    public void createTweetShouldReturn201() {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = defaultTweet.getAuthor();
        NewTweet newTweet = new NewTweet();

        newTweet.setPostTime(defaultTweet.getPostTime());
        newTweet.setMessage(defaultTweet.getMessage());

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(any(String.class));
        Mockito.doReturn(defaultTweet).when(tweetRepository).create(any(Tweet.class));

        Response response = resource.createTweet(newTweet);
        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

        ArgumentCaptor<Tweet> captor = ArgumentCaptor.forClass(Tweet.class);
        verify(tweetRepository).create(captor.capture());
        verifyNoMoreInteractions(tweetRepository);

        Tweet createdTweet = captor.getValue();
        assertThat(createdTweet.getPostTime()).isEqualTo(defaultTweet.getPostTime());
        assertThat(createdTweet.getMessage()).isEqualTo(defaultTweet.getMessage());
        assertThat(createdTweet.getAuthor()).isEqualTo(defaultTweet.getAuthor());
    }
}