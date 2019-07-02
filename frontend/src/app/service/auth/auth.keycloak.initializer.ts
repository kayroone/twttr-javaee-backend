import {KeycloakService} from 'keycloak-angular';

export function AuthKeycloakInitializer(keycloak: KeycloakService): () => Promise<any> {
  return (): Promise<any> => keycloak.init();
}
