package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.user.User;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/*
 * Lightweight User DTO representing meta data of a {@link User}.
 */

@Schema
public class UserProfileDTO extends UserListDTO {

    @Schema
    private int tweetCount;

    @Schema
    private int followingCount;

    @Schema
    private int followerCount;

    public UserProfileDTO(final User user, final int tweetCount,
                          final int followingCount, final int followerCount) {

        super(user);

        this.tweetCount = tweetCount;
        this.followingCount = followingCount;
        this.followerCount = followerCount;
    }

    public int getTweetCount() {
        return tweetCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    @Override
    protected Object[] values() {
        return new Object[]{getId(), getUsername(), tweetCount, followingCount, followerCount};
    }

    @Override
    public String toString() {
        return "UserProfileDTO{" +
                "id=" + getId() +
                "username=" + getUsername() +
                "tweetCount=" + tweetCount +
                ", followingCount=" + followingCount +
                ", followerCount=" + followerCount +
                '}';
    }
}
