import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest} from '@angular/common/http';

import {KeycloakService} from 'keycloak-angular';
import {from, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

/**
 * HTTP interceptor to add JWT to each request header.
 */

@Injectable()
export class KeyCloakTokenInterceptor implements HttpInterceptor {

  constructor(protected keycloak: KeycloakService) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const keyCloakToken: any = from(this.keycloak.getToken());

    return keyCloakToken.pipe(
      map(token => {
        const headers: HttpHeaders = req.headers;
        const headersWithAuthorization: HttpHeaders = headers.append('Authorization', 'Bearer ' + token);
        const requestWithAuthorizationHeader = req.clone({headers: headersWithAuthorization});
        return next.handle(requestWithAuthorizationHeader);
      }));
  }
}
