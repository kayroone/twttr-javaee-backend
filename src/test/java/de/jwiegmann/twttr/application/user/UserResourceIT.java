package de.jwiegmann.twttr.application.user;

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

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class UserResourceIT {

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

    apiUri = integrationTestContainers.getApiUri(Constants.USERS_API_URI);
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
  public void searchUserShouldReturn200() {

    String keyword = "fo";

    UserListDTO[] users =
        RestAssured.given()
            .param("keyword", keyword)
            .when()
            .get(apiUri)
            .then()
            .contentType(MediaType.APPLICATION_JSON)
            .statusCode(Response.Status.OK.getStatusCode())
            .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/User-schema.json"))
            .extract()
            .as(UserListDTO[].class);

    assertThat(users).hasSize(1);
    assertThat(users[0].getUsername()).isEqualTo("foo");
  }

  @Test
  public void searchUserShouldReturn204() {

    String keyword = "rofl";

    RestAssured.given()
        .param("keyword", keyword)
        .when()
        .get(apiUri)
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  public void searchUserShouldReturn400ForKeywordTooLong() {

    String keyword =
        "FoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobar"
            + "FoobarFoobarFoobarFoobarFoobarFoobar";

    RestAssured.given()
        .param("keyword", keyword)
        .when()
        .get(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void searchUserShouldReturn400ForKeywordTooShort() {

    String keyword = "";

    RestAssured.given()
        .param("keyword", keyword)
        .when()
        .get(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void followUserShouldReturn401ForUnauthorized() {

    RestAssured.given()
        .when()
        .put(getSingleItemUriWithPath(2L, "follow"))
        .then()
        .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
  }

  @Test
  public void followUserShouldReturn204() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .put(getSingleItemUriWithPath(2L, "follow"))
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  public void followUserShouldReturn400ForIdTooBig() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .put(getSingleItemUriWithPath(9999999L, "follow"))
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void followUserShouldReturn400ForIdTooSmall() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .put(getSingleItemUriWithPath(0L, "follow"))
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void followUserShouldReturn404ForUserNotFound() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .put(getSingleItemUriWithPath(10L, "follow"))
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void getTimelineForUserShouldReturn200() {

    createTestTweet();

    RestAssured.given()
        .param("offset", 0)
        .param("limit", 100)
        .when()
        .get(getSingleItemUri(2L))
        .then()
        .statusCode(Response.Status.OK.getStatusCode());
  }

  @Test
  public void getTimelineForUserShouldReturn404ForUserNotFound() {

    RestAssured.given()
        .param("offset", 0)
        .param("limit", 100)
        .when()
        .get(getSingleItemUri(404L))
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void getFollowerForUserShouldReturn200() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .put(getSingleItemUriWithPath(3L, "follow"))
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    RestAssured.given()
        .when()
        .get(getSingleItemUriWithPath(3L, "follower"))
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .body("size()", Matchers.is(1));
  }

  @Test
  public void getFollowerForUserShouldReturn204ForNoFollowerFound() {

    RestAssured.given()
        .when()
        .get(getSingleItemUriWithPath(2L, "follower"))
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  public void getFollowerForUserShouldReturn404ForUserNotFound() {

    RestAssured.given()
        .when()
        .get(getSingleItemUriWithPath(1L, "follower"))
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void getFollowingForUserShouldReturn200() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .when()
        .put(getSingleItemUriWithPath(3L, "follow"))
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());

    RestAssured.given()
        .when()
        .get(getSingleItemUriWithPath(2L, "following"))
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .body("size()", Matchers.is(1));
  }

  @Test
  public void getFollowingForUserShouldReturn204ForNoFollowerFound() {

    RestAssured.given()
        .when()
        .get(getSingleItemUriWithPath(3L, "following"))
        .then()
        .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  public void getFollowingForUserShouldReturn404ForUserNotFound() {

    RestAssured.given()
        .when()
        .get(getSingleItemUriWithPath(1L, "following"))
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  public void getProfileForUserShouldReturn200() {

    RestAssured.given()
        .when()
        .get(getSingleItemUri(2L))
        .then()
        .statusCode(Response.Status.OK.getStatusCode())
        .body("tweetCount", Matchers.is(0))
        .body("followingCount", Matchers.is(0))
        .body("followerCount", Matchers.is(0));
  }

  @Test
  public void getProfileForUserShouldReturn404() {

    RestAssured.given()
        .when()
        .get(getSingleItemUri(5L))
        .then()
        .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  private Long createTestTweet() {

    String tweetApiUri = integrationTestContainers.getApiUri(Constants.TWEETS_API_URI);

    String message = "Today is a good day!";

    JsonObject tweetJsonObject =
            Json.createObjectBuilder().add("message", message).add("postTime", testPostTime).build();

    String id =
            RestAssured.given()
                    .headers("Authorization", "Bearer " + authToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(tweetJsonObject.toString())
                    .when()
                    .post(tweetApiUri)
                    .then()
                    .contentType(MediaType.APPLICATION_JSON)
                    .statusCode(Response.Status.CREATED.getStatusCode())
                    .extract()
                    .jsonPath()
                    .getString("id");

    return Long.parseLong(id);
  }

  private URI getSingleItemUri(final Long userId) {

    return UriBuilder.fromUri(apiUri).path("{id}").build(userId);
  }

  private URI getSingleItemUriWithPath(final Long userId, final String path) {

    return UriBuilder.fromUri(apiUri).path("{id}").path(path).build(userId);
  }
}
