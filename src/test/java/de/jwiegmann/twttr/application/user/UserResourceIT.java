package de.jwiegmann.twttr.application.user;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import de.jwiegmann.twttr.DockerComposeEnvironment;
import de.jwiegmann.twttr.domain.user.TestUser;
import de.jwiegmann.twttr.infrastructure.constants.Constants;
import de.jwiegmann.twttr.infrastructure.security.KeyCloakResourceLoader;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.DockerComposeContainer;
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

    private static String uri;
    private static String token;

    @Container
    private static DockerComposeContainer composeEnvironment = DockerComposeEnvironment.getEnvironment();

    @BeforeAll
    public static void setUp() throws IOException {

        uri = getUsersApiUri();
        token = KeyCloakResourceLoader.getKeyCloakAccessTokenForDefaultUser();
    }

    @Test
    @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
            transactional = true, disableConstraints = true)
    public void searchUserShouldReturn200() {

        String keyword = "j";

        UserListDTO[] users = RestAssured.given()
                .param("keyword", keyword)
                .when()
                .get(uri)
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.OK.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/User-schema.json"))
                .extract()
                .as(UserListDTO[].class);

        assertThat(users).hasSize(1);
        assertThat(users[0].getUsername()).isEqualTo(TestUser.newDefaultUser().getUsername());
    }

    /*@Test
    @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
            transactional = true, disableConstraints = true)
    public void searchUserShouldReturn204() {

        String keyword = "foo";

        RestAssured.given()
                .param("keyword", keyword)
                .when()
                .get(getUsersApiUri())
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
            transactional = true, disableConstraints = true)
    public void searchUserShouldReturn400ForKeywordTooLong() {

        String keyword = "FoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobar" +
                "FoobarFoobarFoobarFoobarFoobarFoobar";

        RestAssured.given()
                .param("keyword", keyword)
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
    public void searchUserShouldReturn400ForKeywordTooShort() {

        String keyword = "";

        RestAssured.given()
                .param("keyword", keyword)
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
    @ExpectedDataSet(value = "datasets/users-update-expected-follow.yml")
    public void followUserShouldReturn200() {

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .param("id", 2L)
                .when()
                .put(getUsersApiUri())
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
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

    private static String getUsersApiUri() {

        String uri = "http://{host}:{port}/{context}/{path}";
        return UriBuilder.fromUri(uri)
                .resolveTemplate("host", DockerComposeEnvironment.getApiHost())
                .resolveTemplate("port", DockerComposeEnvironment.getApiPort())
                .resolveTemplate("context", Constants.ROOT_SERVICE_URI)
                .resolveTemplate("path", Constants.USERS_API_URI)
                .toTemplate();
    }

    private URI getSingleItemUri(final Long userId) {

        return UriBuilder.fromUri(getUsersApiUri()).path("{id}").build(userId);
    }

    private URI getSingleItemUriWithPath(final String path, final Long userId) {

        return UriBuilder.fromUri(getUsersApiUri()).path("{id}").path(path).build(userId);
    }
}
