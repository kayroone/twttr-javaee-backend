package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.model.user.UserValidationErrorCodes;
import de.openknowledge.jwe.infrastructure.domain.value.AbstractValueObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.validation.constraints.NotNull;

public class AbstractUser extends AbstractValueObject {

    @Schema(example = "foobar@example.de", required = true, minLength = 1, maxLength = 500)
    @NotNull(payload = UserValidationErrorCodes.UsernameIsNull.class)
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    protected Object[] values() {
        return new Object[0];
    }
}