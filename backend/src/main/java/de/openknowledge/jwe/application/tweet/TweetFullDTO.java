package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.application.user.UserListDTO;
import de.openknowledge.jwe.domain.tweet.Tweet;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Tweet full DTO representing a {@link Tweet}.
 */

@Schema
public class TweetFullDTO extends TweetListDTO {

    @Schema
    private Set<UserListDTO> liker;

    @Schema
    private Set<UserListDTO> retweeter;

    private TweetFullDTO() {
        super();
    }

    public TweetFullDTO(final Tweet tweet, final Set<UserListDTO> liker, final Set<UserListDTO> retweeter) {

        this();

        notNull(tweet, "tweet must not be null");
        notNull(liker, "liker must not be null");
        notNull(retweeter, "retweeter must not be null");

        Long rootTweetId = tweet.getRootTweet() != null ? tweet.getRootTweet().getId() : null;

        setId(tweet.getId());
        setAuthorId(tweet.getAuthor().getId());

        setMessage(tweet.getMessage());
        setPostTime(tweet.getPostTime());
        setRootTweetId(rootTweetId);

        this.liker = liker;
        this.retweeter = retweeter;
    }

    public Set<UserListDTO> getLiker() {
        return liker;
    }

    public Set<UserListDTO> getRetweeter() {
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
