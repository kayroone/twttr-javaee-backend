package de.jwiegmann.twttr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import java.io.IOException;

/** Setup integration test container. */
public class TestContainers {

  private static final Logger LOG = LoggerFactory.getLogger(TestContainers.class);

  private static final int DB_TCP_PORT = 5432;
  private static final int AUTH_WEB_PORT = 8080;
  private static final int API_WEB_PORT = 8081;

  private static final String AUTH_IMAGE = "jboss/keycloak:latest";
  private static final String API_IMAGE = "registry.hub.docker.com/kayroone/twttr-api:latest";

  public static GenericContainer initDatabaseContainer() {

    return new PostgreSQLContainer()
        .withInitScript("src/test/resources/init.sql")
        .withDatabaseName("twttr")
        .withUsername("twttr")
        .withPassword("twttr")
        .withExposedPorts(DB_TCP_PORT)
        .waitingFor(Wait.forListeningPort());
  }

  public static GenericContainer initAuthContainer() {

    GenericContainer authContainer =
        new GenericContainer(AUTH_IMAGE)
            .withExposedPorts(AUTH_WEB_PORT)
            .withEnv("KEYCLOAK_USER", "admin")
            .withEnv("KEYCLOAK_PASSWORD", "admin")
            .withEnv("KEYCLOAK_IMPORT", "/tmp/keycloak-realm-export.json")
            .withClasspathResourceMapping(
                "src/test/resources/keycloak-realm-export.json",
                "/tmp/realm.json",
                BindMode.READ_ONLY)
            .withCopyFileToContainer(
                MountableFile.forClasspathResource(
                    "src/test/resources/keycloak-create-user.sh", 700),
                "/opt/jboss/keycloak-create-user.sh")
            .waitingFor(Wait.forHttp("/auth"));

    /* Create keycloak test user */
    try {
      Container.ExecResult commandResult =
          authContainer.execInContainer("sh", "/opt/jboss/keycloak-create-user.sh");

      if (commandResult.getExitCode() == 0) {
        LOG.info("Created new keycloak test user with name: jw");
      } else {
        LOG.warn(
            "Creating new keycloak test user failed with exit code: "
                + commandResult.getExitCode());
      }

    } catch (IOException | InterruptedException e) {
      LOG.error("Failed to execute keycloak-create-user.sh: " + e.getMessage());
    }

    return authContainer;
  }

  public static GenericContainer initApiContainer() {

    return new GenericContainer(API_IMAGE).withExposedPorts(API_WEB_PORT);
  }
}
