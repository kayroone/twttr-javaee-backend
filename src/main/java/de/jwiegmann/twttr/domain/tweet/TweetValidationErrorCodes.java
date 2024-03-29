package de.jwiegmann.twttr.domain.tweet;

import de.jwiegmann.twttr.infrastructure.validation.ValidationErrorPayload;

public class TweetValidationErrorCodes {

  public static class MessageIsNull extends ValidationErrorPayload {

    public MessageIsNull() {
      super("MESSAGE_IS_NULL");
    }
  }

  public static class PostTimeIsNull extends ValidationErrorPayload {

    public PostTimeIsNull() {
      super("POST_TIME_IS_NULL");
    }
  }

  public static class InvalidMessageSize extends ValidationErrorPayload {

    public InvalidMessageSize() {
      super("MESSAGE_INVALID_SIZE");
    }
  }
}
