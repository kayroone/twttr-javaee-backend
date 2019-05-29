package de.openknowledge.jwe.application.tweet;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;
import de.openknowledge.jwe.IntegrationTestUtil;
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
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;


/**
 * Integration test class for the resource {@link TweetResource}.
 */

@DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT,
        cleanBefore = true, transactional = true, disableConstraints = true)
public class TweetResourceIT {

    private String baseURI = IntegrationTestUtil.getBaseURI();

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
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn201() {

        String message = "Today is a good day!";
        LocalDateTime postTime = LocalDateTime.now();

        JSONObject tweetJSONObject = new JSONObject();
        tweetJSONObject.put("message", message);
        tweetJSONObject.put("postTime", postTime);

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject.toString())
                .when()
                .post(getTweetsApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Tweet-schema.json"))
                .body("id", Matchers.notNullValue())
                .body("message", Matchers.equalTo(message))
                .body("postTime", Matchers.equalTo(postTime.toString()))
                .body("author", Matchers.notNullValue());
    }

    @Test
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn400ForEmptyRequestBody() {

        JSONObject tweetJSONObject = new JSONObject();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject.toString())
                .when()
                .post(getTweetsApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
                .body("size()", Matchers.is(2));
    }

    @Test
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn400ForMissingMessage() {

        LocalDateTime postTime = LocalDateTime.now();

        JSONObject tweetJSONObject = new JSONObject();
        tweetJSONObject.put("postTime", postTime);

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject.toString())
                .when()
                .post(getTweetsApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
                .body("size()", Matchers.is(1));
    }

    @Test
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn400ForMissingPostTime() {

        String message = "Today is a good day!";

        JSONObject tweetJSONObject = new JSONObject();
        tweetJSONObject.put("message", message);

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject.toString())
                .when()
                .post(getTweetsApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
                .body("size()", Matchers.is(1));
    }

    @Test
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn400ForTooShortMessage() {

        String message = "";
        LocalDateTime postTime = LocalDateTime.now();

        JSONObject tweetJSONObject = new JSONObject();
        tweetJSONObject.put("message", message);
        tweetJSONObject.put("postTime", postTime);

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject.toString())
                .when()
                .post(getTweetsApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
                .body("size()", Matchers.is(1));
    }

    @Test
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn400ForTooLongMessage() {

        String message = "FoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobar" +
                "FoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobar" +
                "FoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobarFoobar" +
                "FoobarFoobarFoobarFoobar";
        LocalDateTime postTime = LocalDateTime.now();

        JSONObject tweetJSONObject = new JSONObject();
        tweetJSONObject.put("message", message);
        tweetJSONObject.put("postTime", postTime);

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject.toString())
                .when()
                .post(getTweetsApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
                .body("size()", Matchers.is(1));
    }

    @Test
    @DataSet(value = "datasets/tweets-create.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void deleteTweetShouldReturn204() {

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .when()
                .delete(getTweetsApiUri() + "/" + 1)
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DataSet(value = "datasets/tweets-create.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void deleteTweetShouldReturn404() {

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .when()
                .delete(getTweetsApiUri() + "/" + 403)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    private URI getTweetsApiUri() {

        return UriBuilder.fromUri(baseURI).path(Constants.ROOT_API_URI)
                .path(Constants.TWEETS_API_URI).build();
    }
}