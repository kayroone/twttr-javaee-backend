package de.jwiegmann.twttr;

import de.jwiegmann.twttr.infrastructure.constants.Constants;
import de.jwiegmann.twttr.infrastructure.security.KeyCloakResourceLoader;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;

/** Setup integration test containers. */
public class IntegrationTestContainers extends AbstractTestContainers {

  private boolean withAuthContainer = false;
  private boolean withApiContainer = false;

  /**
   * Create a new test environment. This method does NOT start any containers.
   *
   * @return TestContainers instance.
   */
  public static IntegrationTestContainers newTestEnvironment() {

    return new IntegrationTestContainers();
  }

  private IntegrationTestContainers() {

    super();
  }

  public IntegrationTestContainers withAuthContainer() {

    this.withAuthContainer = true;
    return this;
  }

  public IntegrationTestContainers withApiContainer() {

    this.withApiContainer = true;
    return this;
  }

  public IntegrationTestContainers init() {

    if (this.withAuthContainer) {
      initAuthContainer();
    }
    if (this.withApiContainer) {
      initApiContainer();
    }

    return this;
  }

  public String getApiUri(String endpoint) {

    if (null == endpoint) {
      endpoint = "";
    }

    String uri = "http://{host}:{port}/{context}/{path}";
    return UriBuilder.fromUri(uri)
        .resolveTemplate("host", this.api.getContainerIpAddress())
        .resolveTemplate("port", this.api.getFirstMappedPort())
        .resolveTemplate("context", Constants.ROOT_SERVICE_URI)
        .resolveTemplate("path", endpoint)
        .toTemplate();
  }

  private String getAuthUrl() {

    String uri = "http://{host}:{port}/auth/";
    return UriBuilder.fromUri(uri)
        .resolveTemplate("host", AUTH_HOST_NAME)
        .resolveTemplate("port", AUTH_WEB_PORT)
        .toTemplate();
  }

  public String getAuthToken() throws IOException {

    if (!this.keycloak.isRunning()) {
      return null;
    }

    String keycloakUrl = getAuthUrl();
    return KeyCloakResourceLoader.getKeyCloakAccessToken(keycloakUrl);
  }
}
