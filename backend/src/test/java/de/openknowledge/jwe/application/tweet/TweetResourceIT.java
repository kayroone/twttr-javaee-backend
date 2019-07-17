package de.openknowledge.jwe.application.tweet;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;
import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.infrastructure.constants.Constants;
import de.openknowledge.jwe.infrastructure.security.KeyCloakResourceLoader;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;


/**
 * Integration test class for the resource {@link TweetResource}.
 */
public class TweetResourceIT {

    private final String baseURI = IntegrationTestUtil.getBaseURI();

    private static String accessToken;

    @Rule
    public EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("test-local");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(entityManagerProvider.connection());

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

        JsonObject tweetJsonObject = Json.createObjectBuilder()
                .add("message", message)
                .add("postTime", postTime)
                .build();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJsonObject.toString())
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

        JsonObject tweetJsonObject = Json.createObjectBuilder().build();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJsonObject.toString())
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

        JsonObject tweetJsonObject = Json.createObjectBuilder()
                .add("postTime", postTime.toString())
                .build();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJsonObject.toString())
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

        JsonObject tweetJsonObject = Json.createObjectBuilder()
                .add("message", "Today is a good day!")
                .build();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJsonObject.toString())
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

        JsonObject tweetJsonObject = Json.createObjectBuilder()
                .add("message", "")
                .add("postTime", LocalDateTime.now().toString())
                .build();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJsonObject.toString())
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

        JsonObject tweetJsonObject = Json.createObjectBuilder()
                .add("message", message)
                .add("postTime", LocalDateTime.now().toString())
                .build();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJsonObject.toString())
                .when()
                .post(getTweetsApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/ErrorResponses-schema.json"))
                .body("size()", Matchers.is(1));
    }

    @Test
    @DataSet(value = "datasets/tweets-delete.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true)
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
    public void likeTweetShouldReturn204() {

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .when()
                .put(getSingleItemUriWithPath("like", 1L))
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DataSet(value = "datasets/tweets-create-retweet.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-retweet-expected.yml")
    public void retweetTweetShouldReturn201() {

        String message = "Today is a good day!";
        String postTime = "2019-01-01T12:12:12.000Z";

        JsonObject tweetJsonObject = Json.createObjectBuilder()
                .add("message", message)
                .add("postTime", postTime)
                .build();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJsonObject.toString())
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

        JsonObject tweetJsonObject = Json.createObjectBuilder()
                .add("message", "Today is a good day!")
                .add("postTime", "2019-01-01T12:12:12.000Z")
                .build();

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJsonObject.toString())
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

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(getSingleItemUri(2L))
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.OK.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Tweet-schema.json"));
    }

    @Test
    @DataSet(value = "datasets/tweets-create-get.yml", strategy = SeedStrategy.CLEAN_INSERT,
            cleanBefore = true, transactional = true, disableConstraints = true)
    @ExpectedDataSet(value = "datasets/tweets-create-get.yml")
    public void getTweetShouldReturn404() {

        RestAssured.given()
                .headers("Authorization", "Bearer " + accessToken)
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
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(getTweetsApiUri())
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
                .headers("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .when()
                .get(getTweetsApiUri())
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

    private URI getSingleItemUriWithPath(final String path, final Long userId) {

        return UriBuilder.fromUri(getTweetsApiUri()).path("{id}").path(path).build(userId);
    }
}
