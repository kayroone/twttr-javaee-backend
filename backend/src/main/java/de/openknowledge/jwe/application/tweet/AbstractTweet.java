package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.tweet.Tweet;
import de.openknowledge.jwe.domain.tweet.TweetPostDateTimeAdapter;
import de.openknowledge.jwe.domain.tweet.TweetValidationErrorCodes;
import de.openknowledge.jwe.infrastructure.domain.value.AbstractValueObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;

/**
 * Abstract Tweet class used for DTOs.
 */

public class AbstractTweet extends AbstractValueObject {

    @Schema(example = "2018-01-01T12:34:56.000Z", required = true, format = "date-time")
    @NotNull(payload = TweetValidationErrorCodes.PostTimeIsNull.class)
    @XmlJavaTypeAdapter(TweetPostDateTimeAdapter.class)
    private LocalDateTime postTime;

    @Schema(example = "Today is a good day!", required = true, minLength = 1, maxLength = 280)
    @NotNull(payload = TweetValidationErrorCodes.MessageIsNull.class)
    @Size(min = 1, max = 280, payload = TweetValidationErrorCodes.InvalidMessageSize.class)
    private String message;

    AbstractTweet() { super(); }

    AbstractTweet(Tweet tweet) {

        super();

        this.message = tweet.getMessage();
        this.postTime = tweet.getPostTime();
    }

    public LocalDateTime getPostTime() {
        return postTime;
    }

    public void setPostTime(LocalDateTime postTime) {
        this.postTime = postTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    protected Object[] values() {
        return new Object[0];
    }
}
