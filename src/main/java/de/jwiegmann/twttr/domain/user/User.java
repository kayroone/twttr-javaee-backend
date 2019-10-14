package de.jwiegmann.twttr.domain.user;

import de.jwiegmann.twttr.domain.tweet.Tweet;
import de.jwiegmann.twttr.infrastructure.domain.builder.DefaultBuilder;
import de.jwiegmann.twttr.infrastructure.domain.entity.AbstractEntity;
import de.jwiegmann.twttr.infrastructure.security.PasswordEncoder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * An entity that represents a {@link User}.
 */

@Entity
@Table(name = "tab_user")
public class User extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "user_name", nullable = false, length = 80)
    private String username;

    @NotNull
    @Size(max = 500)
    @Column(name = "user_password", length = 500)
    private String password;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "user")
    private Set<UserRoleRelationship> roles;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserFollowerFollowingRelationship> followings;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserFollowerFollowingRelationship> followers;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private Set<Tweet> tweets;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "tab_user_tweet_like",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "tweet_id"))
    private Set<Tweet> likes;

    @Size(max = 500)
    @Column(name = "user_icon_path", length = 500)
    private String iconPath;

    public static UserBuilder newBuilder() {
        return new UserBuilder();
    }

    /**
     * Add a {@link Tweet} to own likes list.
     *
     * @param tweet The {@link Tweet} that will be added to this {@link User} likes list.
     */

    public void addLike(final Tweet tweet) {
        notNull(tweet, "tweet must not be null");
        this.likes.add(tweet);
    }

    /**
     * Remove a {@link Tweet} from own likes list.
     *
     * @param tweet The {@link Tweet} that will be removed from this {@link User} likes list.
     */

    public void removeLike(final Tweet tweet) {
        notNull(tweet, "tweet must not be null");
        this.likes.remove(tweet);
    }

    /**
     * Follow this {@link User}.
     *
     * @param user The {@link User} that wants to follow.
     */

    public void follow(final User user) {

        notNull(user, "user must not be null");

        UserFollowerFollowingRelationship followerFollowingRelationship = new UserFollowerFollowingRelationship();

        followerFollowingRelationship.setFollowing(this);
        followerFollowingRelationship.setFollower(user);

        // Add the relationship to own follower list - JPA will persist the relation to the other user.
        this.followers.add(followerFollowingRelationship);
    }

    /**
     * Unfollow this {@link User}.
     *
     * @param user The {@link User} that wants to unfollow.
     */

    public void unfollow(final User user) {

        notNull(user, "user must not be null");

        Optional<UserFollowerFollowingRelationship> result = this.followers
                .stream()
                .filter(relationship -> relationship.getFollowing().equals(this))
                .filter(relationship -> relationship.getFollower().equals(user))
                .findFirst();

        if (result.isPresent()) {
            UserFollowerFollowingRelationship relationship = result.get();
            this.followers.remove(relationship);
        }
    }

    /**
     * Add a following relationship to this {@link User}.
     *
     * @param relationship The {@link UserFollowerFollowingRelationship} holding follower and following user.
     */

    public void addFollowing(final UserFollowerFollowingRelationship relationship) {
        notNull(relationship, "relationship must not be null");
        this.followings.add(relationship);
    }

    /**
     * Remove a following relationship from this {@link User}.
     *
     * @param relationship The {@link UserFollowerFollowingRelationship} holding follower and following user.
     */

    public void removeFollowing(final UserFollowerFollowingRelationship relationship) {
        notNull(relationship, "relationship must not be null");
        this.followings.remove(relationship);
    }

    /**
     * Add a follower relationship to this {@link User}.
     *
     * @param relationship The {@link UserFollowerFollowingRelationship} holding follower and following user.
     */

    public void addFollower(final UserFollowerFollowingRelationship relationship) {
        notNull(relationship, "relationship must not be null");
        this.followers.add(relationship);
    }

    /**
     * Remove a follower relationship from this {@link User}.
     *
     * @param relationship The {@link UserFollowerFollowingRelationship} holding follower and following user.
     */

    public void removeFollower(final UserFollowerFollowingRelationship relationship) {
        notNull(relationship, "relationship must not be null");
        this.followers.remove(relationship);
    }

    /**
     * Check if a given {@link User} is a follower of this {@link User}.
     *
     * @param user The {@link User} that will be checked.
     */

    public boolean hasFollower(final User user) {
        notNull(user, "user must not be null");
        return getFollower().contains(user);
    }

    /**
     * Check if this {@link User} is a follower of a given {@link User}.
     *
     * @param user The {@link User} that will be checked.
     */

    public boolean isFollowing(final User user) {
        notNull(user, "user must not be null");
        return getFollowings().contains(user);
    }

    /**
     * Get all followers of this {@link User}.
     *
     * @return A set of {@link User} representing the followers of this {@link User}.
     */

    public Set<User> getFollower() {

        Set<User> follower = new HashSet<>();

        if (this.followers.size() > 0) {
            this.followers
                    .stream()
                    .filter(relationship -> !relationship.getFollower().equals(this))
                    .forEach(relationship -> follower.add(relationship.getFollower()));
        }

        return follower;
    }

    /**
     * Get all {@link User} followed by this {@link User}.
     *
     * @return A set of {@link User} representing the followings of this {@link User}.
     */

    public Set<User> getFollowings() {

        Set<User> followings = new HashSet<>();

        if (this.followings.size() > 0) {
            this.followings
                    .stream()
                    .filter(relationship -> !relationship.getFollowing().equals(this))
                    .forEach(relationship -> followings.add(relationship.getFollowing()));
        }

        return followings;
    }

    public User() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        notNull(id, "id must not be null");
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Set<UserRoleRelationship> getRoles() {
        return roles;
    }

    public Set<Tweet> getTweets() {
        return tweets;
    }

    public Set<Tweet> getLikes() {
        return likes;
    }

    public String getIconPath() {
        return iconPath;
    }

    /**
     * Builder for the entity {@link User}.
     */

    public static class UserBuilder extends DefaultBuilder<User> {

        UserBuilder() {

            super();

            this.instance.followings = new HashSet<>();
            this.instance.followers = new HashSet<>();
            this.instance.likes = new HashSet<>();
            this.instance.tweets = new HashSet<>();
        }

        public UserBuilder withId(final Long id) {
            this.instance.id = notNull(id, "id must not be null");
            return this;
        }

        public UserBuilder withUsername(final String username) {
            this.instance.username = notNull(username, "username must not be null");
            return this;
        }

        public UserBuilder withPassword(final String password) {

            notNull(password, "password must not be null");

            PasswordEncoder passwordEncoder = new PasswordEncoder();
            this.instance.password = passwordEncoder.hashPassword(password);

            return this;
        }

        public UserBuilder withRole(final String role) {

            notNull(role, "role must not be null");

            Set<UserRoleRelationship> roles = new HashSet<>();
            UserRoleRelationship userRoleRelationship = new UserRoleRelationship();

            userRoleRelationship.setRole(role);
            userRoleRelationship.setUser(instance);

            roles.add(userRoleRelationship);
            this.instance.roles = roles;

            return this;
        }

        public UserBuilder withTweets(final Set<Tweet> tweets) {
            this.instance.tweets = notNull(tweets, "tweets must not be null");
            return this;
        }

        public UserBuilder addTweet(final Tweet tweet) {
            notNull(tweet, "tweet must not be null");
            this.instance.tweets.add(tweet);
            return this;
        }

        public UserBuilder withLikes(final Set<Tweet> tweets) {
            this.instance.likes = notNull(tweets, "tweets must not be null");
            return this;
        }

        public UserBuilder addLike(final Tweet like) {
            notNull(like, "like must not be null");
            this.instance.likes.add(like);
            return this;
        }

        public UserBuilder withIconPath(final String iconPath) {
            this.instance.iconPath = notNull(iconPath, "iconPath must not be null");
            return this;
        }
    }
}
