package de.openknowledge.jwe.application.resource;

import de.openknowledge.jwe.application.tweet.NewTweet;
import de.openknowledge.jwe.application.tweet.TweetResource;
import de.openknowledge.jwe.domain.model.tweet.TestTweet;
import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.TweetRepository;
import de.openknowledge.jwe.infrastructure.security.AuthenticatedUserAdapter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Test class for the resource {@link TweetResource}.
 */

@RunWith(MockitoJUnitRunner.class)
public class TweetResourceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private TweetRepository repository = new TweetRepository();

    @Mock
    private AuthenticatedUserAdapter authenticatedUserAdapter;

    @InjectMocks
    private TweetResource resource;

    @Test
    public void createTweetShouldReturn201() {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = defaultTweet.getAuthor();
        NewTweet newTweet = new NewTweet();

        newTweet.setPostTime(defaultTweet.getPostTime());
        newTweet.setAuthor(defaultTweet.getAuthor());
        newTweet.setMessage(defaultTweet.getMessage());

        Mockito.doReturn(defaultUser).when(authenticatedUserAdapter).getUser();
        Mockito.doReturn(defaultTweet).when(repository).create(any(Tweet.class));

        Response response = resource.createTweet(newTweet);
        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

        ArgumentCaptor<Tweet> captor = ArgumentCaptor.forClass(Tweet.class);
        verify(repository).create(captor.capture());
        verifyNoMoreInteractions(repository);

        Tweet createdTweet = captor.getValue();
        assertThat(createdTweet.getPostTime()).isEqualTo(defaultTweet.getPostTime());
        assertThat(createdTweet.getMessage()).isEqualTo(defaultTweet.getMessage());
        assertThat(createdTweet.getAuthor()).isEqualTo(defaultTweet.getAuthor());
    }
}