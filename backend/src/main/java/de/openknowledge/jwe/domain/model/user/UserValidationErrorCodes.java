package de.openknowledge.jwe.domain.model.user;

import de.openknowledge.jwe.infrastructure.validation.ValidationErrorPayload;

class UserValidationErrorCodes {

    private static class UsernameIsNull extends ValidationErrorPayload {

        public UsernameIsNull() {
            super("USERNAME_IS_NULL");
        }
    }
}
