import {RouterModule, Routes} from '@angular/router';
import {NgModule} from '@angular/core';
import {LoginComponent} from './component/login/login.component';
import {ProfileComponent} from './component/profile/profile.component';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'user/:id', component: ProfileComponent, canActivate: ['AppAuthGuard']}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule {
}
