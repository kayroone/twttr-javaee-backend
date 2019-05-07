/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.1.0-SNAPSHOT (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.1.0-SNAPSHOT
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.jwe;

import javax.ws.rs.core.UriBuilder;

/**
 * Util class for integration tests.
 */
public final class IntegrationTestUtil {

  private IntegrationTestUtil() {
    super();
  }

  public static String getBaseURI() {
    String uri = "http://{host}:{port}/{context-root}";
    return UriBuilder.fromUri(uri)
        .resolveTemplate("host", getHost())
        .resolveTemplate("port", getPort())
        .resolveTemplate("context-root", getContextRoot())
        .toTemplate();
  }

  public static String getHealthCheckURI() {
    String uri = "http://{host}:{port}/health";
    return UriBuilder.fromUri(uri)
        .resolveTemplate("host", getHost())
        .resolveTemplate("port", getPort())
        .toTemplate();
  }

  public static String getMetricsURI() {
    String uri = "http://{host}:{port}/metrics";
    return UriBuilder.fromUri(uri)
        .resolveTemplate("host", getHost())
        .resolveTemplate("port", getPort())
        .toTemplate();
  }

  public static String getOpenApiURI() {
    String uri = "http://{host}:{port}/openapi";
    return UriBuilder.fromUri(uri)
        .resolveTemplate("host", getHost())
        .resolveTemplate("port", getPort())
        .toTemplate();
  }

  private static String getHost() {
    return System.getProperty("twttr.test.host", "localhost");
  }

  private static int getPort() {
    int port = Integer.parseInt(System.getProperty("twttr.test.port", "81.0-SNAPSHOT81.0-SNAPSHOT"));
    int offset = Integer.parseInt(System.getProperty("twttr.test.port-offset", "1.0-SNAPSHOT"));
    return port + offset;
  }

  private static String getContextRoot() {
    return System.getProperty("twttr.test.context-path", "twttr-2.3.1.0-SNAPSHOT-archetype");
  }
}
