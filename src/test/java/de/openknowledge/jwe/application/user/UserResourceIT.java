package de.openknowledge.jwe.application.user;

import com.github.database.rider.core.DBUnitRule;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;
import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.infrastructure.constants.Constants;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

public class UserResourceIT {

    private String baseURI = IntegrationTestUtil.getBaseURI();

    @Rule
    public EntityManagerProvider entityManagerProvider = EntityManagerProvider.instance("test-local");

    @Rule
    public DBUnitRule dbUnitRule = DBUnitRule.instance(() -> entityManagerProvider.connection());

    @Test
    @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true, transactional = true)
    @ExpectedDataSet(value = "src/test/resources/datasets/users-create-expected.yml")
    public void searchUserShouldReturn200() {

        String keyword = "jw";

        User[] users = RestAssured.given()
                .param("keyword", keyword)
                .when()
                .post(getUsersApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.OK.getStatusCode())
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("json/schema/Tweet-schema.json"))
                .extract()
                .as(User[].class);

        assertThat(users).hasSize(1);
        assertThat(users[0].getUsername()).isEqualTo(keyword);
    }

    @Test
    @DataSet(strategy = SeedStrategy.CLEAN_INSERT, cleanBefore = true, transactional = true)
    @ExpectedDataSet(value = "src/test/resources/datasets/users-create-expected.yml")
    public void searchUserShouldReturn204() {

        String keyword = "foo";

        RestAssured.given()
                .param("keyword", keyword)
                .when()
                .post(getUsersApiUri())
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    private URI getUsersApiUri() {

        return UriBuilder.fromUri(baseURI).path(Constants.ROOT_API_URI)
                .path(Constants.USERS_API_URI).build();
    }
}