import {KeycloakService} from 'keycloak-angular';
import {environment} from '../../../environments/environment';

/**
 * Initialize the Keycloak service.
 *
 * @param keycloak The - not yet initialized - Keycloak service.
 */

export function initializer(keycloak: KeycloakService): () => Promise<any> {

  return (): Promise<any> => {
    return new Promise(async (resolve, reject) => {

      try {
        await keycloak.init({
          config: environment.config.keycloak,
          initOptions: {
            onLoad: 'login-required',
            checkLoginIframe: false,
            responseMode: 'fragment',
            flow: 'standard'
          },
          enableBearerInterceptor: true,
          bearerExcludedUrls: ['/assets', '/clients/public']
        });

        resolve();

      } catch (error) {
        reject(error);
      }
    });
  };
}
