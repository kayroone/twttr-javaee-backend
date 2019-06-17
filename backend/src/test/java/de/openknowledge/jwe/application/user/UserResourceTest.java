package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.model.tweet.TestTweet;
import de.openknowledge.jwe.domain.model.user.TestUser;
import de.openknowledge.jwe.domain.tweet.Tweet;
import de.openknowledge.jwe.domain.tweet.TweetRepository;
import de.openknowledge.jwe.domain.user.User;
import de.openknowledge.jwe.domain.user.UserFollowerFollowingRelationship;
import de.openknowledge.jwe.domain.user.UserRepository;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SuppressWarnings("ALL")
public class UserResourceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private UserRepository userRepository = new UserRepository();

    @Mock
    private TweetRepository tweetRepository = new TweetRepository();

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserResource resource;

    @Test
    public void searchUserShouldReturn200() {

        String keyword = "tes";
        User user1 = TestUser.newDefaultUser();
        List<User> users = new ArrayList<>();

        users.add(user1);

        Mockito.doReturn(users).when(userRepository).search(anyString(), anyInt(), anyInt());

        Response response = resource.searchUser(keyword);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).search(captor.capture(), anyInt(), anyInt());
        verifyNoMoreInteractions(userRepository);

        assertThat(keyword).isEqualTo(captor.getValue());
    }

    @Test
    public void searchUserShouldReturn204() {

        String keyword = "foo";

        Response response = resource.searchUser(keyword);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).search(captor.capture(), anyInt(), anyInt());
        verifyNoMoreInteractions(userRepository);

        assertThat(keyword).isEqualTo(captor.getValue());
    }

    @Test
    public void followUserShouldReturn204() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId("foo", 2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user1);
        relationship.setFollowing(user2);

        user1.addFollowing(relationship);

        TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(user2).when(userRepository).find(anyLong());

        Response response = resource.followUser(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());

        assertThat(user2.getFollower().size()).isEqualTo(1);
        assertThat(user2.getFollower().contains(user1)).isTrue();
        assertThat(user1.getFollowings().size()).isEqualTo(1);
        assertThat(user1.getFollowings().contains(user2)).isTrue();
    }

    @Test
    public void followUserShouldReturn202() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId("foo", 2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user1);
        relationship.setFollowing(user2);

        user1.addFollowing(relationship);
        user2.addFollower(relationship);

        TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(user2).when(userRepository).find(anyLong());

        Response response = resource.followUser(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.ACCEPTED.getStatusCode());

        assertThat(user2.getFollower().size()).isEqualTo(1);
        assertThat(user2.getFollower().contains(user1)).isTrue();
        assertThat(user1.getFollowings().size()).isEqualTo(1);
        assertThat(user1.getFollowings().contains(user2)).isTrue();
    }

    @Test
    public void followUserShouldReturn404() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId(2L);

        TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());

        Mockito.doThrow(EntityNotFoundException.class).when(userRepository).find(anyLong());

        Response response = resource.followUser(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
        assertThat(user2.getFollower().size()).isEqualTo(0);
    }

    @Test
    public void unfollowUserShouldReturn204() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId(2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user1);
        relationship.setFollowing(user2);

        user1.addFollowing(relationship);
        user2.addFollower(relationship);

        TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(user2).when(userRepository).find(anyLong());

        Response response = resource.unfollowUser(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());

        assertThat(user2.getFollower().size()).isEqualTo(0);
    }

    @Test
    public void unfollowUserShouldReturn202() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId(2L);

        TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(user2).when(userRepository).find(anyLong());

        Response response = resource.unfollowUser(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.ACCEPTED.getStatusCode());

        assertThat(user2.getFollower().size()).isEqualTo(0);
    }

    @Test
    public void unfollowUserShouldReturn404() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId(2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user1);
        relationship.setFollowing(user2);

        user1.addFollowing(relationship);
        user2.addFollower(relationship);

        TestPrincipal testPrincipal = new TestPrincipal(user1.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user1).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doThrow(EntityNotFoundException.class).when(userRepository).find(anyLong());

        Response response = resource.unfollowUser(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
        assertThat(user2.getFollower().size()).isEqualTo(1);
    }

    @Test
    public void getTimelineForUserShouldReturn200() throws EntityNotFoundException {

        Tweet tweet1 = TestTweet.newDefaultTweet();
        User user1 = tweet1.getAuthor();

        List<Tweet> timelineTweets = new ArrayList<>();
        timelineTweets.add(tweet1);

        Mockito.doReturn(user1).when(userRepository).find(anyLong());
        Mockito.doReturn(timelineTweets).when(tweetRepository).findPartialByIdsOrderByDate(anyInt(), anyInt(), any(List.class));

        Response response = resource.getTimeLineForUser(user1.getId(), 0, 100);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        verify(tweetRepository).findPartialByIdsOrderByDate(anyInt(), anyInt(), any(List.class));
        verifyNoMoreInteractions(tweetRepository);
    }

    @Test
    public void getTimelineForUserShouldReturn204() throws EntityNotFoundException {

        Tweet tweet1 = TestTweet.newDefaultTweet();
        User user1 = tweet1.getAuthor();

        List<Tweet> timelineTweets = new ArrayList<>();

        Mockito.doReturn(user1).when(userRepository).find(anyLong());
        Mockito.doReturn(timelineTweets).when(tweetRepository).findPartialByIdsOrderByDate(anyInt(), anyInt(), any(List.class));

        Response response = resource.getTimeLineForUser(user1.getId(), 0, 100);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        verify(tweetRepository).findPartialByIdsOrderByDate(anyInt(), anyInt(), any(List.class));
        verifyNoMoreInteractions(tweetRepository);
    }

    @Test
    public void getTimelineForUserShouldReturn404() throws EntityNotFoundException {

        Tweet tweet1 = TestTweet.newDefaultTweet();
        User user1 = tweet1.getAuthor();

        Mockito.doThrow(EntityNotFoundException.class).when(userRepository).find(anyLong());

        Response response = resource.getTimeLineForUser(user1.getId(), 0, 100);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
    }

    @Test
    public void getFollowerForUserShouldReturn200() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId("foo", 2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user2);
        relationship.setFollowing(user1);

        user1.addFollower(relationship);

        Mockito.doReturn(user1).when(userRepository).find(anyLong());

        Response response = resource.getFollower(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        verify(userRepository).find(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void getFollowerForUserShouldReturn204() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId("foo", 2L);

        Mockito.doReturn(user1).when(userRepository).find(anyLong());

        Response response = resource.getFollower(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        verify(userRepository).find(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void getFollowingsForUserShouldReturn200() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId("foo", 2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user2);
        relationship.setFollowing(user1);

        user2.addFollowing(relationship);

        Mockito.doReturn(user2).when(userRepository).find(anyLong());

        Response response = resource.getFollowing(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        verify(userRepository).find(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    public void getFollowingsForUserShouldReturn204() throws EntityNotFoundException {

        User user1 = TestUser.newDefaultUser();
        User user2 = TestUser.newDefaultUserWithId("foo", 2L);

        Mockito.doReturn(user1).when(userRepository).find(anyLong());

        Response response = resource.getFollowing(user2.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        verify(userRepository).find(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
}
