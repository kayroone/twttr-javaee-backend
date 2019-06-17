package de.openknowledge.jwe.application.tweet;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;
import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.application.user.UserListDTO;
import de.openknowledge.jwe.domain.model.tweet.TestTweet;
import de.openknowledge.jwe.domain.model.user.TestUser;
import de.openknowledge.jwe.domain.tweet.Tweet;
import de.openknowledge.jwe.domain.user.User;
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
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


/**
 * Integration test class for the resource {@link TweetResource}.
 */

@DataSet(value = "datasets/users-create.yml", strategy = SeedStrategy.CLEAN_INSERT,
        cleanBefore = true, transactional = true, disableConstraints = true)
public class TweetResourceIT {

    private final String baseURI = IntegrationTestUtil.getBaseURI();

    private static String accessToken;

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(getDBConnection());

    @BeforeClass
    public static void getKeyCloakAccessToken() throws IOException {

        accessToken = new KeyCloakResourceLoader().getKeyCloakAccessToken();
    }

    @Test
    @DataSet(value = "datasets/tweets-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn201() {

        String message = "Today is a good day!";
        String postTime = "2019-01-01T12:12:12.000Z";

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
                .body("postTime", Matchers.equalTo(postTime))
                .body("authorId", Matchers.notNullValue());
    }

    @Test
    @DataSet(value = "datasets/tweets-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-empty.yml")
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
    @DataSet(value = "datasets/tweets-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-empty.yml")
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
    @DataSet(value = "datasets/tweets-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-empty.yml")
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
    @DataSet(value = "datasets/tweets-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-empty.yml")
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
    @DataSet(value = "datasets/tweets-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-empty.yml")
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
    @DataSet(value = "datasets/tweets-delete-expected.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-delete-expected.yml")
    public void deleteTweetShouldReturn204() {

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .when()
                .delete(getSingleItemUri(1L))
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DataSet(value = "datasets/tweets-delete.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-delete.yml")
    public void deleteTweetShouldReturn404() {

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .when()
                .delete(getSingleItemUri(404L))
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DataSet(value = "datasets/tweets-update.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-update-expected-like.yml")
    public void likeTweetShouldReturn200() {

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .when()
                .put(getSingleItemUri(1L))
                .then()
                .statusCode(Response.Status.OK.getStatusCode());
    }

    @Test
    @DataSet(value = "datasets/tweets-create-retweet.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-retweet-expected.yml")
    public void retweetTweetShouldReturn201() {

        String message = "Foobar!";
        String postTime = "2019-01-01T12:12:12.000Z";

        JSONObject tweetJSONObject = new JSONObject();
        tweetJSONObject.put("message", message);
        tweetJSONObject.put("postTime", postTime);

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject.toString())
                .when()
                .post(getSingleItemUri(1L))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Tweet-schema.json"))
                .body("id", Matchers.equalTo(2))
                .body("message", Matchers.equalTo(message))
                .body("postTime", Matchers.equalTo(postTime))
                .body("authorId", Matchers.notNullValue())
                .body("rootTweetId", Matchers.equalTo(1));
    }

    @Test
    @DataSet(value = "datasets/tweets-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-empty.yml")
    public void retweetTweetShouldReturn404() {

        String message = "Foobar!";
        String postTime = "2019-01-01T12:12:12.000Z";

        JSONObject tweetJSONObject = new JSONObject();
        tweetJSONObject.put("message", message);
        tweetJSONObject.put("postTime", postTime);

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject.toString())
                .when()
                .post(getSingleItemUri(404L))
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DataSet(value = "datasets/tweets-create-get.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-get.yml")
    public void getTweetShouldReturn200() {

        User defaultUser = TestUser.newDefaultUser();

        Tweet defaultTweet = TestTweet.newDefaultTweet();
        TweetFullDTO tweetFullDTO = new TweetFullDTO(defaultTweet);

        Set<UserListDTO> liker = new HashSet<>();
        liker.add(new UserListDTO(defaultUser));

        Set<UserListDTO> retweeter = new HashSet<>();
        retweeter.add(new UserListDTO(defaultUser));

        tweetFullDTO.setLiker(liker);
        tweetFullDTO.setRetweeter(retweeter);

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(getSingleItemUri(2L))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.OK.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Tweet-schema.json"))
                .body("id", Matchers.equalTo(2))
                .body("message", Matchers.equalTo(tweetFullDTO.getMessage()))
                .body("postTime", Matchers.equalTo(tweetFullDTO.getPostTime()))
                .body("authorId", Matchers.equalTo(tweetFullDTO.getAuthorId()))
                .body("rootTweetId", Matchers.equalTo(1))
                .body("liker", Matchers.equalTo(tweetFullDTO.getLiker()))
                .body("retweeter", Matchers.equalTo(tweetFullDTO.getRetweeter()));
    }

    @Test
    @DataSet(value = "datasets/tweets-create-get.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-get.yml")
    public void getTweetShouldReturn404() {

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(getSingleItemUri(404L))
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DataSet(value = "datasets/tweets-create-get.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-get.yml")
    public void getMainTimelineShouldReturn200() {

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(getSingleItemUri(2L))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.OK.getStatusCode())
                .body("size()", Matchers.equalTo(3));
    }

    @Test
    @DataSet(value = "datasets/tweets-create-empty.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-empty.yml")
    public void getMainTimelineShouldReturn204() {

        RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(getSingleItemUri(2L))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.NO_CONTENT.getStatusCode())
                .body("size()", Matchers.equalTo(0));
    }

    private URI getTweetsApiUri() {

        return UriBuilder.fromUri(baseURI).path(Constants.ROOT_API_URI)
                .path(Constants.TWEETS_API_URI).build();
    }

    private URI getSingleItemUri(final Long tweetId) {

        return UriBuilder.fromUri(getTweetsApiUri()).path("{id}").build(tweetId);
    }

    private Connection getDBConnection() {

        try {
            EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("test-local");
            IDatabaseConnection dbUnitConn =
                    new DatabaseConnection(entityManagerProvider.connection(), "public");

            DatabaseConfig databaseConfig = dbUnitConn.getConfig();
            databaseConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true);
            databaseConfig.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, true);
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new PostgresqlDataTypeFactory());

            return dbUnitConn.getConnection();

        } catch (SQLException | DatabaseUnitException e) {
            e.printStackTrace();
        }

        return null;
    }
}
