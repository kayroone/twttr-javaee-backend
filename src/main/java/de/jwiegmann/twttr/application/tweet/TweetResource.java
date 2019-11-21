package de.jwiegmann.twttr.application.tweet;

import de.jwiegmann.twttr.application.user.UserListDTO;
import de.jwiegmann.twttr.domain.tweet.Tweet;
import de.jwiegmann.twttr.domain.tweet.TweetRepository;
import de.jwiegmann.twttr.domain.user.User;
import de.jwiegmann.twttr.domain.user.UserRepository;
import de.jwiegmann.twttr.domain.user.UserRole;
import de.jwiegmann.twttr.infrastructure.constants.Constants;
import de.jwiegmann.twttr.infrastructure.domain.entity.EntityNotFoundException;
import de.jwiegmann.twttr.infrastructure.domain.error.ApplicationErrorDTO;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
          @APIResponse(
                  responseCode = "201",
                  description = "Tweet created",
                  content = @Content(schema = @Schema(implementation = Tweet.class))),
          @APIResponse(
                  responseCode = "400",
                  description = "Invalid request data",
                  content = @Content(schema = @Schema(implementation = ApplicationErrorDTO.class)))
  })
  public Response createTweet(
          @RequestBody(
                  description = "New tweet",
                  required = true,
                  content = @Content(schema = @Schema(implementation = TweetNewDTO.class)))
          @Valid final TweetNewDTO tweetNewDTO) {

    LOG.info("Going to create tweet with message '{}'", tweetNewDTO.getMessage());

    User author =
            userRepository.getReferenceByUsername(securityContext.getUserPrincipal().getName());

    Tweet tweet =
            Tweet.newBuilder()
                    .withPostTime(tweetNewDTO.getPostTime())
                    .withMessage(tweetNewDTO.getMessage())
                    .withAuthor(author)
                    .build();

    Tweet createdTweet = tweetRepository.create(tweet);

    TweetListDTO tweetListDTO = new TweetListDTO(createdTweet);

    LOG.info("Tweet {} created by {}", tweetListDTO, author);
    return Response.status(Response.Status.CREATED).entity(tweetListDTO).build();
  }

  @DELETE
  @Path("/{id}")
  @Transactional
  @RolesAllowed(UserRole.USER)
  @Operation(description = "Delete a tweet")
  @APIResponses({
          @APIResponse(responseCode = "204", description = "Tweet deleted"),
          @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")
  })
  public Response deleteTweet(
          @Parameter(description = "tweet identifier") @PathParam("id") @Min(1) @Max(10000) final Long tweetId) {

    LOG.info("Going to delete tweet with id {}", tweetId);

    try {
      Tweet foundTweet = tweetRepository.find(tweetId);

      tweetRepository.delete(foundTweet);

      LOG.info("Tweet with id {} deleted", tweetId);
      return Response.status(Response.Status.NO_CONTENT).build();

    } catch (EntityNotFoundException e) {
      LOG.warn("Tweet with id {} not found", tweetId);
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @PUT
  @Path("/{id}/like")
  @Transactional
  @RolesAllowed(UserRole.USER)
  @Operation(description = "Like an unliked tweet")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Tweet liked/unliked"),
          @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")
  })
  public Response likeTweet(
          @Parameter(description = "tweet identifier") @PathParam("id") @Min(1) @Max(10000) final Long tweetId) {

    User user = userRepository.findByUsername(securityContext.getUserPrincipal().getName());

    try {
      Tweet foundTweet = tweetRepository.find(tweetId);

      LOG.info("Going to unlike tweet with id {} by user {}", tweetId, user);
      user.addLike(foundTweet);

      userRepository.update(user);

      LOG.info("Tweet {} successfully liked by {}", foundTweet, user);
      return Response.status(Response.Status.NO_CONTENT).build();

    } catch (EntityNotFoundException e) {
      LOG.warn("Tweet with id {} not found", tweetId);
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @DELETE
  @Path("/{id}/like")
  @Transactional
  @RolesAllowed(UserRole.USER)
  @Operation(description = "Unlike an liked tweet")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Tweet unliked"),
          @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")
  })
  public Response unlikeTweet(
          @Parameter(description = "tweet identifier") @PathParam("id") @Min(1) @Max(10000) final Long tweetId) {

    User user = userRepository.findByUsername(securityContext.getUserPrincipal().getName());

    try {
      Tweet foundTweet = tweetRepository.find(tweetId);

      LOG.info("Going to like tweet with id {} by user {}", tweetId, user);
      user.removeLike(foundTweet);

      userRepository.update(user);

      LOG.info("Tweet {} successfully unliked by {}", foundTweet, user);
      return Response.status(Response.Status.NO_CONTENT).build();

    } catch (EntityNotFoundException e) {
      LOG.warn("Tweet with id {} not found", tweetId);
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @POST
  @Path("/{id}")
  @Transactional
  @RolesAllowed(UserRole.USER)
  @Operation(description = "Retweet an tweet")
  @APIResponses({
          @APIResponse(responseCode = "201", description = "Tweet retweeted"),
          @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")
  })
  public Response retweetTweet(
          @RequestBody(
                  description = "New retweet",
                  required = true,
                  content = @Content(schema = @Schema(implementation = TweetNewDTO.class)))
          @Valid final TweetNewDTO tweetNewDTO,
          @Parameter(description = "tweet identifier") @PathParam("id") @Min(1) @Max(10000) final Long tweetId) {

    User user = userRepository.getReferenceByUsername(securityContext.getUserPrincipal().getName());

    try {
      Tweet rootTweet = tweetRepository.find(tweetId);

      Tweet tweet =
              new Tweet.TweetBuilder()
                      .withMessage(tweetNewDTO.getMessage())
                      .withAuthor(user)
                      .withPostTime(tweetNewDTO.getPostTime())
                      .withRootTweet(rootTweet)
                      .build();

      LOG.info("Going to create retweet with id {} by user {}", tweet.getId(), user);
      Tweet createdRetweet = tweetRepository.create(tweet);

      TweetListDTO tweetListDTO = new TweetListDTO(createdRetweet);

      LOG.info("Retweet {} successfully created by {}", tweetListDTO, user);
      return Response.status(Response.Status.CREATED).entity(tweetListDTO).build();

    } catch (EntityNotFoundException e) {
      LOG.warn("Tweet with id {} not found", tweetId);
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Path("/{id}")
  @PermitAll
  @Operation(description = "Get a tweet")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Successful fetched tweet"),
          @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")
  })
  public Response getTweet(
          @Parameter(description = "tweet identifier") @PathParam("id") @Min(1) @Max(10000) final Long tweetId) {

    try {
      Tweet foundTweet = tweetRepository.find(tweetId);

      List<UserListDTO> liker =
              foundTweet.getLiker().stream().map(UserListDTO::new).collect(Collectors.toList());

      List<UserListDTO> retweeter =
              foundTweet.getRetweets().stream()
                      .map(t -> new UserListDTO(t.getAuthor()))
                      .collect(Collectors.toList());

      TweetFullDTO tweetFullDTO = new TweetFullDTO(foundTweet, liker, retweeter);

      LOG.info("Tweet {} successfully fetched", tweetFullDTO);
      return Response.status(Response.Status.OK).entity(tweetFullDTO).build();

    } catch (EntityNotFoundException e) {
      LOG.warn("Tweet with id {} not found", tweetId);
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Path("/{id}/liker")
  @PermitAll
  @Operation(description = "Get a list of all tweets liker")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Successful fetched tweets liker list"),
          @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")
  })
  public Response getTweetLiker(
          @Parameter(description = "tweet identifier") @PathParam("id") @Min(1) @Max(10000) final Long tweetId) {

    try {
      Tweet foundTweet = tweetRepository.find(tweetId);

      Set<UserListDTO> liker =
              foundTweet.getLiker().stream().map(UserListDTO::new).collect(Collectors.toSet());

      LOG.info("Tweet liker list {} successfully fetched", liker);
      return Response.status(Response.Status.OK).entity(liker).build();

    } catch (EntityNotFoundException e) {
      LOG.warn("Tweet with id {} not found", tweetId);
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @Path("/{id}/retweeter")
  @PermitAll
  @Operation(description = "Get a list of all tweet retweeter")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Successful fetched tweets retweeter list"),
          @APIResponse(responseCode = "404", description = "Tweet with given id does not exist")
  })
  public Response getTweetRetweeter(
          @Parameter(description = "tweet identifier") @PathParam("id") @Min(1) @Max(10000) final Long tweetId) {

    try {
      Tweet foundTweet = tweetRepository.find(tweetId);

      Set<UserListDTO> retweeter =
              foundTweet.getRetweets().stream()
                      .map(t -> new UserListDTO(t.getAuthor()))
                      .collect(Collectors.toSet());

      LOG.info("Tweet retweeter list {} successfully fetched", retweeter);
      return Response.status(Response.Status.OK).entity(retweeter).build();

    } catch (EntityNotFoundException e) {
      LOG.warn("Tweet with id {} not found", tweetId);
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

  @GET
  @PermitAll
  @Operation(description = "Get the main timeline consisting of the latest tweets")
  @APIResponses({
          @APIResponse(responseCode = "200", description = "Successful fetched latest tweets list"),
          @APIResponse(responseCode = "204", description = "No tweets available yet")
  })
  public Response getMainTimeLine() {

    List<Tweet> tweets = tweetRepository.findPartialOrderByDate(0, 100);

    List<TweetListDTO> timelineDTOs =
            tweets.stream().map(TweetListDTO::new).collect(Collectors.toList());

    LOG.info("Main timeline tweets list {} successfully fetched", timelineDTOs);

    if (tweets.size() > 0) {
      return Response.status(Response.Status.OK).entity(timelineDTOs).build();
    } else {
      return Response.status(Response.Status.NO_CONTENT).build();
    }
  }
}
