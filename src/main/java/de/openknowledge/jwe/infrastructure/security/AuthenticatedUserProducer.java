package de.openknowledge.jwe.infrastructure.security;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.UserRepository;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Receive an user authenticated event from {@link AuthenticationFilter} and provide the authenticated
 * {@link User} for this request scope.
 */

@RequestScoped
public class AuthenticatedUserProducer {

    @Inject
    UserRepository userRepository;

    @Produces
    @RequestScoped
    @AuthenticatedUser
    private AuthenticatedUserAdapter authenticatedUser;

    public void handleAuthenticationEvent(@Observes @AuthenticatedUser String username) {

        User authenticatedUser = userRepository.findByUsername(username);
        this.authenticatedUser = new AuthenticatedUserAdapter(authenticatedUser);
    }
}