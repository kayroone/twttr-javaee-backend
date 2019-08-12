package de.openknowledge.jwe;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.ws.rs.core.UriBuilder;

import static junit.framework.TestCase.assertTrue;

/**
 * Abstract class to setup integration test containers.
 */

@Testcontainers
public abstract class AbstractContainer {

    private static final int THORNTAIL_WEB_PORT = 8081;

    /**
     * Database container based on the thorntail showcase docker image.
     */

    @Container
    private static GenericContainer apiContainer =
            new GenericContainer("twttr/api:1.0")
                    .withExposedPorts(THORNTAIL_WEB_PORT)
                    .waitingFor(
                            Wait.forHttp("/twttr-service/api/hello")
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

        EntityManager entityManager =
                Persistence.createEntityManagerFactory("test-local").createEntityManager();

        assertTrue(entityManager.isOpen());

        //databaseContainer.start();
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
     * Stop the API container manually.
     * <p>
     * The default behavior is that containers are shut down automatically after all tests have been run.
     */

    protected static void stopApiContainer() {

        apiContainer.stop();
    }

    protected static void restartApiContainer() {

        stopApiContainer();
        startApiContainer();
    }

    protected static boolean isApiContainerRunning() {

        return apiContainer.isRunning();
    }
}
