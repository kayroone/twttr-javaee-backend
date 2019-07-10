import {Serializable} from '../util/Serializable';

/**
 * User representing a user.
 */

export class User extends Serializable {

  id: number;
  username: string;

  getId(): number { return this.id; }
  getUsername(): string { return this.username; }
}
