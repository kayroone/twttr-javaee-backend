package de.openknowledge.jwe;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Setup integration test container.
 */

public class IntegrationTestContainer {

    private static final int THORNTAIL_WEB_PORT = 8080;

    /**
     * API container based on the thorntail showcase docker image.
     */

    private static final GenericContainer container =
            new GenericContainer("twttr/api:1.0")
                    .withExposedPorts(THORNTAIL_WEB_PORT)
                    .waitingFor(
                            Wait.forHttp("/twttr-service/api/hello")
                    );

    public static GenericContainer getContainer() {

        return container;
    }
}
