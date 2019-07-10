import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {ProfileComponent} from './component/profile/profile.component';
import {KeyCloakAuthGuard} from './guard/keycloak/keycloak.auth-guard';

const routes: Routes = [
  {
    path: 'profile/:username',
    component: ProfileComponent,
    canActivate: [KeyCloakAuthGuard],
    children: [],
    data: {
      roles: ['USER', 'MODERATOR']
    }
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {
}
