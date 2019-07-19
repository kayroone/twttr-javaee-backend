package de.openknowledge.jwe.infrastructure.security;

import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.domain.user.TestUser;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class providing functionality to fetch resources from the KeyCloak server.
 */

public class KeyCloakResourceLoader {

    private final String KEYCLOAK_ACCESS_TOKEN_PROPERTY = "access_token";

    private final String KEYCLOAK_CONFIG_FILE = "keycloak.json";

    private final String KEYCLOAK_GRANT_TYPE_REQUEST_PARAMETER = "grant_type";
    private final String KEYCLOAK_PASSWORD_REQUEST_PARAMETER = "password";
    private final String KEYCLOAK_USERNAME_REQUEST_PARAMETER = "username";
    private final String KEYCLOAK_REALM_REQUEST_PARAMETER = "realm";
    private final String KEYCLOAK_CLIENT_ID_REQUEST_PARAMETER = "client_id";
    private final String KEYCLOAK_CLIENT_SECRET_REQUEST_PARAMETER = "client_secret";
    private final String KEYCLOAK_RESOURCE_REQUEST_PARAMETER = "resource";
    private final String KEYCLOAK_CREDENTIALS_REQUEST_PARAMETER = "credentials";
    private final String KEYCLOAK_SECRET_REQUEST_PARAMETER = "secret";

    /**
     * Get the access token (signed JWT) for the test default user of this application.
     *
     * @return The JWT as string.
     * @throws IOException
     */

    public String getKeyCloakAccessTokenForDefaultUser() throws IOException {

        JsonObject keyCloakConfig = getConfigFile();
        ArrayList<NameValuePair> postParameters = createAccessTokenRequestParameters(keyCloakConfig);

        HttpResponse httpResponse = sendAccessTokenRequest(postParameters);

        String responseJSON = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8.name());
        JsonObject keyCloakResponse = Json.createReader(new StringReader(responseJSON)).readObject();

        return keyCloakResponse.getString(KEYCLOAK_ACCESS_TOKEN_PROPERTY);
    }

    /**
     * Get the KeyCloak config file from the test/resources dir.
     *
     * @return JsonObject holding the KeyCloak server config.
     * @throws IOException
     */

    private JsonObject getConfigFile() throws IOException {

        String jsonString;

        try (InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream(KEYCLOAK_CONFIG_FILE)) {
            jsonString = IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8.name());
        }

        return Json.createReader(new StringReader(jsonString)).readObject();
    }

    /**
     * Create a list holding key value pairs with parameters needed for the Keycloak access token HTTP request.
     *
     * @param keyCloakConfig The Keycloak server config file.
     * @return A list holding key value pairs with parameters.
     */

    private ArrayList<NameValuePair> createAccessTokenRequestParameters(final JsonObject keyCloakConfig) {

        ArrayList<NameValuePair> postParameters = new ArrayList<>();

        postParameters.add(new BasicNameValuePair(KEYCLOAK_GRANT_TYPE_REQUEST_PARAMETER,
                KEYCLOAK_PASSWORD_REQUEST_PARAMETER));
        postParameters.add(new BasicNameValuePair(KEYCLOAK_USERNAME_REQUEST_PARAMETER,
                TestUser.getDefaultUsername()));
        postParameters.add(new BasicNameValuePair(KEYCLOAK_PASSWORD_REQUEST_PARAMETER,
                TestUser.getDefaultUserPassword()));
        postParameters.add(new BasicNameValuePair(KEYCLOAK_REALM_REQUEST_PARAMETER,
                keyCloakConfig.getString(KEYCLOAK_REALM_REQUEST_PARAMETER)));
        postParameters.add(new BasicNameValuePair(KEYCLOAK_CLIENT_ID_REQUEST_PARAMETER,
                keyCloakConfig.getString(KEYCLOAK_RESOURCE_REQUEST_PARAMETER)));
        postParameters.add(new BasicNameValuePair(KEYCLOAK_CLIENT_SECRET_REQUEST_PARAMETER,
                keyCloakConfig.getJsonObject(KEYCLOAK_CREDENTIALS_REQUEST_PARAMETER)
                        .getString(KEYCLOAK_SECRET_REQUEST_PARAMETER)));

        return postParameters;
    }

    /**
     * Send the Keycloak access token HTTP request.
     *
     * @param postParameters List holding the request parameters.
     * @return The HTTP response from the Keycloak server.
     * @throws IOException
     */

    private HttpResponse sendAccessTokenRequest(final ArrayList<NameValuePair> postParameters) throws IOException {

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(IntegrationTestUtil.getKeyCloakAccessTokenURL());

        httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

        return httpClient.execute(httpPost);
    }
}
