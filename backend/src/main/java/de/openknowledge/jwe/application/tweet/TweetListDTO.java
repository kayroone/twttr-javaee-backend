package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.tweet.Tweet;
import de.openknowledge.jwe.domain.tweet.TweetPostDateTimeAdapter;
import de.openknowledge.jwe.infrastructure.domain.value.AbstractValueObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Tweet list DTO representing a {@link Tweet}.
 */

@Schema
public class TweetListDTO extends AbstractValueObject {

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

        this();

        notNull(tweet, "Tweet must not be null");

        setId(tweet.getId());
        setAuthorId(tweet.getAuthor().getId());
        setMessage(tweet.getMessage());
        setPostTime(tweet.getPostTime());

        this.rootTweetId = tweet.getRootTweet() != null ? tweet.getRootTweet().getId() : null;
    }

    Long getId() {
        return id;
    }

    void setId(Long id) {
        notNull(id, "id must not be null");
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    void setAuthorId(Long authorId) {
        notNull(authorId, "authorId must not be null");
        this.authorId = authorId;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        notNull(message, "message must not be null");
        this.message = message;
    }

    public LocalDateTime getPostTime() {
        return postTime;
    }

    void setPostTime(LocalDateTime postTime) {
        notNull(postTime, "postTime must not be null");
        this.postTime = postTime;
    }

    Long getRootTweetId() {
        return rootTweetId;
    }

    void setRootTweetId(Long rootTweetId) {
        this.rootTweetId = rootTweetId;
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
