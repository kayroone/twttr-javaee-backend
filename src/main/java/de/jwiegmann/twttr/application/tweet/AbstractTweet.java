package de.jwiegmann.twttr.application.tweet;

import de.jwiegmann.twttr.domain.tweet.Tweet;
import de.jwiegmann.twttr.domain.tweet.TweetValidationErrorCodes;
import de.jwiegmann.twttr.infrastructure.domain.value.AbstractValueObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/** Abstract Tweet class used for DTOs. */
public class AbstractTweet extends AbstractValueObject {

  @Schema(example = "2018-01-01 12:34:56", required = true, format = "date-time")
  @NotNull(payload = TweetValidationErrorCodes.PostTimeIsNull.class)
  private LocalDateTime postTime;

  @Schema(example = "Today is a good day!", required = true, minLength = 1, maxLength = 280)
  @NotNull(payload = TweetValidationErrorCodes.MessageIsNull.class)
  @Size(min = 1, max = 280, payload = TweetValidationErrorCodes.InvalidMessageSize.class)
  private String message;

  public AbstractTweet() {
    super();
  }

  AbstractTweet(final Tweet tweet) {

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
