package de.openknowledge.jwe.domain.model.user;

import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.*;

/**
 * An entity that represents the relationship between a Users followers and followings.
 */

@Entity
@Table(name = "TAB_USER_FOLLOWER_FOLLOWING_RELATIONSHIP")
public class UserFollowerFollowingRelationship extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID_FOLLOWER_FOLLOWING_RELATIONSHIP")
    private long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "USER_ID_FOLLOWER")
    private User follower;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "USER_ID_FOLLOWING")
    private User following;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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