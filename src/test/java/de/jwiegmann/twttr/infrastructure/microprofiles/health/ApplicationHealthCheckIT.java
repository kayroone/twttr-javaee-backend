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

import de.jwiegmann.twttr.TestContainers;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Integration test for the MP-Health.
 */

@Testcontainers
class ApplicationHealthCheckIT {

    @Container private static GenericContainer dbContainer = TestContainers.initDatabaseContainer();
    @Container private static GenericContainer authContainer = TestContainers.initAuthContainer();
    @Container private static GenericContainer apiContainer = TestContainers.initApiContainer();

    private static String uri;

    @BeforeAll
    public static void setUp() {

        uri = getApplicationUri();
    }

    @Test
    public void checkHealth() {
        RestAssured.given()
                .accept(MediaType.APPLICATION_JSON)
                .when()
                .get(uri)
                .then()
                .contentType(MediaType.APPLICATION_JSON)
                .statusCode(Response.Status.OK.getStatusCode())
                .body("outcome", Matchers.equalTo("UP"))
                .rootPath("checks.find{ it.name == 'application' }")
                .body("state", Matchers.equalTo("UP"))
                .body("data.buildVersion", Matchers.notNullValue())
                .body("data.buildTimestamp", Matchers.notNullValue())
                .body("data.name", Matchers.equalTo("base"));
    }

    private static String getApplicationUri() {

        String uri = "http://{host}:{port}/{path}";
        return UriBuilder.fromUri(uri)
                .resolveTemplate("host", apiContainer.getContainerIpAddress())
                .resolveTemplate("port", apiContainer.getFirstMappedPort())
                .resolveTemplate("path", "health")
                .toTemplate();
    }
}
