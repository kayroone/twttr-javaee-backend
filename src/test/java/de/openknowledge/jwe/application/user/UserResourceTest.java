package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.model.user.TestUser;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.model.user.UserFollowerFollowingRelationship;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class UserResourceTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private UserRepository userRepository = new UserRepository();

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserResource resource;

    @Test
    public void searchUserShouldReturn200() {

        String keyword = "tes";
        User defaultUser = TestUser.newDefaultUser();
        List<User> users = new ArrayList<>();

        users.add(defaultUser);

        Mockito.doReturn(defaultUser).when(userRepository).getReferenceByUsername(any(String.class));
        Mockito.doReturn(users).when(userRepository).search(any(String.class), any(Integer.class), any(Integer.class));

        Response response = resource.searchUser(keyword);
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).search(captor.capture(), any(Integer.class), any(Integer.class));
        verifyNoMoreInteractions(userRepository);

        assertThat(keyword).isEqualTo(captor.getValue());
    }

    @Test
    public void searchUserShouldReturn204() {

        String keyword = "foo";

        Response response = resource.searchUser(keyword);
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).search(captor.capture(), any(Integer.class), any(Integer.class));
        verifyNoMoreInteractions(userRepository);

        assertThat(keyword).isEqualTo(captor.getValue());
    }

    @Test
    public void followUserShouldReturn204() throws EntityNotFoundException {

        User user = TestUser.newDefaultUser();
        User userToFollow = TestUser.newDefaultUserWithId("foo", 2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user);
        relationship.setFollowing(userToFollow);

        user.addFollowing(relationship);

        TestPrincipal testPrincipal = new TestPrincipal(user.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(userToFollow).when(userRepository).find(anyLong());

        Response response = resource.followUser(userToFollow.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());

        assertThat(userToFollow.getFollower().size()).isEqualTo(1);
        assertThat(userToFollow.getFollower().contains(user)).isTrue();
        assertThat(user.getFollowings().size()).isEqualTo(1);
        assertThat(user.getFollowings().contains(userToFollow)).isTrue();
    }

    @Test
    public void followUserShouldReturn202() throws EntityNotFoundException {

        User user = TestUser.newDefaultUser();
        User userToFollow = TestUser.newDefaultUserWithId("foo", 2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user);
        relationship.setFollowing(userToFollow);

        user.addFollowing(relationship);
        userToFollow.addFollower(relationship);

        TestPrincipal testPrincipal = new TestPrincipal(user.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(userToFollow).when(userRepository).find(anyLong());

        Response response = resource.followUser(userToFollow.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.ACCEPTED.getStatusCode());

        assertThat(userToFollow.getFollower().size()).isEqualTo(1);
        assertThat(userToFollow.getFollower().contains(user)).isTrue();
        assertThat(user.getFollowings().size()).isEqualTo(1);
        assertThat(user.getFollowings().contains(userToFollow)).isTrue();
    }

    @Test
    public void followUserShouldReturn404() throws EntityNotFoundException {

        User user = TestUser.newDefaultUser();
        User userToFollow = TestUser.newDefaultUserWithId(2L);

        TestPrincipal testPrincipal = new TestPrincipal(user.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user).when(userRepository).getReferenceByUsername(anyString());

        Mockito.doThrow(EntityNotFoundException.class).when(userRepository).find(anyLong());

        Response response = resource.followUser(userToFollow.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
        assertThat(userToFollow.getFollower().size()).isEqualTo(0);
    }

    @Test
    public void unfollowUserShouldReturn204() throws EntityNotFoundException {

        User user = TestUser.newDefaultUser();
        User userToUnfollow = TestUser.newDefaultUserWithId(2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user);
        relationship.setFollowing(userToUnfollow);

        user.addFollowing(relationship);
        userToUnfollow.addFollower(relationship);

        TestPrincipal testPrincipal = new TestPrincipal(user.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(userToUnfollow).when(userRepository).find(anyLong());

        Response response = resource.unfollowUser(userToUnfollow.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NO_CONTENT.getStatusCode());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());

        assertThat(userToUnfollow.getFollower().size()).isEqualTo(0);
    }

    @Test
    public void unfollowUserShouldReturn202() throws EntityNotFoundException {

        User user = TestUser.newDefaultUser();
        User userToUnfollow = TestUser.newDefaultUserWithId(2L);

        TestPrincipal testPrincipal = new TestPrincipal(user.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doReturn(userToUnfollow).when(userRepository).find(anyLong());

        Response response = resource.unfollowUser(userToUnfollow.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.ACCEPTED.getStatusCode());

        assertThat(userToUnfollow.getFollower().size()).isEqualTo(0);
    }

    @Test
    public void unfollowUserShouldReturn404() throws EntityNotFoundException {

        User user = TestUser.newDefaultUser();
        User userToUnfollow = TestUser.newDefaultUserWithId(2L);

        UserFollowerFollowingRelationship relationship = new UserFollowerFollowingRelationship();
        relationship.setFollower(user);
        relationship.setFollowing(userToUnfollow);

        user.addFollowing(relationship);
        userToUnfollow.addFollower(relationship);

        TestPrincipal testPrincipal = new TestPrincipal(user.getUsername());

        Mockito.doReturn(testPrincipal).when(securityContext).getUserPrincipal();
        Mockito.doReturn(user).when(userRepository).getReferenceByUsername(anyString());
        Mockito.doThrow(EntityNotFoundException.class).when(userRepository).find(anyLong());

        Response response = resource.unfollowUser(userToUnfollow.getId());
        assertThat(response.getStatus()).isEqualTo(Response.Status.NOT_FOUND.getStatusCode());
        assertThatNullPointerException()
                .isThrownBy(() -> new EntityNotFoundException(null))
                .withMessage("identifier must not be null")
                .withNoCause();
        assertThat(userToUnfollow.getFollower().size()).isEqualTo(1);
    }
}