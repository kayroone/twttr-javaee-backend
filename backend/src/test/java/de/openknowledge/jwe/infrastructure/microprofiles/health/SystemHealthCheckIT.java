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
package de.openknowledge.jwe.infrastructure.microprofiles.health;

import de.openknowledge.jwe.AbstractContainer;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Integration test for the MP-Health {@link SystemHealthCheck}.
 */

public class SystemHealthCheckIT extends AbstractContainer {

    private static String apiUrl;

    @BeforeAll
    public static void setUp() {

        apiUrl = getApiUrl("health");
    }

    @Test
    public void checkHealth() {

        RestAssured.given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get(apiUrl)
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.OK.getStatusCode())
                .body("outcome", Matchers.equalTo("UP"))
                .rootPath("checks.find{ it.name == 'system' }")
                .body("state", Matchers.equalTo("UP"))
                .body("data.arch", Matchers.notNullValue())
                .body("data.name", Matchers.notNullValue())
                .body("data.loadAverage", Matchers.notNullValue())
                .body("data.'loadAverage per processor'", Matchers.notNullValue())
                .body("data.'memory free'", Matchers.notNullValue())
                .body("data.'memory total'", Matchers.notNullValue())
                .body("data.processors", Matchers.notNullValue())
                .body("data.version", Matchers.notNullValue());
    }
}
