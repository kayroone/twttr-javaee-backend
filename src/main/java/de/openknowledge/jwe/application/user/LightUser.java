package de.openknowledge.jwe.application.user;

import de.openknowledge.jwe.domain.model.user.User;

public class LightUser extends AbstractUser {

    private Long id;

    // Public constructor needed for UserResource#searchUser:
    public LightUser() { }

    public LightUser(User user) {

        super();
        this.id = user.getId();
        setUsername(user.getUsername());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}