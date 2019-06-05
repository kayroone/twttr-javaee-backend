package de.openknowledge.jwe.domain.model.user;

import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * An entity that represents the relationship between a Users followers and followings.
 */

@Entity
@Table(name = "TAB_USER_FOLLOWER_FOLLOWING_RELATIONSHIP")
public class UserFollowerFollowingRelationship extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_FOLLOWER_FOLLOWING_RELATIONSHIP_ID")
    private long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_FOLLOWER_ID", referencedColumnName = "USER_ID")
    private User follower;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_FOLLOWING_ID", referencedColumnName = "USER_ID")
    private User following;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserFollowerFollowingRelationship() { super(); }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User following) {
        this.following = following;
    }
}