package de.openknowledge.jwe.domain.model.user;

import de.openknowledge.jwe.domain.model.relationship.FollowerFollowingRelationship;
import de.openknowledge.jwe.domain.model.role.UserRole;
import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.infrastructure.domain.builder.DefaultBuilder;
import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * An entity that represents a User.
 */

@Entity
@Table(name = "USER")
public class User extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", nullable = false)
    private Long id;

    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "USER_NAME", nullable = false, length = 80)
    private String username;

    @NotNull
    @Size(max = 500)
    @Column(name = "USER_PASSWORD", length = 500)
    private String password;

    @NotNull
    @Column(name = "USER_ROLE")
    private UserRole role;

    @OneToMany(mappedBy = "following")
    @Column(name = "USER_FOLLOWING")
    private List<FollowerFollowingRelationship> followings;

    @OneToMany(mappedBy = "follower")
    @Column(name = "USER_FOLLOWER")
    private List<FollowerFollowingRelationship> followers;

    @OneToMany
    @Column(name = "USER_TWEETS")
    private List<Tweet> tweets;

    @Size(max = 500)
    @Column(name = "USER_ICON_PATH", length = 500)
    private String iconPath;

    // METHODS --------------------------------------------------------------------------------------------------------

    /**
     * Update this {@link User}.
     *
     * @param username
     * @param password
     * @param role
     * @param iconPath
     */

    public void updateUser(String username, String password, UserRole role, String iconPath) {

        this.username = notNull(username, "Username must nut be null");
        this.password = notNull(password, "Password must not be null");
        this.role = notNull(role, "Role must not be null");
        this.iconPath = iconPath;
    }

    /**
     * Follow another {@link User}.
     *
     * @param userToFollow
     */

    public void follow(User userToFollow) {

        FollowerFollowingRelationship followerFollowingRelationship = new FollowerFollowingRelationship();

        followerFollowingRelationship.setFollowing(userToFollow);
        followerFollowingRelationship.setFollower(this);

        getFollowings().add(followerFollowingRelationship);
        userToFollow.getFollowers().add(followerFollowingRelationship);
    }

    /**
     * Unfollow a {@link User}.
     *
     * @param userToUnfollow
     */

    public void unfollow(User userToUnfollow) {

        FollowerFollowingRelationship followerFollowingRelationship = new FollowerFollowingRelationship();

        followerFollowingRelationship.setFollowing(userToUnfollow);
        followerFollowingRelationship.setFollower(this);

        getFollowings().remove(followerFollowingRelationship);
        userToUnfollow.getFollowers().remove(followerFollowingRelationship);
    }

    // CONSTRUCTOR ----------------------------------------------------------------------------------------------------

    public User() {
        super();
    }

    // GETTER AND SETTER ----------------------------------------------------------------------------------------------

    @Override
    public Long getId() {
        return null;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setFollowers(List<FollowerFollowingRelationship> followers) {
        this.followers = followers;
    }

    public List<FollowerFollowingRelationship> getFollowers() {
        return followers;
    }

    public void setFollowings(List<FollowerFollowingRelationship> followings) {
        this.followings = followings;
    }

    public List<FollowerFollowingRelationship> getFollowings() {
        return followings;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    // BUILDER --------------------------------------------------------------------------------------------------------

    /**
     * Builder for the entity {@link User}.
     */

    public static class UserBuilder extends DefaultBuilder<User> {

        public UserBuilder(String username) {
            this.instance.username = notNull(username, "Username must not be null");
        }

        public UserBuilder withPassword(final String password) {
            this.instance.password = notNull(password, "Password must not be null");
            return this;
        }

        public UserBuilder withRole(final UserRole role) {
            this.instance.role = role;
            return this;
        }

        public UserBuilder withFollowings(final List<FollowerFollowingRelationship> followings) {
            this.instance.followings = notNull(followings, "dueDate must not be null");
            return this;
        }

        public UserBuilder withFollowers(final List<FollowerFollowingRelationship> followers) {
            this.instance.followers = notNull(followers, "dueDate must not be null");
            return this;
        }

        public UserBuilder withTweetList(final List<Tweet> tweets) {
            this.instance.tweets = notNull(tweets, "title must not be null");
            return this;
        }

        public UserBuilder withIconPath(final String iconPath) {
            this.instance.iconPath = notNull(iconPath, "title must not be null");
            return this;
        }
    }
}
