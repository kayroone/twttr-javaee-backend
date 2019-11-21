package de.jwiegmann.twttr.application.tweet;

import de.jwiegmann.twttr.domain.tweet.TestTweet;
import de.jwiegmann.twttr.domain.tweet.Tweet;
import de.jwiegmann.twttr.domain.tweet.TweetRepository;
import de.jwiegmann.twttr.domain.user.TestUser;
import de.jwiegmann.twttr.domain.user.User;
import de.jwiegmann.twttr.domain.user.UserRepository;
import de.jwiegmann.twttr.infrastructure.domain.entity.EntityNotFoundException;
import de.jwiegmann.twttr.infrastructure.security.TestPrincipal;
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

/** Test class for the resource {@link TweetResource}. */
@SuppressWarnings("ALL")
public class TweetResourceTest {

  @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

  @Mock private TweetRepository tweetRepository = new TweetRepository();

  @Mock private UserRepository userRepository = new UserRepository();

  @Mock private SecurityContext securityContext;

  @InjectMocks private TweetResource resource;

  @Test
  public void createTweetShouldReturn201() {

    Tweet tweet1 = TestTweet.newDefaultTweet();
    User user1 = tweet1.getAuthor();
    TweetNewDTO tweetNewDTO = new TweetNewDTO();

    tweetNewDTO.setPostTime(tweet1.getPostTime());
    tweetNewDTO.setMessage(tweet1.getMessage());

    TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());
    Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

    Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());
    Mockito.doReturn(tweet1).when(tweetRepository).create(any(Tweet.class));

    Response response = resource.createTweet(tweetNewDTO);
    assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

    ArgumentCaptor<Tweet> captor = ArgumentCaptor.forClass(Tweet.class);
    verify(tweetRepository).create(captor.capture());
    verifyNoMoreInteractions(tweetRepository);

    Tweet createdTweet = captor.getValue();
    assertThat(createdTweet.getPostTime()).isEqualTo(tweet1.getPostTime());
    assertThat(createdTweet.getMessage()).isEqualTo(tweet1.getMessage());
    assertThat(createdTweet.getAuthor()).isEqualTo(tweet1.getAuthor());
  }

  @Test
  public void deleteTweetShouldReturn204() throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();
    User user1 = tweet1.getAuthor();

    TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());
    Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();

    Mockito.doNothing().when(tweetRepository).delete(any(Tweet.class));
    Mockito.doReturn(tweet1).when(tweetRepository).find(anyLong());

    Response response = resource.deleteTweet(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    verify(tweetRepository).delete(any(Tweet.class));
    verify(tweetRepository).find(anyLong());
    verifyNoMoreInteractions(tweetRepository);
  }

  @Test
  public void deleteTweetShouldReturn404ForEntityNotFoundException()
      throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();
    User user1 = tweet1.getAuthor();
    TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

    Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
    Mockito.doThrow(new EntityNotFoundException(-1L)).when(tweetRepository).find(anyLong());

    Response response = resource.deleteTweet(-1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

    verify(tweetRepository).find(anyLong());
    verifyNoMoreInteractions(tweetRepository);
  }

  @Test
  public void likeTweetShouldReturn200() throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();
    User user1 = TestUser.newDefaultUser();
    TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

    Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
    Mockito.doReturn(user1).when(userRepository).findByUsername(anyString());
    Mockito.doReturn(tweet1).when(tweetRepository).find(anyLong());

    Response response = resource.likeTweet(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    verify(tweetRepository).find(anyLong());
    verify(userRepository).update(any(User.class));
    verifyNoMoreInteractions(tweetRepository);

    assertThat(user1.getLikes().size()).isEqualTo(1);
  }

  @Test
  public void unlikeTweetShouldReturn200() throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();

    User user1 = TestUser.newDefaultUser();
    user1.addLike(tweet1);

    TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

    Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
    Mockito.doReturn(user1).when(userRepository).findByUsername(anyString());
    Mockito.doReturn(tweet1).when(tweetRepository).find(anyLong());

    Response response = resource.unlikeTweet(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    verify(tweetRepository).find(anyLong());
    verify(userRepository).update(any(User.class));
    verifyNoMoreInteractions(tweetRepository);

    assertThat(user1.getLikes().size()).isEqualTo(0);
  }

  @Test
  public void retweetTweetShouldReturn201() throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();
    User user1 = tweet1.getAuthor();

    TweetNewDTO tweetNewDTO = new TweetNewDTO();
    LocalDateTime postTime = LocalDateTime.now();

    tweetNewDTO.setPostTime(postTime);
    tweetNewDTO.setMessage("Retweeted");

    TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

    Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
    Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());
    Mockito.doReturn(tweet1).when(tweetRepository).find(anyLong());
    Mockito.doReturn(tweet1).when(tweetRepository).create(any(Tweet.class));

    Response response = resource.retweetTweet(tweetNewDTO, tweet1.getId());
    assertThat(response.getStatus()).isEqualTo(Response.Status.CREATED.getStatusCode());

    ArgumentCaptor<Tweet> captor = ArgumentCaptor.forClass(Tweet.class);
    verify(tweetRepository).create(captor.capture());

    Tweet retweet = captor.getValue();
    assertThat(retweet.getPostTime()).isEqualTo(postTime.toString());
    assertThat(retweet.getMessage()).isEqualTo(tweetNewDTO.getMessage());
    assertThat(retweet.getAuthor()).isEqualTo(tweet1.getAuthor());
    assertThat(retweet.getRootTweet()).isEqualTo(tweet1);
  }

  @Test
  public void retweetTweetShouldReturn404() throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();
    User user1 = tweet1.getAuthor();

    TweetNewDTO tweetNewDTO = new TweetNewDTO();
    LocalDateTime postTime = LocalDateTime.now();

    tweetNewDTO.setPostTime(postTime);
    tweetNewDTO.setMessage("Retweeted");

    TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

    Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
    Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());
    Mockito.doThrow(EntityNotFoundException.class).when(tweetRepository).find(anyLong());

    Response response = resource.retweetTweet(tweetNewDTO, 404L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());

    assertThatNullPointerException()
        .isThrownBy(() -> new EntityNotFoundException(null))
        .withMessage("identifier must not be null")
        .withNoCause();
  }

  @Test
  public void getTweetShouldReturn200() throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();

    Mockito.doReturn(tweet1).when(tweetRepository).find(anyLong());

    Response response = resource.getTweet(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

    verify(tweetRepository).find(anyLong());
    verifyNoMoreInteractions(tweetRepository);
  }

  @Test
  public void getTweetShouldReturn404() throws EntityNotFoundException {

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

    Tweet tweet1 = TestTweet.newDefaultTweet();

    // Liker:
    User user1 = TestUser.newDefaultUserWithId("liker", 2L);
    tweet1.addLiker(user1);

    Mockito.doReturn(tweet1).when(tweetRepository).find(anyLong());

    Response response = resource.getTweetLiker(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

    verify(tweetRepository).find(anyLong());
    verifyNoMoreInteractions(tweetRepository);
  }

  @Test
  public void getTweetLikerShouldReturn404() throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();

    // Liker:
    User user1 = TestUser.newDefaultUserWithId("liker", 2L);
    tweet1.addLiker(user1);

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

    Tweet tweet1 = TestTweet.newDefaultTweet();

    // Retweet:
    Tweet tweet2 = TestTweet.newDefaultTweetWithId(2L);
    tweet1.addRetweet(tweet2);

    Mockito.doReturn(tweet1).when(tweetRepository).find(anyLong());

    Response response = resource.getTweetRetweeter(1L);
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

    verify(tweetRepository).find(anyLong());
    verifyNoMoreInteractions(tweetRepository);
  }

  @Test
  public void getTweetRetweeterShouldReturn404() throws EntityNotFoundException {

    Tweet tweet1 = TestTweet.newDefaultTweet();

    // Retweet:
    Tweet tweet2 = TestTweet.newDefaultTweetWithId(2L);
    tweet1.addRetweet(tweet2);

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

    Tweet tweet1 = TestTweet.newDefaultTweet();
    List<Tweet> timelineTweets = new ArrayList<>();

    timelineTweets.add(tweet1);

    Mockito.doReturn(timelineTweets)
        .when(tweetRepository)
        .findPartialOrderByDate(anyInt(), anyInt());

    Response response = resource.getMainTimeLine();
    assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

    verify(tweetRepository).findPartialOrderByDate(anyInt(), anyInt());
    verifyNoMoreInteractions(tweetRepository);
  }

  @Test
  public void getMainTimelineShouldReturn204() {

    List<Tweet> timelineTweets = new ArrayList<>();

    Mockito.doReturn(timelineTweets)
        .when(tweetRepository)
        .findPartialOrderByDate(anyInt(), anyInt());

    Response response = resource.getMainTimeLine();
    assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

    verify(tweetRepository).findPartialOrderByDate(anyInt(), anyInt());
    verifyNoMoreInteractions(tweetRepository);
  }
}
