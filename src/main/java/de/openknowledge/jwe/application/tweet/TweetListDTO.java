package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.tweet.TweetPostDateTimeAdapter;
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

    @Schema(example = "11")
    private Long rootTweetId;

    @Schema(example = "Today is a good day!", required = true, minLength = 1, maxLength = 280)
    private String message;

    @Schema(example = "2018-01-01T12:34:56.000Z")
    @XmlJavaTypeAdapter(TweetPostDateTimeAdapter.class)
    private LocalDateTime postTime;

    public TweetListDTO() {
        super();
    }

    public TweetListDTO(final Tweet tweet) {

        this();

        notNull(tweet, "Tweet must not be null");

        this.id = tweet.getId();
        this.authorId = tweet.getAuthor().getId();
        this.rootTweetId = tweet.getRootTweet() != null ? tweet.getRootTweet().getId() : null;
        this.message = tweet.getMessage();
        this.postTime = tweet.getPostTime();
    }

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Long getRootTweetId() { return rootTweetId; }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getPostTime() {
        return postTime;
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