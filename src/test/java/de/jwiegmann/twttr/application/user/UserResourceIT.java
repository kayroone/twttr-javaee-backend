package de.jwiegmann.twttr.application.user;

import de.jwiegmann.twttr.TestContainers;
import de.jwiegmann.twttr.infrastructure.constants.Constants;
import de.jwiegmann.twttr.infrastructure.security.KeyCloakResourceLoader;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class UserResourceIT {

  private static String apiUri;
  private static String authUri;
  private static String authToken;

  /* Start full test environment */
  @Container private static GenericContainer dbContainer = TestContainers.initDatabaseContainer();
  @Container private static GenericContainer authContainer = TestContainers.initAuthContainer();
  @Container private static GenericContainer apiContainer = TestContainers.initApiContainer();

  @BeforeAll
  public static void setUp() throws IOException {

    apiUri = getApiUri();
    authUri = getAuthUri();
    authToken = KeyCloakResourceLoader.getKeyCloakAccessToken(authUri);
  }

  @Test
  public void searchUserShouldReturn200() {

    String keyword = "j";

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
    assertThat(users[0].getUsername()).isEqualTo("jw");
  }

  @Test
  public void searchUserShouldReturn204() {

    String keyword = "foo";

    RestAssured.given()
        .param("keyword", keyword)
        .when()
        .get(getApiUri())
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
        .get(getApiUri())
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
        .get(getApiUri())
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
        .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
        .body("size()", Matchers.is(1));
  }

  @Test
  public void followUserShouldReturn200() {

    RestAssured.given()
        .headers("Authorization", "Bearer " + authToken)
        .param("id", 2L)
        .when()
        .put(getApiUri())
        .then()
        .statusCode(Response.Status.OK.getStatusCode());
  }

  /*@Test
  @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create.yml")
  public void followUserShouldReturn400ForMissingIdParameter() {

      RestAssured.given()
              .param("id", "")
              .when()
              .get(getUsersApiUri())
              .then()
              .contentType(MediaType.APPLICATION_JSON)
              .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
              .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
              .body("size()", Matchers.is(1));
  }

  @Test
  @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create.yml")
  public void followUserShouldReturn400ForIdTooBig() {

      RestAssured.given()
              .param("id", 999999)
              .when()
              .get(getUsersApiUri())
              .then()
              .contentType(MediaType.APPLICATION_JSON)
              .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
              .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
              .body("size()", Matchers.is(1));
  }

  @Test
  @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create.yml")
  public void followUserShouldReturn400ForIdTooSmall() {

      RestAssured.given()
              .param("id", 0)
              .when()
              .get(getUsersApiUri())
              .then()
              .contentType(MediaType.APPLICATION_JSON)
              .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
              .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
              .body("size()", Matchers.is(1));
  }

  @Test
  @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create.yml")
  public void followUserShouldReturn404ForUserNotFound() {

      RestAssured.given()
              .param("id", 87)
              .when()
              .get(getUsersApiUri())
              .then()
              .contentType(MediaType.APPLICATION_JSON)
              .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
              .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
              .body("size()", Matchers.is(1));
  }

  @Test
  @DataSet(value = "datasets/users-create-tweets.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create-tweets.yml")
  public void getTimelineForUserShouldReturn200() {

      RestAssured.given()
              .param("offset", 0)
              .param("limit", 100)
              .when()
              .get(getSingleItemUri(1L))
              .then()
              .statusCode(Response.Status.OK.getStatusCode())
              .body("size()", Matchers.is(3));
  }

  @Test
  @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create.yml")
  public void getTimelineForUserShouldReturn204ForNoTweetsFound() {

      RestAssured.given()
              .param("offset", 0)
              .param("limit", 100)
              .when()
              .get(getSingleItemUri(1L))
              .then()
              .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  @DataSet(value = "datasets/users-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create-empty.yml")
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
  @DataSet(value = "datasets/users-update-expected-follow.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-update-expected-follow.yml")
  public void getFollowerForUserShouldReturn200() {

      RestAssured.given()
              .when()
              .get(getSingleItemUriWithPath("follower", 2L))
              .then()
              .statusCode(Response.Status.OK.getStatusCode())
              .body("size()", Matchers.is(1));
  }

  @Test
  @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create.yml")
  public void getFollowerForUserShouldReturn204ForNoFollowerFound() {

      RestAssured.given()
              .when()
              .get(getSingleItemUriWithPath("follower", 1L))
              .then()
              .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  @DataSet(value = "datasets/users-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create-empty.yml")
  public void getFollowerForUserShouldReturn404ForUserNotFound() {

      RestAssured.given()
              .when()
              .get(getSingleItemUriWithPath("follower", 1L))
              .then()
              .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  @DataSet(value = "datasets/users-update-expected-follow.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-update-expected-follow.yml")
  public void getFollowingForUserShouldReturn200() {

      RestAssured.given()
              .when()
              .get(getSingleItemUriWithPath("following", 1L))
              .then()
              .statusCode(Response.Status.OK.getStatusCode())
              .body("size()", Matchers.is(1));
  }

  @Test
  @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create.yml")
  public void getFollowingForUserShouldReturn204ForNoFollowerFound() {

      RestAssured.given()
              .when()
              .get(getSingleItemUriWithPath("following", 1L))
              .then()
              .statusCode(Response.Status.NO_CONTENT.getStatusCode());
  }

  @Test
  @DataSet(value = "datasets/users-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create-empty.yml")
  public void getFollowingForUserShouldReturn404ForUserNotFound() {

      RestAssured.given()
              .when()
              .get(getSingleItemUriWithPath("following", 1L))
              .then()
              .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }

  @Test
  @DataSet(value = "datasets/users-create-tweets.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create-tweets.yml")
  public void getProfileForUserShouldReturn200() {

      RestAssured.given()
              .when()
              .get(getSingleItemUri(1L))
              .then()
              .statusCode(Response.Status.OK.getStatusCode())
              .body("tweetCount", Matchers.is(2))
              .body("followingCount", Matchers.is(0))
              .body("followerCount", Matchers.is(0));
  }

  @Test
  @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
          transactional = true, disableConstraints = true)
  @ExpectedDataSet(value = "datasets/users-create.yml")
  public void getProfileForUserShouldReturn404() {

      RestAssured.given()
              .when()
              .get(getSingleItemUri(1L))
              .then()
              .statusCode(Response.Status.NOT_FOUND.getStatusCode());
  }*/

  private static String getApiUri() {

    String uri = "http://{host}:{port}/{context}/{path}";
    return UriBuilder.fromUri(uri)
        .resolveTemplate("host", apiContainer.getContainerIpAddress())
        .resolveTemplate("port", apiContainer.getFirstMappedPort())
        .resolveTemplate("context", Constants.ROOT_SERVICE_URI)
        .resolveTemplate("path", Constants.USERS_API_URI)
        .toTemplate();
  }

  private static String getAuthUri() {

    String uri = "http://{host}:{port}/auth/";
    return UriBuilder.fromUri(uri)
        .resolveTemplate("host", authContainer.getContainerIpAddress())
        .resolveTemplate("port", authContainer.getMappedPort(8080))
        .toTemplate();
  }

  private URI getSingleItemUri(final Long userId) {

    return UriBuilder.fromUri(getApiUri()).path("{id}").build(userId);
  }

  private URI getSingleItemUriWithPath(final String path, final Long userId) {

    return UriBuilder.fromUri(getApiUri()).path("{id}").path(path).build(userId);
  }
}
