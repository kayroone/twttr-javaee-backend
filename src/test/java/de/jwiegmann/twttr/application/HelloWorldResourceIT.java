package de.jwiegmann.twttr.application;

import de.jwiegmann.twttr.IntegrationTestContainers;
import de.jwiegmann.twttr.application.tweet.TweetResource;
import de.jwiegmann.twttr.infrastructure.constants.Constants;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.core.Response;

/** Integration test class for the resource {@link TweetResource}. */
@Testcontainers
public class HelloWorldResourceIT {

  private static String apiUri;

  @BeforeAll
  public static void setUp() {

    IntegrationTestContainers integrationTestContainers = IntegrationTestContainers.newTestEnvironment().withApiContainer();

    apiUri = integrationTestContainers.getApiUri(Constants.HELLO_WORLD_URI);
  }

  @Test
  public void createTweetShouldReturn201() {

    RestAssured.given().when().get(apiUri).then().statusCode(Response.Status.OK.getStatusCode());
  }
}
