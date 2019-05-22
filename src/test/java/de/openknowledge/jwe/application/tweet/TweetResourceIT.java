package de.openknowledge.jwe.application.tweet;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;
import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.infrastructure.constants.Constants;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import net.minidev.json.JSONObject;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Integration test class for the resource {@link TweetResource}.
 */

public class TweetResourceIT {

    private String baseURI = IntegrationTestUtil.getBaseURI();

    @Rule
    public EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("test-local");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(() -> entityManagerProvider.connection());

    @Test
    @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true, transactional = true)
    @ExpectedDataSet(value = "src/test/resources/datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn201() {

        String message = "Today is a good day!";

        JSONObject tweetJSONObject = new JSONObject();
        tweetJSONObject.put("message", message);

        RestAssured.given()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(tweetJSONObject)
                .when()
                .post(getTweetsApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("json/schema/Tweet-schema.json"))
                .body("id", Matchers.notNullValue())
                .body("message", Matchers.equalTo(message))
                .body("postTime", Matchers.notNullValue())
                .body("author", Matchers.notNullValue());
    }

    private URI getTweetsApiUri() {

        return UriBuilder.fromUri(baseURI).path(Constants.ROOT_API_URI)
                .path(Constants.TWEETS_API_URI).build();
    }
}
