import {KeycloakConfig} from 'keycloak-angular';

const keycloakConfig: KeycloakConfig = {
  url: 'http://localhost:8080/auth',
  realm: 'twttr',
  clientId: 'twttr-service',
  credentials: {
    secret: '1f4cebfb-c1bc-43bb-87f1-a6ebda78f4ae'
  }
};

export const environment = {
  production: false,
  keycloak: keycloakConfig,
};
