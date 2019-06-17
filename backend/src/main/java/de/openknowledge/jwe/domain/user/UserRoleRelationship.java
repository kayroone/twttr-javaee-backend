package de.openknowledge.jwe.domain.user;

import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.Size;

import static org.apache.commons.lang3.Validate.notNull;


/**
 * Entity representing the relationship between a {@link User} and it's permission roles.
 */

@Entity
@Table(name = "TAB_USER_ROLE_RELATIONSHIP")
public class UserRoleRelationship extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ROLE_RELATIONSHIP_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Size(max = 20)
    @Column(name = "USER_ROLE")
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
