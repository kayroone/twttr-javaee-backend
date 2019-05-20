package de.openknowledge.jwe.infrastructure.security;

import org.wildfly.swarm.keycloak.deployment.KeycloakSecurityContextAssociation;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;

/**
 * Fire user authenticated event -> Will be processed in {@link AuthenticatedUserProducer} and propagate the
 * {@link SecurityContext} for this application.
 */

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

    @Inject
    @AuthenticatedUser
    Event<String> userAuthenticatedEvent;

    @Override
    public void filter(ContainerRequestContext requestContext) {

        SecurityContext securityContext = requestContext.getSecurityContext();
        String username = KeycloakSecurityContextAssociation.get().getToken().getPreferredUsername();
        Principal userPrincipal = () -> username;

        // Fire auth event:
        userAuthenticatedEvent.fire(username);

        // Propagate user security context:
        requestContext.setSecurityContext(new SecurityContext() {

            @Override
            public Principal getUserPrincipal() {
                return userPrincipal;
            }

            @Override
            public boolean isUserInRole(String role) {
                return securityContext.isUserInRole(role);
            }

            @Override
            public boolean isSecure() {
                return securityContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return securityContext.getAuthenticationScheme();
            }
        });
    }
}