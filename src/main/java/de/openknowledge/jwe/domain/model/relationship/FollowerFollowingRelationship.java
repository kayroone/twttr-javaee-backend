package de.openknowledge.jwe.domain.model.relationship;

import de.openknowledge.jwe.domain.model.user.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "FOLLOWER_FOLLOWING_RELATIONSHIP")
public class FollowerFollowingRelationship implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FOLLOWER_FOLLOWING_RELATIONSHIP")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ID_FOLLOWER")
    private User follower;

    @ManyToOne
    @JoinColumn(name = "ID_FOLLOWING")
    private User following;

    // GETTER AND SETTER ----------------------------------------------------------------------------------------------

    public long getId() {
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
