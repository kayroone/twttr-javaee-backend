package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.model.user.TestUser;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
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
}