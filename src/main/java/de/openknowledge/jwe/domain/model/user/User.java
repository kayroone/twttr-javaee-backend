package de.openknowledge.jwe.domain.model.user;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.infrastructure.domain.builder.DefaultBuilder;
import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;
import de.openknowledge.jwe.infrastructure.security.PasswordEncoder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * An entity that represents a {@link User}.
 */

@Entity
@Table(name = "TAB_USER")
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

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "user")
    private Set<UserRoleRelationship> roles;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserFollowerFollowingRelationship> followings;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserFollowerFollowingRelationship> followers;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Tweet> tweets;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "TAB_USER_TWEET_LIKE",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TWEET_ID"))
    private Set<Tweet> likes;

    @Size(max = 500)
    @Column(name = "USER_ICON_PATH", length = 500)
    private String iconPath;

    static UserBuilder newBuilder() {
        return new UserBuilder();
    }

    /**
     * Update this {@link User}.
     *
     * @param username
     * @param password
     * @param roles
     * @param iconPath
     */

    public void updateUser(String username, String password, Set<UserRoleRelationship> roles, String iconPath) {

        this.username = notNull(username, "Username must nut be null");
        this.password = notNull(password, "Password must not be null");
        this.roles = notNull(roles, "Role must not be null");
        this.iconPath = iconPath;
    }

    /**
     * Checks if a given {@link Tweet} is already liked by this {@link User}.
     *
     * @param tweet
     * @return true if the {@link Tweet} is already liked or false if not.
     */

    public boolean hasLike(final Tweet tweet) {

        return this.likes.contains(tweet);
    }

    /**
     * Add a {@link Tweet} to own likes list.
     *
     * @param tweet
     */

    public void addLike(final Tweet tweet) {

        this.likes.add(tweet);
    }

    /**
     * Remove a {@link Tweet} from own likes list.
     *
     * @param tweet
     */

    public void removeLike(final Tweet tweet) {

        this.likes.remove(tweet);
    }

    public User() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Set<UserFollowerFollowingRelationship> getFollowers() {
        return followers;
    }

    public Set<UserFollowerFollowingRelationship> getFollowings() {
        return followings;
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

        public UserBuilder() {
            super();
        }

        public UserBuilder withUsername(String username) {
            this.instance.username = notNull(username, "Username must not be null");
            return this;
        }

        public UserBuilder withId(Long id) {
            this.instance.id = notNull(id, "Id must not be null");
            return this;
        }

        public UserBuilder withPassword(final String password) {

            // Never store user passwords in clear text:
            PasswordEncoder passwordEncoder = new PasswordEncoder();
            this.instance.password = passwordEncoder
                    .hashPassword(notNull(password, "Password must not be null"));
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

        public UserBuilder withFollowings(final Set<UserFollowerFollowingRelationship> followings) {
            this.instance.followings = notNull(followings, "followings must not be null");
            return this;
        }

        public UserBuilder withFollowers(final Set<UserFollowerFollowingRelationship> followers) {
            this.instance.followers = notNull(followers, "followers must not be null");
            return this;
        }

        public UserBuilder withTweetList(final Set<Tweet> tweets) {
            this.instance.tweets = notNull(tweets, "tweets must not be null");
            return this;
        }

        public UserBuilder withLikes(final Set<Tweet> tweets) {
            this.instance.likes = notNull(tweets, "likes must not be null");
            return this;
        }

        public UserBuilder withIconPath(final String iconPath) {
            this.instance.iconPath = notNull(iconPath, "iconPath must not be null");
            return this;
        }
    }
}
