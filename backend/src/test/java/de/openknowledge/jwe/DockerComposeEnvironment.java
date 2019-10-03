package de.openknowledge.jwe;

import org.testcontainers.containers.DockerComposeContainer;

import java.io.File;

/**
 * Setup integration test container.
 */

public class DockerComposeEnvironment {

  private static final int POSTGRES_DB__PORT = 5432;
  private static final int TWTTR_API_WEB_PORT = 8080;

  private static final String POSTGRES_HOST_NAME = "postgres";
  private static final String TWTTR_API_HOST_NAME = "twttr/api";

  /**
   * API container based on the thorntail showcase docker image.
   */

  public static DockerComposeContainer environment =
          new DockerComposeContainer(new File("docker-compose.yml"))
                  .withExposedService(POSTGRES_HOST_NAME, POSTGRES_DB__PORT)
                  .withExposedService(TWTTR_API_HOST_NAME, TWTTR_API_WEB_PORT);

  public static DockerComposeContainer getEnvironment() {

    return environment;
  }

  public static String getPostgresHost() {

    return environment.getServiceHost(POSTGRES_HOST_NAME, POSTGRES_DB__PORT);
  }

  public static int getPostgresPort() {

    return environment.getServicePort(POSTGRES_HOST_NAME, POSTGRES_DB__PORT);
  }

  public static String getTwttrHost() {

    return environment.getServiceHost(TWTTR_API_HOST_NAME, TWTTR_API_WEB_PORT);
  }

  public static int getTwttrPort() {

    return environment.getServicePort(TWTTR_API_HOST_NAME, TWTTR_API_WEB_PORT);
  }
}
