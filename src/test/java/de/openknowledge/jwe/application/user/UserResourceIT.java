package de.openknowledge.jwe.application.user;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.SeedStrategy;
import com.github.database.rider.core.util.EntityManagerProvider;
import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.domain.model.user.TestUser;
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

    private URI getUsersApiUri() {

        return UriBuilder.fromUri(baseURI).path(Constants.ROOT_API_URI)
                .path(Constants.USERS_API_URI).build();
    }
}