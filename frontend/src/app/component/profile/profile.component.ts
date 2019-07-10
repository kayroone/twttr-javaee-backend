import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {KeycloakService} from 'keycloak-angular';
import {Location} from '@angular/common';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})

/**
 * Component representing the user profile.
 */

export class ProfileComponent implements OnInit {

  constructor(private route: ActivatedRoute, private keycloakService: KeycloakService,
              private location: Location) {
  }

  ngOnInit() {
    this.location.go('/profile/' + this.keycloakService.getUsername());
  }
}
