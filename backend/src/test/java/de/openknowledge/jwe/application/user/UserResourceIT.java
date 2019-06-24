package de.openknowledge.jwe.application.user;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;
import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.domain.user.TestUser;
import de.openknowledge.jwe.infrastructure.constants.Constants;
import de.openknowledge.jwe.infrastructure.security.KeyCloakResourceLoader;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class UserResourceIT {

    private final String baseURI = IntegrationTestUtil.getBaseURI();

    private static String accessToken;

    @BeforeClass
    public static void initEntityManager() throws DatabaseUnitException {

        EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("test-local");
        IDatabaseConnection dbUnitConn = new DatabaseConnection(entityManagerProvider.connection(), "public");
        DatabaseConfig databaseConfig = dbUnitConn.getConfig();

        databaseConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());
    }

    @BeforeClass
    public static void getKeyCloakAccessToken() throws IOException {

        accessToken = new KeyCloakResourceLoader().getKeyCloakAccessToken();
    }

    @Test
    @DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true,
            transactional = true, disableConstraints = true)
    public void searchUserShouldReturn200() {

        String keyword = "j";

        UserListDTO[] users = RestAssured.given()
                .param("keyword", keyword)
                .when()
                .get(getUsersApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.OK.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/User-schema.json"))
                .extract()
                .as(UserListDTO[].class);

        assertThat(users).hasSize(1);
        assertThat(users[0].getUsername()).isEqualTo(TestUser.newDefaultUser().getUsername());
    }

    @Test
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
                .statusCode(Response.Status.OK.getStatusCode());
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
                .statusCode(Response.Status.OK.getStatusCode());
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
                .statusCode(Response.Status.OK.getStatusCode());
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

    private URI getSingleItemUri(final Long userId) {

        return UriBuilder.fromUri(getUsersApiUri()).path("{id}").build(userId);
    }

    private URI getSingleItemUriWithPath(final String path, final Long userId) {

        return UriBuilder.fromUri(getUsersApiUri()).path(path).path("{id}").build(userId);
    }

    private URI getUsersApiUri() {

        return UriBuilder.fromUri(baseURI).path(Constants.ROOT_API_URI)
                .path(Constants.USERS_API_URI).build();
    }
}