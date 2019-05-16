package de.openknowledge.jwe.domain.model.user;

import de.openknowledge.jwe.domain.model.role.UserRole;
import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.infrastructure.domain.builder.DefaultBuilder;
import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
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

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "TAB_USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"))
    @Column(name = "USER_ROLE", nullable = false)
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL)
    @Column(name = "USER_FOLLOWING")
    private List<FollowerFollowingRelationship> followings;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    @Column(name = "USER_FOLLOWER")
    private List<FollowerFollowingRelationship> followers;

    @OneToMany
    @Column(name = "USER_TWEETS")
    private List<Tweet> tweets;

    @Size(max = 500)
    @Column(name = "USER_ICON_PATH", length = 500)
    private String iconPath;

    /**
     * Update this {@link User}.
     *
     * @param username
     * @param password
     * @param roles
     * @param iconPath
     */

    public void updateUser(String username, String password, Set<UserRole> roles, String iconPath) {

        this.username = notNull(username, "Username must nut be null");
        this.password = notNull(password, "Password must not be null");
        this.roles = notNull(roles, "Role must not be null");
        this.iconPath = iconPath;
    }

    public static UserBuilder newBuilder() {

        return new UserBuilder();
    }

    public User() {
        super();
    }

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

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
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

        public UserBuilder withPassword(final String password) {
            this.instance.password = notNull(password, "Password must not be null");
            return this;
        }

        public UserBuilder withRole(final Set<UserRole> roles) {
            this.instance.roles = roles;
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
