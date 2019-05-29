package de.openknowledge.jwe.infrastructure.security;

import de.openknowledge.jwe.IntegrationTestUtil;
import de.openknowledge.jwe.domain.model.user.TestUser;
import de.openknowledge.jwe.domain.model.user.User;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Class providing functionality to fetch resources from the KeyCloak server.
 */

public class KeyCloakResourceLoader {

    /**
     * Get the access token (signed JWT) for the test default user of this application.
     *
     * @return The JWT as string.
     * @throws IOException
     */

    public String getKeyCloakAccessToken() throws IOException {

        JSONObject keyCloakConfig = getKeyCloakConfigFile();
        User testUser = TestUser.newDefaultUser();

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(IntegrationTestUtil.getKeyCloakAccessTokenURL());

        ArrayList<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("grant_type", "password"));
        postParameters.add(new BasicNameValuePair("username", testUser.getUsername()));
        postParameters.add(new BasicNameValuePair("password", TestUser.getDefaultUserPassword()));
        postParameters.add(new BasicNameValuePair("realm", keyCloakConfig.getString("realm")));
        postParameters.add(new BasicNameValuePair("client_id", keyCloakConfig.getString("resource")));
        postParameters.add(new BasicNameValuePair("client_secret", keyCloakConfig
                .getJSONObject("credentials").getString("secret")));

        httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

        HttpResponse httpResponse = httpClient.execute(httpPost);
        String responseJSON = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8.name());

        JSONObject keyCloakResponse = new JSONObject(responseJSON);
        return keyCloakResponse.getString("access_token");
    }

    /**
     * Get the KeyCloak config file from the test/resources dir.
     *
     * @return JSONObject holding the KeyCloak server config.
     * @throws IOException
     */

    private JSONObject getKeyCloakConfigFile() throws IOException {

        String jsonString;

        try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("keycloak.json")) {
            jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        }

        return new JSONObject(jsonString);
    }
}
