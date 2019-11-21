package de.jwiegmann.twttr.domain.user;

import de.jwiegmann.twttr.infrastructure.validation.ValidationErrorPayload;

class UserValidationErrorCodes {

  private static class UsernameIsNull extends ValidationErrorPayload {

    public UsernameIsNull() {
      super("USERNAME_IS_NULL");
    }
  }
}
