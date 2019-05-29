package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.model.user.UserRole;
import de.openknowledge.jwe.domain.repository.TweetRepository;
import de.openknowledge.jwe.domain.repository.UserRepository;
import de.openknowledge.jwe.infrastructure.constants.Constants;
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
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * A resource that provides access to the {@link Tweet} entity.
 */

@Path(Constants.TWEETS_API_URI)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class TweetResource {

    private static final Logger LOG = LoggerFactory.getLogger(TweetResource.class);

    @Inject
    private TweetRepository tweetRepository;

    @Inject
    private UserRepository userRepository;

    @Context
    private SecurityContext securityContext;

    @POST
    @Transactional
    @RolesAllowed(UserRole.USER)
    @Operation(description = "Create a new tweet")
    @APIResponses({
            @APIResponse(responseCode = "201", description = "Tweet created",
                    content = @Content(schema = @Schema(implementation = Tweet.class))),
            @APIResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(schema = @Schema(implementation = ApplicationErrorDTO.class)))
    })
    public Response createTweet(
            @RequestBody(description = "New tweet", required = true,
                    content = @Content(schema = @Schema(implementation = NewTweet.class)))
            @Valid final NewTweet newTweet) {

        LOG.info("Going to create a new Tweet with message '{}'", newTweet.getMessage());

        User author = userRepository.getReferenceByUsername(securityContext.getUserPrincipal().getName());

        Tweet tweet = Tweet.newBuilder()
                .withPostTime(newTweet.getPostTime())
                .withMessage(newTweet.getMessage())
                .withAuthor(author)
                .build();

        tweetRepository.create(tweet);

        TweetListDTO createdTweet = new TweetListDTO(tweet);

        LOG.info("Tweet {} created by {}", createdTweet, author);
        return Response.status(Response.Status.CREATED).entity(createdTweet).build();
    }
}