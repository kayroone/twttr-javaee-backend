package de.jwiegmann.twttr.infrastructure.security;

import de.jwiegmann.twttr.domain.user.TestUser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

/** Class providing functionality to fetch resources from the KeyCloak server. */
public class KeyCloakResourceLoader {

  private static final Logger LOG = LoggerFactory.getLogger(KeyCloakResourceLoader.class);

  private static final String KEYCLOAK_CONFIG_FILE = "src/test/resources/keycloak.json";
  private static final String KEYCLOAK_TOKEN_URI = "realms/public/protocol/openid-connect/token";

  private static final String KEYCLOAK_ACCESS_TOKEN_PROPERTY = "access_token";
  private static final String KEYCLOAK_GRANT_TYPE_REQUEST_PARAMETER = "grant_type";
  private static final String KEYCLOAK_PASSWORD_REQUEST_PARAMETER = "password";
  private static final String KEYCLOAK_USERNAME_REQUEST_PARAMETER = "username";
  private static final String KEYCLOAK_REALM_REQUEST_PARAMETER = "realm";
  private static final String KEYCLOAK_CLIENT_ID_REQUEST_PARAMETER = "client_id";
  private static final String KEYCLOAK_RESOURCE_REQUEST_PARAMETER = "resource";

  /**
   * Get the access token (signed JWT) for the test default user of this application.
   *
   * @return The JWT as string.
   * @throws IOException
   */
  public static String getKeyCloakAccessToken(String keycloakHost) throws IOException {

    JsonObject keyCloakConfig = getConfigFile();
    ArrayList<NameValuePair> postParameters = createAccessTokenRequestParameters(keyCloakConfig);

    String keycloakUrl = keycloakHost + KEYCLOAK_TOKEN_URI;

    HttpResponse httpResponse = sendAccessTokenRequest(keycloakUrl, postParameters);

    String responseJSON =
        EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8.name());
    JsonObject keyCloakResponse = Json.createReader(new StringReader(responseJSON)).readObject();

    if (keyCloakResponse.containsKey("error")) {
      LOG.warn("Failed to fetch keycloak access token: " + keyCloakResponse.getString("error_description"));
    }

    return keyCloakResponse.getString(KEYCLOAK_ACCESS_TOKEN_PROPERTY);
  }

  /**
   * Get the KeyCloak config file from the test/resources dir.
   *
   * @return JsonObject holding the KeyCloak server config.
   * @throws IOException
   */
  private static JsonObject getConfigFile() throws IOException {

    String jsonString;

    try (InputStream inputStream = FileUtils.openInputStream(new File(KEYCLOAK_CONFIG_FILE))) {
      jsonString =
          IOUtils.toString(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8.name());
    }

    return Json.createReader(new StringReader(jsonString)).readObject();
  }

  /**
   * Create a list holding key value pairs with parameters needed for the Keycloak access token HTTP
   * request.
   *
   * @param keyCloakConfig The Keycloak server config file.
   * @return A list holding key value pairs with parameters.
   */
  private static ArrayList<NameValuePair> createAccessTokenRequestParameters(
      final JsonObject keyCloakConfig) {

    ArrayList<NameValuePair> postParameters = new ArrayList<>();

    postParameters.add(
        new BasicNameValuePair(
            KEYCLOAK_GRANT_TYPE_REQUEST_PARAMETER, KEYCLOAK_PASSWORD_REQUEST_PARAMETER));
    postParameters.add(
        new BasicNameValuePair(KEYCLOAK_USERNAME_REQUEST_PARAMETER, TestUser.getDefaultUsername()));
    postParameters.add(
        new BasicNameValuePair(
            KEYCLOAK_PASSWORD_REQUEST_PARAMETER, TestUser.getDefaultUserPassword()));
    postParameters.add(
        new BasicNameValuePair(
            KEYCLOAK_REALM_REQUEST_PARAMETER,
            keyCloakConfig.getString(KEYCLOAK_REALM_REQUEST_PARAMETER)));
    postParameters.add(
        new BasicNameValuePair(
            KEYCLOAK_CLIENT_ID_REQUEST_PARAMETER,
            keyCloakConfig.getString(KEYCLOAK_RESOURCE_REQUEST_PARAMETER)));

    return postParameters;
  }

  /**
   * Send the Keycloak access token HTTP request.
   *
   * @param postParameters List holding the request parameters.
   * @return The HTTP response from the Keycloak server.
   * @throws IOException
   */
  private static HttpResponse sendAccessTokenRequest(
      final String keycloakUrl, final ArrayList<NameValuePair> postParameters) throws IOException {

    HttpClient httpClient = HttpClientBuilder.create().build();
    HttpPost httpPost = new HttpPost(keycloakUrl);

    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));

    return httpClient.execute(httpPost);
  }
}
