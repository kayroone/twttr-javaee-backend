package de.jwiegmann.twttr;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

/**
 * Setup integration test container.
 */

public class DockerComposeEnvironment {

    private static final int DB_PORT = 5432;
  private static final int KEYCLOAK_WEB_PORT = 8080;
    private static final int API_WEB_PORT = 8081;

    private static final String DB_HOST_NAME = "postgres";
  private static final String KEYCLOAK_HOST_NAME = "keycloak";
  private static final String API_HOST_NAME = "twttr";

    /**
     * Docker compose container based on local docker-compose.yml file.
     */

    public static DockerComposeContainer environment =
            new DockerComposeContainer(new File("src/test/resources/docker-compose.yml"))
                    .withLocalCompose(true)
                    .withExposedService(DB_HOST_NAME, DB_PORT, Wait.forListeningPort())
                    .withExposedService(KEYCLOAK_HOST_NAME, KEYCLOAK_WEB_PORT, Wait.forListeningPort())
                    .withExposedService(API_HOST_NAME, API_WEB_PORT, Wait.forHttp("/service/hello"));

    public static DockerComposeContainer getEnvironment() {

        return environment;
    }

    public static String getDbHost() {

        return environment.getServiceHost(DB_HOST_NAME, DB_PORT);
    }

    public static int getDbPort() {

        return environment.getServicePort(DB_HOST_NAME, DB_PORT);
    }

    public static String getApiHost() {

        return environment.getServiceHost(API_HOST_NAME, API_WEB_PORT);
    }

    public static int getApiPort() {

        return environment.getServicePort(API_HOST_NAME, API_WEB_PORT);
    }
}
