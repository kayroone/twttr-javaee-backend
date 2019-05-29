package de.openknowledge.jwe.application.tweet;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.domain.model.tweet.TweetPostDateTimeDeserializer;
import de.openknowledge.jwe.domain.model.tweet.TweetPostDateTimeSerializer;
import de.openknowledge.jwe.infrastructure.domain.value.AbstractValueObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Lightweight Tweet DTO representing a {@link Tweet}.
 */

@Schema
public class TweetListDTO extends AbstractValueObject {

    @Schema(example = "1000")
    private Long id;

    @Schema(example = "12")
    private Long author;

    @Schema(example = "Today is a good day!", required = true, minLength = 1, maxLength = 280)
    private String message;

    @Schema(example = "2018-01-01T12:34:56.000Z", required = true, format = "date-time")
    @JsonSerialize(using = TweetPostDateTimeSerializer.class)
    @JsonDeserialize(using = TweetPostDateTimeDeserializer.class)
    private LocalDateTime postTime;

    public TweetListDTO() {
        super();
    }

    TweetListDTO(final Tweet tweet) {

        this();

        notNull(tweet, "Tweet must not be null");

        this.id = tweet.getId();
        this.author = tweet.getAuthor().getId();
        this.message = tweet.getMessage();
        this.postTime = tweet.getPostTime();
    }

    public Long getId() {
        return id;
    }

    public Long getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getPostTime() {
        return postTime;
    }

    @Override
    protected Object[] values() {
        return new Object[]{id, author, message, postTime};
    }

    @Override
    public String toString() {
        return "TweetListDTO{" +
                "id=" + id +
                ", author=" + author +
                ", message='" + message + '\'' +
                ", postTime=" + postTime +
                '}';
    }
}