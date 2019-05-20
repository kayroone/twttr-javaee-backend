package de.openknowledge.jwe.infrastructure.security;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.user.FollowerFollowingRelationship;
import de.openknowledge.jwe.domain.model.user.User;

import java.util.List;

/**
 * User Adapter to propagate the authenticated {@link User} in request scope.
 */

public class AuthenticatedUserAdapter {

    private User entity;

    public AuthenticatedUserAdapter() { }

    AuthenticatedUserAdapter(User user) {
        this.entity = user;
    }

    public User getUser() {

        return entity;
    }

    public String getUsername() {

        return entity.getUsername();
    }

    public String getPassword() {

        return entity.getPassword();
    }

    public String getRole() {

        return entity.getPassword();
    }

    public List<FollowerFollowingRelationship> getFollowings() {

        return entity.getFollowers();
    }

    public List<FollowerFollowingRelationship> getFollowers() {

        return entity.getFollowers();
    }

    public List<Tweet> getTweets() {

        return entity.getTweets();
    }

    public String getIconPath() {

        return entity.getIconPath();
    }

    public boolean isInRole(String role) {

        return entity.getRoles().contains(role);
    }
}
