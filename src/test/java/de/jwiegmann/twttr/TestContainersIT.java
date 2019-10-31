package de.jwiegmann.twttr;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.wildfly.common.Assert.assertTrue;

/** Integration test class for the resource {@link TestContainers}. */
@Testcontainers
public class TestContainersIT {

  @Container private static final GenericContainer dbContainer = TestContainers.initDatabaseContainer();
  @Container private static final GenericContainer authContainer = TestContainers.initAuthContainer();
  @Container private static final GenericContainer apiContainer = TestContainers.initApiContainer();

  @Test
  public void checkDbContainerIsRunning() {

    assertTrue(dbContainer.isRunning());
  }

  @Test
  public void checkAuthContainerIsRunning() {

    assertTrue(authContainer.isRunning());
  }

  @Test
  public void checkApiContainerIsRunning() {

    assertTrue(apiContainer.isRunning());
  }
}
