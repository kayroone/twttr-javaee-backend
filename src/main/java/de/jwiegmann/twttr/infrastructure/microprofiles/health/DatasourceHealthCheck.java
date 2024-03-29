package de.jwiegmann.twttr.infrastructure.microprofiles.health;

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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/** Health check for the datasource. */
@ApplicationScoped
@Liveness
public class DatasourceHealthCheck implements HealthCheck {

  private static final Logger LOG = LoggerFactory.getLogger(DatasourceHealthCheck.class);

  @Inject private DataSource datasource;

  @Inject
  @ConfigProperty(name = "health.datasource.timeout")
  private int timeout;

  @Override
  public HealthCheckResponse call() {
    HealthCheckResponseBuilder builder = HealthCheckResponse.named("datasource");

    try {
      Connection connection = datasource.getConnection();
      DatabaseMetaData metaData = connection.getMetaData();

      boolean valid = connection.isValid(timeout);

      return builder
          .withData("databaseProductName", metaData.getDatabaseProductName())
          .withData("databaseProductVersion", metaData.getDatabaseProductVersion())
          .withData("driverName", metaData.getDriverName())
          .withData("driverVersion", metaData.getDriverVersion())
          .withData("valid", valid)
          .state(valid)
          .build();
    } catch (SQLException e) {
      LOG.error(e.getMessage(), e);
      return builder.down().build();
    }
  }
}
