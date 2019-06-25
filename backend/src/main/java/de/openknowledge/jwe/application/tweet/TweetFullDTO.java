package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.application.user.UserListDTO;
import de.openknowledge.jwe.domain.tweet.Tweet;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Tweet full DTO representing a {@link Tweet}.
 */

@Schema
public class TweetFullDTO extends TweetListDTO {

    @Schema
    private List<UserListDTO> liker;

    @Schema
    private List<UserListDTO> retweeter;

    private TweetFullDTO() {
        super();
    }

    public TweetFullDTO(final Tweet tweet, final List<UserListDTO> liker, final List<UserListDTO> retweeter) {

        super(tweet);

        notNull(liker, "liker must not be null");
        notNull(retweeter, "retweeter must not be null");

        this.liker = liker;
        this.retweeter = retweeter;
    }

    public List<UserListDTO> getLiker() {
        return liker;
    }

    public List<UserListDTO> getRetweeter() {
        return retweeter;
    }

    @Override
    protected Object[] values() {
        return new Object[]{getId(), getAuthorId(), getMessage(), getPostTime(), getRootTweetId(), liker, retweeter};
    }

    @Override
    public String toString() {
        return "TweetFullDTO{" +
                "id=" + getId() +
                ", authorId=" + getAuthorId() +
                ", rootTweetId=" + getRootTweetId() +
                ", message='" + getMessage() + '\'' +
                ", postTime=" + getPostTime() +
                ", liker=" + liker +
                ", retweeter=" + retweeter +
                '}';
    }
}
