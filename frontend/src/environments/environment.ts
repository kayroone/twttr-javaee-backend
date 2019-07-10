const keycloakConfig = {
  url: 'http://localhost:8080/auth',
  realm: 'twttr',
  clientId: 'twttr-spa'
};

const api = {
  users: 'http://localhost:8081/twttr-service/api/users',
  tweets: 'http://localhost:8081/twttr-service/api/tweets',
};

export const environment = {
  production: false,
  config: {
    keycloak: keycloakConfig,
    frontend: api
  }
};
