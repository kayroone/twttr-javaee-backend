import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from '@angular/router';
import {KeycloakAuthGuard, KeycloakService} from 'keycloak-angular';

/**
 * Guard to protect secured URIs.
 */

@Injectable()
export class KeyCloakAuthGuard extends KeycloakAuthGuard {

  constructor(protected router: Router, protected keycloakService: KeycloakService) {
    super(router, keycloakService);
  }

  /**
   * Checks if the JWT contains the path roles. If no JWT is present, the user will be
   * redirected to the login page.
   *
   * @param route The route that will be checked.
   * @param state Snapshot of the actual router state.
   */

  isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {

    return new Promise((resolve, reject) => {

      if (!this.authenticated) {
        this.keycloakService.login();
        return;
      }

      const requiredRoles = route.data.roles;

      if (!requiredRoles || requiredRoles.length === 0) {
        return resolve(true);
      } else {

        if (!this.roles || this.roles.length === 0) {
          resolve(false);
        }

        let granted = false;

        for (const requiredRole of requiredRoles) {
          if (this.roles.indexOf(requiredRole) > -1) {
            granted = true;
            break;
          }
        }

        resolve(granted);
      }
    });
  }
}
