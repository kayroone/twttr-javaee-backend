package de.jwiegmann.twttr.domain.user;

import de.jwiegmann.twttr.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import static org.apache.commons.lang3.Validate.notNull;

/** Entity representing the relationship between a {@link User} and it's permission roles. */
@Entity
@Table(name = "tab_user_role_relationship")
public class UserRoleRelationship extends AbstractEntity<Long> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_role_relationship_id")
  private long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Size(max = 20)
  @Column(name = "user_role")
  private String role;

  @Override
  public Long getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(final User user) {
    notNull(user, "user must not be null");
    this.user = user;
  }

  public String getRole() {
    return role;
  }

  public void setRole(final String role) {
    notNull(role, "role must not be null");
    this.role = role;
  }
}
