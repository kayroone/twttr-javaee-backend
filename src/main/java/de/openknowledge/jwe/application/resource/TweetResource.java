package de.openknowledge.jwe.application.resource;

import de.openknowledge.jwe.application.security.UserPrincipal;
import de.openknowledge.jwe.domain.model.role.UserRole;
import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.service.TweetService;
import de.openknowledge.jwe.infrastructure.domain.error.ApplicationErrorDTO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDateTime;

/**
 * A resource that provides access to the {@link Tweet} entity.
 */


@Path("tweet")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class TweetResource {

    private static final Logger LOG = LoggerFactory.getLogger(TweetResource.class);

    @Inject
    TweetService tweetService;

    @Context
    SecurityContext securityContext;

    @PUT
    @Transactional
    @RolesAllowed(UserRole.Names.USER)
    @Operation(description = "Create a new tweet")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Tweet created",
                    content = @Content(schema = @Schema(implementation = Tweet.class))),
            @APIResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ApplicationErrorDTO.class)))
    })
    public Response createTweet(
            @RequestBody(description = "New tweet", required = true,
                    content = @Content(schema = @Schema(implementation = Tweet.class)))
            @QueryParam("message") String message) {

        User loggedInUser = (UserPrincipal) securityContext.getUserPrincipal();

        Tweet tweet = Tweet.newBuilder()
                .withMessage(message)
                .withAuthor(loggedInUser)
                .withPostTime(LocalDateTime.now())
                .build();

        tweetService.create(tweet);

        LOG.info("Tweet created {}", tweet);

        return Response.status(Response.Status.CREATED).entity(tweet).build();
    }
}