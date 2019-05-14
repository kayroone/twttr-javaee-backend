package de.openknowledge.jwe.domain.service;

import de.openknowledge.jwe.domain.model.relationship.FollowerFollowingRelationship;
import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.TweetRepository;
import de.openknowledge.jwe.infrastructure.domain.entity.EntityNotFoundException;
import org.wildfly.common.annotation.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that provides {@link Tweet} operations.
 */

@ApplicationScoped
public class TweetService {

    @Inject
    TweetRepository tweetRepository;

    /**
     * Get the timeline (max 100 items) for a given {@link User}.
     *
     * @param user
     * @return An ordered list of {@link Tweet} representing the given users timeline.
     */

    public List<Tweet> getTimeLineForUser(@NotNull User user, @NotNull int offset,
                                          @NotNull int limit) {

        List<FollowerFollowingRelationship> followerFollowingRelationships = user.getFollowings();
        List<User> followings = new ArrayList<>();

        // Add all users to be followed:
        for (FollowerFollowingRelationship relationship : followerFollowingRelationships) {
            followings.add(relationship.getFollowing());
        }

        // Also add own user:
        followings.add(user);

        // Extract id's:
        List<Long> followingsIds = followings.stream().map(User::getId).collect(Collectors.toList());

        return tweetRepository.findPartialByIdsOrderByDate(offset, limit, followingsIds);
    }

    /**
     * Get the main timeline (max 100 items) displayed on the home page of the application.
     *
     * @return An ordered list of {@link Tweet} representing the main timeline.
     */

    public List<Tweet> getMainTimeLine() {

        return tweetRepository.findPartialOrderByDate(0, 100);
    }

    /**
     * Retweet an existing {@link Tweet}.
     *
     * @param tweet
     */

    public void retweet(@NotNull Tweet tweet, @NotNull User user, @NotNull String message) {

        Tweet retweet = new Tweet.TweetBuilder()
                .withMessage(message)
                .withAuthor(user)
                .withLikerList(tweet.getLiker())
                .withPostTime(LocalDateTime.now())
                .build();

        tweet.getRetweets().add(retweet);

        tweetRepository.create(retweet);
        tweetRepository.update(tweet);
    }

    /**
     * Find a {@link Tweet} by it's Id.
     *
     * @param id
     * @return
     * @throws EntityNotFoundException
     */

    public Tweet findById(@NotNull Long id) throws EntityNotFoundException {

        return tweetRepository.find(id);
    }

    /**
     * Create a new {@link Tweet}.
     *
     * @param tweet
     * @return The created {@link Tweet}.
     */

    public Tweet create(@NotNull Tweet tweet) {

        return tweetRepository.create(tweet);
    }

    /**
     * Update an existing {@link Tweet}.
     *
     * @param tweet
     * @return The updated {@link Tweet}.
     */

    public Tweet update(@NotNull Tweet tweet) {

        return tweetRepository.update(tweet);
    }

    /**
     * Delete an existing {@link Tweet}.
     *
     * @param tweet
     */

    public void delete(@NotNull Tweet tweet) {

        tweetRepository.delete(tweet);
    }
}
