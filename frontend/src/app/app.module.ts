import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

import {AuthKeycloakInitializer} from './service/auth/auth.keycloak.initializer';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app.routing.module';
import {HeaderComponent} from './component/header/header.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    KeycloakAngularModule,
    NgbModule
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: AuthKeycloakInitializer,
      multi: true,
      deps: [KeycloakService]
    }
  ],
  bootstrap: [AppComponent]
})

export class AppModule {}
