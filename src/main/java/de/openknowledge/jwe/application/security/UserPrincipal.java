package de.openknowledge.jwe.application.security;

import de.openknowledge.jwe.domain.model.user.User;

import javax.security.auth.Subject;
import java.security.Principal;

/**
 * Principal class representing a {@link User}.
 */

public class UserPrincipal extends User implements Principal {

    @Override
    public String getName() {
        return super.getUsername();
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}
