import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {User} from '../../model/user';
import {environment} from '../../../environments/environment';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})

/**
 * Service to fetch user data.
 */

export class UserService {

  constructor(private http: HttpClient) {
  }

  public getByUsername(username: string): Observable<User> {

    const params = new HttpParams().set('username', username);
    return this.http.get<User>(environment.config.frontend.users, {params});
  }
}
