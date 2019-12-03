/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.jwiegmann.twttr.infrastructure.microprofiles.health;

import de.jwiegmann.twttr.IntegrationTestContainers;
import de.jwiegmann.twttr.infrastructure.constants.Constants;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/** Integration test for the MP-Health. */
public class DatasourceHealthCheckIT {

  private static IntegrationTestContainers integrationTestContainers;
  private static String apiUri;

  @BeforeAll
  public static void init() {

    integrationTestContainers =
        IntegrationTestContainers.newTestEnvironment().withApiContainer().init();

    apiUri = integrationTestContainers.getHostUri(Constants.HEALTH_API_URI);
  }

  @AfterAll
  public static void teardown() {

    integrationTestContainers.teardownContainers();
  }

  @Test
  public void checkHealth() {
    RestAssured.given()
        .accept(MediaType.APPLICATION_JSON)
        .when()
        .get(apiUri)
        .then()
        .contentType(MediaType.APPLICATION_JSON)
        .statusCode(Response.Status.OK.getStatusCode())
        .body("status", Matchers.equalTo("UP"))
        .rootPath("checks.find{ it.name == 'datasource' }")
        .body("status", Matchers.equalTo("UP"))
        .body("data.driverName", Matchers.notNullValue())
        .body("data.driverVersion", Matchers.notNullValue())
        .body("data.databaseProductName", Matchers.notNullValue())
        .body("data.databaseProductVersion", Matchers.notNullValue());
  }
}
