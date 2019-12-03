package de.jwiegmann.twttr;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unchecked")
public abstract class AbstractTestContainers {

  private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestContainers.class);

  private static final String APPLICATION_NAME = "twttr";

  private static final int DB_TCP_PORT = 5432;
  private static final int API_WEB_PORT = 8081;

  private static final String AUTH_IMAGE = "jboss/keycloak:latest";
  private static final String API_IMAGE = "registry.hub.docker.com/kayroone/twttr-api:latest";

  static final int AUTH_WEB_PORT = 8080;
  static final String AUTH_HOST_NAME = "keycloak.service";

  private Network network;
  private PostgreSQLContainer postgres;

  GenericContainer keycloak;
  GenericContainer api;

  private DataSource dataSource;
  private final Set<HikariDataSource> dataSourcesForCleanup = new HashSet<>();

  public AbstractTestContainers() {

    this.network = Network.newNetwork();
  }

  public ResultSet performQuery(final String sqlStatement) {

    try (Connection dbConnection = this.dataSource.getConnection()) {

      Statement statement = dbConnection.createStatement();
      boolean withResultSet = statement.execute(sqlStatement);

      if (withResultSet) {

        ResultSet resultSet = statement.getResultSet();
        resultSet.next();

        return resultSet;
      }

    } catch (SQLException e) {
      LOG.error("Failed to execute statement: " + sqlStatement);
    }

    return null;
  }

  public void teardownDataSources() {

    if (!this.dataSourcesForCleanup.isEmpty()) {
      this.dataSourcesForCleanup.forEach(HikariDataSource::close);
    }
  }

  void initApiContainer() {

    initDatabaseContainer();

    this.api =
        new GenericContainer(API_IMAGE)
            .withExposedPorts(API_WEB_PORT)
            .waitingFor(Wait.forHttp("/service/hello"));

    /* This container must be reachable from host system for testing authentication with keycloak */
    List<String> portBinding = new ArrayList<>();
    String binding = String.format("%d:%d", API_WEB_PORT, API_WEB_PORT);

    portBinding.add(binding);
    this.api.setPortBindings(portBinding);

    /* Network configuration */
    this.api.withNetwork(this.network).withNetworkAliases("twttr.service");

    this.api.start();
  }

  /**
   * This method performs the following tasks:
   *
   * 1. Init keycloak container based on latest keycloak docker image.
   * 2. Add database configuration to the container.
   * 3. Add a custom keycloak realm configuration.
   * 4. Add a custom shell script to add a custom user to keycloak for testing.
   * 5. Start the container with the custom realm config and execute the user create shell script.
   */
  void initAuthContainer() {

    this.keycloak =
        new GenericContainer(AUTH_IMAGE)
            .withExposedPorts(AUTH_WEB_PORT)
            .withEnv("DB_VENDOR", "h2")
            .withEnv("KEYCLOAK_USER", "admin")
            .withEnv("KEYCLOAK_PASSWORD", "admin")
            .withEnv("KEYCLOAK_IMPORT", "/tmp/realm.json")
            .withClasspathResourceMapping(
                "keycloak-realm-export.json", "/tmp/realm.json", BindMode.READ_ONLY)
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("keycloak-create-user.sh", 700),
                "/opt/jboss/keycloak-create-user.sh")
            .waitingFor(Wait.forHttp("/auth"));

    /* This container must be reachable from host system for oauth lifecycle with api container */
    List<String> portBinding = new ArrayList<>();
    String binding = String.format("%d:%d", AUTH_WEB_PORT, AUTH_WEB_PORT);

    portBinding.add(binding);
    this.keycloak.setPortBindings(portBinding);

    /* Network configuration */
    this.keycloak.withNetwork(this.network).withNetworkAliases("keycloak.service");

    /* Executing create user script only possible in running container */
    this.keycloak.start();

    /* Create keycloak test user */
    try {
      Container.ExecResult commandResult =
          this.keycloak.execInContainer("sh", "/opt/jboss/keycloak-create-user.sh");

      if (commandResult.getExitCode() == 0) {
        LOG.info("Created new keycloak test user with name: jw");
      } else {
        LOG.warn(
            "Creating new keycloak test user failed with exit code: "
                + commandResult.getExitCode());
      }

    } catch (IOException | InterruptedException e) {
      LOG.error("Failed to execute keycloak-create-user.sh: " + e.getMessage());
    }
  }

  public void teardownContainers() {

    if (null != this.keycloak && this.keycloak.isRunning()) {
      LOG.info("Stopping keycloak container.");
      this.keycloak.stop();
    }
    if (null != this.api && this.api.isRunning()) {
      LOG.info("Stopping twttr container.");
      this.api.stop();
    }
    if (null != this.postgres && this.postgres.isRunning()) {
      LOG.info("Stopping postgres container.");
      this.postgres.stop();
    }
    this.network.close();
  }

  private void initDatabaseContainer() {

    this.postgres =
        new PostgreSQLContainer("postgres:latest")
            .withDatabaseName(APPLICATION_NAME)
            .withUsername(APPLICATION_NAME)
            .withPassword(APPLICATION_NAME);

    this.postgres.withExposedPorts(DB_TCP_PORT);

    /* This container needs a fixed port binding because db must be reachable from api container */
    List<String> portBinding = new ArrayList<>();
    String binding = String.format("%d:%d", DB_TCP_PORT, DB_TCP_PORT);

    portBinding.add(binding);
    this.postgres.setPortBindings(portBinding);

    /* Network configuration */
    this.postgres.withNetwork(this.network).withNetworkAliases("twttr.database");

    this.postgres.start();

    /* Init datasource so we can execute SQL statements on the DB container */
    initDataSource();
  }

  private void initDataSource() {

    HikariConfig hikariConfig = new HikariConfig();

    hikariConfig.setJdbcUrl(this.postgres.getJdbcUrl());
    hikariConfig.setUsername(this.postgres.getUsername());
    hikariConfig.setPassword(this.postgres.getPassword());
    hikariConfig.setDriverClassName(this.postgres.getDriverClassName());

    final HikariDataSource dataSource = new HikariDataSource(hikariConfig);

    this.dataSourcesForCleanup.add(dataSource);
    this.dataSource = dataSource;
  }
}
