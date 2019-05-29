package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.UserRepository;
import de.openknowledge.jwe.infrastructure.constants.Constants;
import de.openknowledge.jwe.infrastructure.domain.error.ApplicationErrorDTO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path(Constants.USERS_API_URI)
@Produces({MediaType.APPLICATION_JSON})
public class UserResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

    @GET
    @Operation(description = "Search for user by a given keyword")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @APIResponse(responseCode = "204", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApplicationErrorDTO.class)))
    })
    public Response searchUser(@Parameter(description = "Keyword matching the username")
                               @QueryParam("keyword") final String keyword) {

        List<UserListDTO> usersFound = userRepository
                .search(keyword, -1, -1)
                .stream()
                .map(UserListDTO::new)
                .collect(Collectors.toList());

        if (!usersFound.isEmpty()) {
            LOG.info("User found {}", usersFound);
            return Response.status(Response.Status.OK)
                    .entity(new GenericEntity<List<UserListDTO>>(usersFound) {
                    }).build();
        } else {
            LOG.info("No User found with keyword {}", keyword);
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }
}
