package de.openknowledge.jwe;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for container class {@link IntegrationTestContainer}.
 */

@Testcontainers
public class IntegrationTestContainerIT {

    @Container
    private static GenericContainer testContainer = IntegrationTestContainer.getContainer();

    @BeforeAll
    public static void testContainerIsRunning() {

        assertTrue(testContainer.isRunning());
    }

    @Test
    public void testStopContainerManually() {

        testContainer.stop();

        assertFalse(testContainer.isRunning());
    }
}
