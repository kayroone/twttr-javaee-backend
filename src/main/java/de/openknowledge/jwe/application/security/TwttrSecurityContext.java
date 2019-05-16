package de.openknowledge.jwe.application.security;

import de.openknowledge.jwe.domain.model.role.UserRole;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.service.UserService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Custom security context to access the logged in {@link User} directly.
 */

@RequestScoped
public class TwttrSecurityContext {

    @Context
    SecurityContext securityContext;

    @Inject
    UserService userService;

    public User getUser() {

        Principal userPrincipal = securityContext.getUserPrincipal();

        return userService.findbyUsername(userPrincipal.getName());
    }

    public boolean isInRole(UserRole role) {

        return securityContext.isUserInRole(role.name());
    }
}
