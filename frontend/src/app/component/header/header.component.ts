import {Component, OnInit} from '@angular/core';
import {KeycloakService} from 'keycloak-angular';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})

/**
 * Component representing the header of the user profile.
 */

export class HeaderComponent implements OnInit {

  constructor(private keycloakService: KeycloakService) {
  }

  ngOnInit() {
  }

  logout() {
    this.keycloakService.logout();
  }

}
