package de.jwiegmann.twttr.application;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class TestDatabaseAdapter {

  private final PostgreSQLContainer database;

  public TestDatabaseAdapter(final PostgreSQLContainer database) {
    this.database = database;
  }

}
