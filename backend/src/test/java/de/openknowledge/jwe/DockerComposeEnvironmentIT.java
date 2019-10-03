package de.openknowledge.jwe;

import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for container class {@link DockerComposeEnvironment}.
 */

@Testcontainers
public class DockerComposeEnvironmentIT {

    @Container
    private static DockerComposeContainer composeEnvironment = DockerComposeEnvironment.getEnvironment();

    /*@BeforeAll
    public static void testContainerIsRunning() {

        assertTrue(composeEnvironment.wa);
    }

    @Test
    public void testStopContainerManually() {

        composeEnvironment.stop();

        assertFalse(composeEnvironment.isRunning());
    }*/
}
