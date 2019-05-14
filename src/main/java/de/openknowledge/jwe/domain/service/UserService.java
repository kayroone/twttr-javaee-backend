package de.openknowledge.jwe.domain.service;

import de.openknowledge.jwe.domain.model.relationship.FollowerFollowingRelationship;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.UserRepository;
import de.openknowledge.jwe.infrastructure.domain.entity.EntityNotFoundException;
import org.wildfly.common.annotation.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * Service that provides {@link User} operations.
 */

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    /**
     * Follow a {@link User}.
     *
     * @param user         The user that wants to follow.
     * @param userToFollow The user that will be followed.
     */

    public void follow(@NotNull User user, @NotNull User userToFollow) {

        FollowerFollowingRelationship followerFollowingRelationship = new FollowerFollowingRelationship();

        followerFollowingRelationship.setFollowing(userToFollow);
        followerFollowingRelationship.setFollower(user);

        user.getFollowings().add(followerFollowingRelationship);
        userToFollow.getFollowers().add(followerFollowingRelationship);

        userRepository.update(user);
        userRepository.update(userToFollow);
    }

    /**
     * Unfollow a {@link User}.
     *
     * @param user           The user that wants to unfollow.
     * @param userToUnfollow The user that will be unfollowed.
     */

    public void unfollow(@NotNull User user, @NotNull User userToUnfollow) {

        FollowerFollowingRelationship followerFollowingRelationship = new FollowerFollowingRelationship();

        followerFollowingRelationship.setFollowing(userToUnfollow);
        followerFollowingRelationship.setFollower(user);

        user.getFollowings().remove(followerFollowingRelationship);
        userToUnfollow.getFollowers().remove(followerFollowingRelationship);

        userRepository.update(user);
        userRepository.update(userToUnfollow);
    }

    /**
     * Find a {@link User} by it's username.
     *
     * @return The {@link User}.
     */

    public User findbyUsername(@NotNull String username) {

        return userRepository.findByUsername(username);
    }

    /**
     * Find {@link User} by a keyword.
     *
     * @return A list of {@link User} containing the keyword in their usernames.
     */

    public List<User> search(@NotNull String keyword) {

        return userRepository.findByKeyword(keyword);
    }

    /**
     * Find all existing {@link User}.
     *
     * @return A list with all existing {@link User}.
     */

    public List<User> findAll() {

        return userRepository.findAll();
    }

    /**
     * Find a single {@link User} by it's Id.
     *
     * @return The single {@link User}.
     */

    public User findById(@NotNull Long id) throws EntityNotFoundException {

        return userRepository.find(id);
    }

    /**
     * Create a new {@link User}.
     *
     * @param user
     * @return The created {@link User}.
     */

    public User create(@NotNull User user) {

        return userRepository.create(user);
    }

    /**
     * Update an existing {@link User}.
     *
     * @param user
     * @return The updated {@link User}.
     */

    public User update(@NotNull User user) {

        return userRepository.update(user);
    }

    /**
     * Delete an existing {@link User}.
     *
     * @param user
     */

    public void delete(@NotNull User user) {

        userRepository.delete(user);
    }
}
