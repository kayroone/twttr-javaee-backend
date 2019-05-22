package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.TweetRepository;
import de.openknowledge.jwe.domain.repository.UserRepository;
import de.openknowledge.jwe.infrastructure.constants.Constants;
import de.openknowledge.jwe.infrastructure.domain.error.ApplicationErrorDTO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

@Path(Constants.USERS_API_URI)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class UserResource {

    private static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private TweetRepository tweetRepository;

    @Inject
    private UserRepository userRepository;

    @Context
    private SecurityContext securityContext;

    @GET
    @Transactional
    @PermitAll
    @Operation(description = "Search a user")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @APIResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ApplicationErrorDTO.class)))
    })
    public Response searchUser(@QueryParam("keyword") final String keyword) {

        List<User> usersFound = userRepository.search(keyword, -1, -1);

        if (usersFound.size() > 0) {
            LOG.info("Users found {}", usersFound);
            return Response.status(Response.Status.OK).entity(usersFound).build();
        } else {
            LOG.info("No Users found with keyword {}", keyword);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
