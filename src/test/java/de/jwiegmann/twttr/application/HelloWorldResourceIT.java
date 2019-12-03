package de.jwiegmann.twttr.application;

import de.jwiegmann.twttr.IntegrationTestContainers;
import de.jwiegmann.twttr.application.tweet.TweetResource;
import de.jwiegmann.twttr.infrastructure.constants.Constants;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

/** Integration test class for the resource {@link TweetResource}. */
public class HelloWorldResourceIT {

  private static IntegrationTestContainers integrationTestContainers;
  private static String apiUri;

  @BeforeAll
  public static void init() {

    integrationTestContainers =
        IntegrationTestContainers.newTestEnvironment().withApiContainer().init();

    apiUri = integrationTestContainers.getApiUri(Constants.HELLO_WORLD_URI);
  }

  @AfterAll
  public static void teardown() {

    integrationTestContainers.teardownContainers();
  }

  @Test
  public void sayHelloShouldReturn200() {

    RestAssured.given().when().get(apiUri).then().statusCode(Response.Status.OK.getStatusCode());
  }
}
