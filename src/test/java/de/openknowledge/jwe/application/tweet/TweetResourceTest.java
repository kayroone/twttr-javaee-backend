package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.model.tweet.TestTweet;
import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.user.TestUser;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.TweetRepository;
import de.openknowledge.jwe.domain.repository.UserRepository;
import de.openknowledge.jwe.infrastructure.domain.entity.EntityNotFoundException;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.*;
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

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
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

    @Test
    public void deleteTweetShouldReturn204() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = defaultTweet.getAuthor();

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doNothing().when(tweetRepository).delete(any(Tweet.class));
        Mockito.doReturn(defaultTweet).when(tweetRepository).find(anyLong());

        Response response = resource.deleteTweet(1L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        verify(tweetRepository).delete(any(Tweet.class));
        verify(tweetRepository).find(anyLong());
        verifyNoMoreInteractions(tweetRepository);
    }

    @Test
    public void deleteTweetShouldReturn404ForEntityNotFoundException() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = defaultTweet.getAuthor();
        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doThrow(new EntityNotFoundException(-1L)).when(tweetRepository).find(anyLong());

        Response response = resource.deleteTweet(-1L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

        verify(tweetRepository).find(anyLong());
        verifyNoMoreInteractions(tweetRepository);
    }

    @Test
    public void likeTweetShouldReturn200() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = TestUser.newDefaultUser();
        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(defaultUser).when(userRepository).findByUsername(anyString());
        Mockito.doReturn(defaultTweet).when(tweetRepository).find(anyLong());

        Response response = resource.likeTweet(1L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        verify(tweetRepository).find(anyLong());
        verify(userRepository).update(any(User.class));
        verifyNoMoreInteractions(tweetRepository);

        assertThat(defaultUser.getLikes().size()).isEqualTo(1);
    }

    @Test
    public void unlikeTweetShouldReturn200() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();

        User defaultUser = TestUser.newDefaultUser();
        defaultUser.addLike(defaultTweet);

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(defaultUser).when(userRepository).findByUsername(anyString());
        Mockito.doReturn(defaultTweet).when(tweetRepository).find(anyLong());

        Response response = resource.unlikeTweet(1L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        verify(tweetRepository).find(anyLong());
        verify(userRepository).update(any(User.class));
        verifyNoMoreInteractions(tweetRepository);

        assertThat(defaultUser.getLikes().size()).isEqualTo(0);
    }

    @Test
    public void retweetTweetShouldReturn201() throws EntityNotFoundException {

        Tweet rootTweet = TestTweet.newDefaultTweet();
        User defaultUser = rootTweet.getAuthor();
        NewTweet newTweet = new NewTweet();
        LocalDateTime postTime = LocalDateTime.now();

        newTweet.setPostTime(postTime);
        newTweet.setMessage("Retweeted");

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(rootTweet).when(tweetRepository).find(anyLong());

        Response response = resource.retweetTweet(newTweet, rootTweet.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

        ArgumentCaptor<Tweet> captor = ArgumentCaptor.forClass(Tweet.class);
        verify(tweetRepository).create(captor.capture());

        Tweet retweet = captor.getValue();
        assertThat(retweet.getPostTime()).isEqualTo(postTime.toString());
        assertThat(retweet.getMessage()).isEqualTo(newTweet.getMessage());
        assertThat(retweet.getAuthor()).isEqualTo(rootTweet.getAuthor());
        assertThat(retweet.getRootTweet()).isEqualTo(rootTweet);
    }

    @Test
    public void retweetTweetShouldReturn404() throws EntityNotFoundException {

        Tweet rootTweet = TestTweet.newDefaultTweet();
        User defaultUser = rootTweet.getAuthor();
        NewTweet newTweet = new NewTweet();
        LocalDateTime postTime = LocalDateTime.now();

        newTweet.setPostTime(postTime);
        newTweet.setMessage("Retweeted");

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doThrow(EntityNotFoundException.class).when(tweetRepository).find(anyLong());

        Response response = resource.retweetTweet(newTweet, 404L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
    }

    @Test
    public void getTweetShouldReturn200() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = defaultTweet.getAuthor();

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(defaultTweet).when(tweetRepository).find(anyLong());

        Response response = resource.getTweet(1L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        verify(tweetRepository).find(anyLong());
        verifyNoMoreInteractions(tweetRepository);
    }

    @Test
    public void getTweetShouldReturn404() throws EntityNotFoundException {

        Tweet rootTweet = TestTweet.newDefaultTweet();
        User defaultUser = rootTweet.getAuthor();

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doThrow(EntityNotFoundException.class).when(tweetRepository).find(anyLong());

        Response response = resource.getTweet(404L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
    }

    @Test
    public void getTweetLikerShouldReturn200() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = defaultTweet.getAuthor();

        // Liker:
        User tweetLiker = TestUser.newDefaultUserWithId("liker", 2L);
        defaultTweet.addLiker(tweetLiker);

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(defaultTweet).when(tweetRepository).find(anyLong());

        Response response = resource.getTweetLiker(1L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        verify(tweetRepository).find(anyLong());
        verifyNoMoreInteractions(tweetRepository);
    }

    @Test
    public void getTweetLikerShouldReturn404() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User tweetAuthor = defaultTweet.getAuthor();

        // Liker:
        User tweetLiker = TestUser.newDefaultUserWithId("liker", 2L);
        defaultTweet.addLiker(tweetLiker);

        TestPrincipal testPrincipal = new TestPrincipal(tweetAuthor.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(tweetAuthor).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doThrow(EntityNotFoundException.class).when(tweetRepository).find(anyLong());

        Response response = resource.getTweetLiker(404L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
    }

    @Test
    public void getTweetRetweeterShouldReturn200() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = defaultTweet.getAuthor();

        // Retweet:
        Tweet retweet = TestTweet.newDefaultTweetWithId(2L);
        defaultTweet.addRetweet(retweet);

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(defaultTweet).when(tweetRepository).find(anyLong());

        Response response = resource.getTweetRetweeter(1L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        verify(tweetRepository).find(anyLong());
        verifyNoMoreInteractions(tweetRepository);
    }

    @Test
    public void getTweetRetweeterShouldReturn404() throws EntityNotFoundException {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        User defaultUser = defaultTweet.getAuthor();

        // Retweet:
        Tweet retweet = TestTweet.newDefaultTweetWithId(2L);
        defaultTweet.addRetweet(retweet);

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doThrow(EntityNotFoundException.class).when(tweetRepository).find(anyLong());

        Response response = resource.getTweetRetweeter(404L);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
    }

    @Test
    public void getMainTimelineShouldReturn200() {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        List<Tweet> timelineTweets = new ArrayList<>();

        timelineTweets.add(defaultTweet);

        User defaultUser = defaultTweet.getAuthor();

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(timelineTweets).when(tweetRepository).findPartialOrderByDate(anyInt(), anyInt());

        Response response = resource.getMainTimeLine();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        verify(tweetRepository).findPartialOrderByDate(anyInt(), anyInt());
        verifyNoMoreInteractions(tweetRepository);
    }

    @Test
    public void getMainTimelineShouldReturn204() {

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        List<Tweet> timelineTweets = new ArrayList<>();

        User defaultUser = defaultTweet.getAuthor();

        TestPrincipal testPrincipal = new TestPrincipal(defaultUser.getUsername());
        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(timelineTweets).when(tweetRepository).findPartialOrderByDate(anyInt(), anyInt());

        Response response = resource.getMainTimeLine();
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        verify(tweetRepository).findPartialOrderByDate(anyInt(), anyInt());
        verifyNoMoreInteractions(tweetRepository);
    }
}