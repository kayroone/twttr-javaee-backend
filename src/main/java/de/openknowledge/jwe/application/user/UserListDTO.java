package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.infrastructure.domain.value.AbstractValueObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Lightweight User DTO representing a {@link User}.
 */

@Schema
public class UserListDTO extends AbstractValueObject {

    @Schema(example = "1000")
    private Long id;

    @Schema(example = "foobar@example.de")
    private String username;

    public UserListDTO() { super(); }

    UserListDTO(final User user) {

        this();

        notNull(user, "User must not be null");

        this.id = user.getId();
        this.username = user.getUsername();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    protected Object[] values() {
        return new Object[]{id, username};
    }

    @Override
    public String toString() {
        return "UserListDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}