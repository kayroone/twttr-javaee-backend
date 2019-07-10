import {APP_INITIALIZER, NgModule} from '@angular/core';

import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {BrowserModule} from '@angular/platform-browser';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app.routing.module';
import {KeyCloakAuthGuard} from './guard/keycloak/keycloak.auth-guard';

import {KeycloakService} from 'keycloak-angular';
import {initializer} from './guard/keycloak/keycloak.initializer';
import {KeyCloakTokenInterceptor} from './guard/keycloak/keycloak.token-interceptor';

import {ProfileComponent} from './component/profile/profile.component';
import {HeaderComponent} from './component/header/header.component';
import {ContentComponent} from './component/content/content.component';
import {FooterComponent} from './component/footer/footer.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ContentComponent,
    FooterComponent,
    ProfileComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    KeyCloakAuthGuard,
    KeycloakService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
      deps: [KeycloakService]
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: KeyCloakTokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})

export class AppModule {

}
