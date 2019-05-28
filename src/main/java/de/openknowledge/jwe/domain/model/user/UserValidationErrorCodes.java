package de.openknowledge.jwe.domain.model.user;

import de.openknowledge.jwe.infrastructure.validation.ValidationErrorPayload;

public class UserValidationErrorCodes {

    public static class UsernameIsNull extends ValidationErrorPayload {

        public UsernameIsNull() {
            super("USERNAME_IS_NULL");
        }
    }
}
