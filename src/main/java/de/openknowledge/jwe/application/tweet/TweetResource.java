package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.model.user.UserRole;
import de.openknowledge.jwe.domain.repository.TweetRepository;
import de.openknowledge.jwe.domain.repository.UserRepository;
import de.openknowledge.jwe.infrastructure.constants.Constants;
import de.openknowledge.jwe.infrastructure.domain.entity.EntityNotFoundException;
import de.openknowledge.jwe.infrastructure.domain.error.ApplicationErrorDTO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.*;
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

        LOG.info("Going to create tweet with message '{}'", newTweet.getMessage());

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

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed(UserRole.USER)
    @Operation(description = "Delete a tweet")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Tweet deleted"),
            @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")})
    public Response deleteTweet(@Parameter(description = "tweet identifier")
                                @PathParam("id") @Min(1) @Max(10000) final Long tweetId) {

        LOG.info("Going to delete tweet with id {}", tweetId);

        try {
            Tweet foundTweet = tweetRepository.find(tweetId);

            tweetRepository.delete(foundTweet);

            LOG.info("Tweet deleted");

            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (EntityNotFoundException e) {
            LOG.warn("Tweet with id {} not found", tweetId);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Transactional
    @RolesAllowed(UserRole.USER)
    @Operation(description = "Like an unliked tweet")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Tweet liked/unliked"),
            @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")})
    public Response likeTweet(@Parameter(description = "tweet identifier")
                              @QueryParam("id") @Min(1) @Max(10000) final Long tweetId) {

        User user = userRepository
                .getReferenceByUsername(securityContext.getUserPrincipal().getName());

        try {
            Tweet foundTweet = tweetRepository.find(tweetId);

            LOG.info("Going to unlike tweet with id {} by user {}", tweetId, user);
            user.addLike(foundTweet);

            userRepository.update(user);

            LOG.info("Tweet {} successfully liked/unliked by {}", foundTweet, user);
            return Response.status(Response.Status.OK).build();
        } catch (EntityNotFoundException e) {
            LOG.warn("Tweet with id {} not found", tweetId);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Transactional
    @RolesAllowed(UserRole.USER)
    @Operation(description = "Unlike an liked tweet")
    @APIResponses({
            @APIResponse(responseCode = "200", description = "Tweet unliked"),
            @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")})
    public Response unlikeTweet(@Parameter(description = "tweet identifier")
                                @QueryParam("id") @Min(1) @Max(10000) final Long tweetId) {

        User user = userRepository
                .getReferenceByUsername(securityContext.getUserPrincipal().getName());

        try {
            Tweet foundTweet = tweetRepository.find(tweetId);

            LOG.info("Going to like tweet with id {} by user {}", tweetId, user);
            user.removeLike(foundTweet);

            userRepository.update(user);

            return Response.status(Response.Status.OK).build();
        } catch (EntityNotFoundException e) {
            LOG.warn("Tweet with id {} not found", tweetId);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}