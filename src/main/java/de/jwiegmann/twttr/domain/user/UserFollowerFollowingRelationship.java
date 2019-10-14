package de.jwiegmann.twttr.domain.user;

import de.jwiegmann.twttr.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * An entity that represents the relationship between a Users followers and followings.
 */

@Entity
@Table(name = "tab_user_follower_following_relationship")
public class UserFollowerFollowingRelationship extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_follower_following_relationship_id")
    private long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_follower_id", referencedColumnName = "user_id")
    private User follower;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_following_id", referencedColumnName = "user_id")
    private User following;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserFollowerFollowingRelationship() {
        super();
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(final User follower) {
        notNull(follower, "follower must not be null");
        this.follower = follower;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(final User following) {
        notNull(following, "following must not be null");
        this.following = following;
    }
}
