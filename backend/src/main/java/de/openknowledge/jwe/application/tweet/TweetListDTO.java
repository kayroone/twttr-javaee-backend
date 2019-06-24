package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.tweet.Tweet;
import de.openknowledge.jwe.domain.tweet.TweetPostDateTimeAdapter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

/**
 * Tweet list DTO representing a {@link Tweet}.
 */

@Schema
public class TweetListDTO extends AbstractTweet {

    @Schema(example = "1000")
    private Long id;

    @Schema(example = "12")
    private Long authorId;

    @Schema(example = "Today is a good day!", required = true, minLength = 1, maxLength = 280)
    private String message;

    @Schema(example = "2018-01-01T12:34:56.000Z")
    @XmlJavaTypeAdapter(TweetPostDateTimeAdapter.class)
    private LocalDateTime postTime;

    @Schema(example = "11")
    private Long rootTweetId;

    TweetListDTO() {
        super();
    }

    public TweetListDTO(final Tweet tweet) {

        super(tweet);

        this.id = tweet.getId();
        this.authorId = tweet.getAuthor().getId();
        this.rootTweetId = tweet.getRootTweet() != null ? tweet.getRootTweet().getId() : null;
    }

    Long getId() {
        return id;
    }

    Long getAuthorId() {
        return authorId;
    }

    Long getRootTweetId() {
        return rootTweetId;
    }

    @Override
    protected Object[] values() {
        return new Object[]{id, authorId, message, postTime, rootTweetId};
    }

    @Override
    public String toString() {
        return "TweetListDTO{" +
                "id=" + id +
                ", authorId=" + authorId +
                ", rootTweetId=" + rootTweetId +
                ", message='" + message + '\'' +
                ", postTime=" + postTime +
                '}';
    }
}
