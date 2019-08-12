package de.openknowledge.jwe;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.core.UriBuilder;

/**
 * Abstract class to setup integration test containers.
 */

@Testcontainers
public abstract class AbstractContainer {

    private static final int H2_TCP_PORT = 1521;
    private static final int H2_WEB_PORT = 81;
    private static final int THORNTAIL_WEB_PORT = 8080;

    /**
     * Database container based on a H2 docker image.
     */

    private static GenericContainer databaseContainer =
            new GenericContainer("oscarfonts/h2:latest")
                    .withExposedPorts(H2_TCP_PORT, H2_WEB_PORT);

    /**
     * Database container based on the thorntail showcase docker image.
     */

    @Container
    private static GenericContainer apiContainer =
            new GenericContainer("archetype/thorntail:0")
                    .withExposedPorts(THORNTAIL_WEB_PORT)
                    .waitingFor(
                            Wait.forHttp("/base/api/hello")
                    );

    /**
     * Construct the API URL the integration test should use.
     *
     * @param path The path that will be appended to the API containers host and port.
     * @return The full API URL.
     */

    protected static String getApiUrl(String path) {

        String uri = "http://{host}:{port}/{path}";
        return UriBuilder.fromUri(uri)
                .resolveTemplate("host", apiContainer.getContainerIpAddress())
                .resolveTemplate("port", apiContainer.getMappedPort(THORNTAIL_WEB_PORT))
                .resolveTemplate("path", path)
                .toTemplate();
    }

    /**
     * Start the database container manually.
     * <p>
     * The default behavior is that containers annotated with @Container are started automatically.
     */

    protected static void startDatabaseContainer() {

        databaseContainer.start();
    }

    /**
     * Start the API container manually.
     * <p>
     * The default behavior is that containers annotated with @Container are started automatically.
     */

    protected static void startApiContainer() {

        apiContainer.start();
    }

    /**
     * Stop the database container manually.
     * <p>
     * The default behavior is that containers are shut down automatically after all tests have been run.
     */

    protected static void stopDatabaseContainer() {

        databaseContainer.stop();
    }

    /**
     * Stop the API container manually.
     * <p>
     * The default behavior is that containers are shut down automatically after all tests have been run.
     */

    protected static void stopApiContainer() {

        apiContainer.stop();
    }

    protected static void restartContainers() {

        stopApiContainer();
        stopDatabaseContainer();

        startDatabaseContainer();
        startApiContainer();
    }

    protected static boolean isDatabaseContainerRunning() {

        return databaseContainer.isRunning();
    }

    protected static boolean isApiContainerRunning() {

        return apiContainer.isRunning();
    }
}
