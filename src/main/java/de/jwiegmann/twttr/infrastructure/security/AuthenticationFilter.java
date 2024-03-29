package de.jwiegmann.twttr.infrastructure.security;

import org.keycloak.KeycloakSecurityContext;
import org.wildfly.swarm.keycloak.deployment.KeycloakSecurityContextAssociation;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;

@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

  @Override
  public void filter(final ContainerRequestContext requestContext) {

    SecurityContext securityContext = requestContext.getSecurityContext();

    KeycloakSecurityContext keycloakSecurityContext = KeycloakSecurityContextAssociation.get();

    if (keycloakSecurityContext != null) {

      String username = KeycloakSecurityContextAssociation.get().getToken().getPreferredUsername();
      Principal userPrincipal = () -> username;

      // Propagate user security context:
      requestContext.setSecurityContext(
          new SecurityContext() {

            @Override
            public Principal getUserPrincipal() {
              return userPrincipal;
            }

            @Override
            public boolean isUserInRole(final String role) {
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
}
