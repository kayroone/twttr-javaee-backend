package de.openknowledge.jwe.domain.service;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.domain.repository.UserRepository;
import de.openknowledge.jwe.infrastructure.domain.entity.EntityNotFoundException;
import org.wildfly.common.annotation.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * Service that provides {@link User} operations.
 */

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    /**
     * Find all existing {@link User}.
     *
     * @return A list with all existing {@link User}.
     */

    public List<User> findAll() {

        return userRepository.findAll();
    }

    /**
     * Find a single {@link User} by it's Id.
     *
     * @return The single {@link User}.
     */

    public User findById(@NotNull Long id) throws EntityNotFoundException {

        return userRepository.find(id);
    }

    /**
     * Create a new {@link User}.
     *
     * @param user
     * @return The created {@link User}.
     */

    public User create(@NotNull User user) {

        return userRepository.create(user);
    }

    /**
     * Update an existing {@link User}.
     *
     * @param user
     * @return The updated {@link User}.
     */

    public User update(@NotNull User user) {

        return userRepository.update(user);
    }

    /**
     * Delete an existing {@link User}.
     *
     * @param user
     */

    public void delete(@NotNull User user) {

        userRepository.delete(user);
    }
}
