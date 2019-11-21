package de.jwiegmann.twttr.application.tweet;

import de.jwiegmann.twttr.IntegrationTestContainers;
import de.jwiegmann.twttr.infrastructure.constants.Constants;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/** Integration test class for the resource {@link TweetResource}. */
@Testcontainers
public class TweetResourceIT {

  private static final String testPostTime = "2019-05-11T17:32:20.897";

  private static IntegrationTestContainers integrationTestContainers;
  private static String apiUri;
  private static String authToken;

  @BeforeAll
  public static void init() throws IOException {

    integrationTestContainers =
        IntegrationTestContainers.newTestEnvironment()
            .withAuthContainer()
            .withApiContainer()
            .init();

    apiUri = integrationTestContainers.getApiUri(Constants.TWEETS_API_URI);
    authToken = integrationTestContainers.getAuthToken();
  }

  @AfterAll
  public static void teardown() {

    integrationTestContainers.teardownDataSources();
  }

  @BeforeEach
  public void initDatabase() {

    integrationTestContainers.performQuery(
        "INSERT INTO tab_user(user_id, user_name, user_password) VALUES ('2', 'foo', 'foo')");
    integrationTestContainers.performQuery(
        "INSERT INTO tab_user_role_relationship(user_id, user_role) VALUES ('2', 'user-role')");
    integrationTestContainers.performQuery(
        "INSERT INTO tab_user(user_id, user_name, user_password) VALUES ('3', 'bar', 'bar')");
    integrationTestContainers.performQuery(
        "INSERT INTO tab_user_role_relationship(user_id, user_role) VALUES ('3', 'user-role')");
  }

  @AfterEach
  public void clearDatabase() {

    integrationTestContainers.performQuery(
        "TRUNCATE tab_user_follower_following_relationship CASCADE");
    integrationTestContainers.performQuery("TRUNCATE tab_user CASCADE");
    integrationTestContainers.performQuery("TRUNCATE tab_tweet CASCADE");
  }

  @Test
  public void createTweetShouldReturn201() {

    String message = "Today is a good day!";

    JsonObject tweetJsonObject =
        Json.createObjectBuilder().add("message", message).add("postTime", testPostTime).build();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(tweetJsonObject.toString())
        .when()
        .post(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.CREATED.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Tweet-schema.json"))
        .body("id", Matchers.notNullValue())
        .body("message", Matchers.equalTo(message))
        .body("postTime", Matchers.equalTo(testPostTime))
        .body("authorId", Matchers.notNullValue());
  }

  @Test
  public void createTweetShouldReturn400ForEmptyRequestBody() {

    JsonObject tweetJsonObject = Json.createObjectBuilder().build();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(tweetJsonObject.toString())
        .when()
        .post(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(2));
  }

  @Test
  public void createTweetShouldReturn400ForMissingMessage() {

    JsonObject tweetJsonObject = Json.createObjectBuilder().add("postTime", testPostTime).build();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(tweetJsonObject.toString())
        .when()
        .post(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void createTweetShouldReturn400ForMissingPostTime() {

    JsonObject tweetJsonObject =
        Json.createObjectBuilder().add("message", "Today is a good day!").build();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(tweetJsonObject.toString())
        .when()
        .post(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void createTweetShouldReturn400ForTooShortMessage() {

    JsonObject tweetJsonObject =
        Json.createObjectBuilder().add("message", "").add("postTime", testPostTime).build();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(tweetJsonObject.toString())
        .when()
        .post(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void createTweetShouldReturn400ForTooLongMessage() {

    String message =
        "FoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobar"
            + "FoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobar"
            + "FoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobar"
            + "FoobarFoobarFoobarFoobar";

    JsonObject tweetJsonObject =
        Json.createObjectBuilder().add("message", message).add("postTime", testPostTime).build();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(tweetJsonObject.toString())
        .when()
        .post(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void deleteTweetShouldReturn204() {

    Long testTweetId = createTestTweet();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .delete(getSingleItemUri(testTweetId))
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  public void deleteTweetShouldReturn404() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .delete(getSingleItemUri(404L))
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void likeTweetShouldReturn204() {

    Long testTweetId = createTestTweet();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .put(getSingleItemUriWithPath(testTweetId, "like"))
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  public void retweetTweetShouldReturn201() {

    Long testTweetId = createTestTweet();
    Integer testTweetIdForAssertion = Math.toIntExact(testTweetId);

    String message = "Today was a good day!";

    JsonObject retweetJsonObject =
        Json.createObjectBuilder().add("message", message).add("postTime", testPostTime).build();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(retweetJsonObject.toString())
        .when()
        .post(getSingleItemUri(testTweetId))
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.CREATED.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Tweet-schema.json"))
        .body("id", Matchers.notNullValue())
        .body("message", Matchers.equalTo(message))
        .body("postTime", Matchers.equalTo(testPostTime))
        .body("authorId", Matchers.notNullValue())
        .body("rootTweetId", Matchers.equalTo(testTweetIdForAssertion));
  }

  @Test
  public void retweetTweetShouldReturn404() {

    JsonObject tweetJsonObject =
        Json.createObjectBuilder()
            .add("message", "Today is a good day!")
            .add("postTime", testPostTime)
            .build();

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(tweetJsonObject.toString())
        .when()
        .post(getSingleItemUri(404L))
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void getTweetShouldReturn200() {

    Long testTweetId = createTestTweet();

    RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON)
        .when()
        .get(getSingleItemUri(testTweetId))
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.OK.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Tweet-schema.json"));
  }

  @Test
  public void getTweetShouldReturn404() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .contentType(MediaType.APPLICATION_JSON)
        .when()
        .get(getSingleItemUri(404L))
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void getMainTimelineShouldReturn200() {

    createTestTweet();

    RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON)
        .when()
        .get(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.OK.getStatusCode());
  }

  @Test
  public void getMainTimelineShouldReturn204() {

    RestAssured.given()
        .contentType(MediaType.APPLICATION_JSON)
        .when()
        .get(apiUri)
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  private Long createTestTweet() {

    String message = "Today is a good day!";

    JsonObject tweetJsonObject =
        Json.createObjectBuilder().add("message", message).add("postTime", testPostTime).build();

    String id =
        RestAssured.given()
            .headers("Authorization", "Bearer " + authToken)
            .contentType(MediaType.APPLICATION_JSON)
            .body(tweetJsonObject.toString())
            .when()
            .post(apiUri)
            .then()
            .contentType(MediaType.APPLICATION_JSON)
            .statusCode(Response.Status.CREATED.getStatusCode())
            .extract()
            .jsonPath()
            .getString("id");

    return Long.parseLong(id);
  }

  private URI getSingleItemUri(final Long tweetId) {

    return UriBuilder.fromUri(apiUri).path("{id}").build(tweetId);
  }

  private URI getSingleItemUriWithPath(final Long tweetId, final String path) {

    return UriBuilder.fromUri(apiUri).path("{id}").path(path).build(tweetId);
  }
}
