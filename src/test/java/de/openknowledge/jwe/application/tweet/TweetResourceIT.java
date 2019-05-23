package de.openknowledge.jwe.application.tweet;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;
import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.domain.model.user.TestUser;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.infrastructure.constants.Constants;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import java.util.ArrayList;

import static org.apache.http.entity.mime.MIME.UTF8_CHARSET;


/**
 * Integration test class for the resource {@link TweetResource}.
 */

public class TweetResourceIT {

    private String baseURI = IntegrationTestUtil.getBaseURI();

    private static String accessToken;

    @Rule
    public EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("test-local");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(() -> entityManagerProvider.connection());

    @Test
    @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true, transactional = true)
    @ExpectedDataSet(value = "datasets/tweets-create-expected.yml")
    public void createTweetShouldReturn201() {

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
                .statusCode(Response.Status.CREATED.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/Tweet-schema.json"))
                .body("id", Matchers.notNullValue())
                .body("message", Matchers.equalTo(message))
                .body("postTime", Matchers.notNullValue())
                .body("author", Matchers.notNullValue());
    }

    private URI getTweetsApiUri() {

        return UriBuilder.fromUri(baseURI).path(Constants.ROOT_API_URI)
                .path(Constants.TWEETS_API_URI).build();
    }

    @BeforeClass
    public static void getKeyCloakAccessToken() {

        User testUser = TestUser.newDefaultUser();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(IntegrationTestUtil.getKeyCloakAccessTokenURL());

        ArrayList<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("username", testUser.getUsername()));
        postParameters.add(new BasicNameValuePair("password", "password"));
        postParameters.add(new BasicNameValuePair("realm", "twttr"));
        postParameters.add(new BasicNameValuePair("grant_type", "password"));
        postParameters.add(new BasicNameValuePair("client_id", "twttr-service"));
        postParameters.add(new BasicNameValuePair("client_secret", "1f4cebfb-c1bc-43bb-87f1-a6ebda78f4ae"));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

            HttpResponse httpResponse = httpClient.execute(httpPost);
            String responseJSON = EntityUtils.toString(httpResponse.getEntity(), UTF8_CHARSET);

            JSONObject keyCloakResponse = new JSONObject(responseJSON);
            accessToken = keyCloakResponse.getString("access_token");
        } catch (IOException e) {
            // Ignore
        }
    }
}
