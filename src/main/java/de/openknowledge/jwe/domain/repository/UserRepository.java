package de.openknowledge.jwe.domain.repository;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.infrastructure.domain.repository.AbstractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wildfly.common.annotation.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

/**
 * Repository to access {@link User} entities.
 */

@ApplicationScoped
public class UserRepository extends AbstractRepository<User> implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(UserRepository.class);

    public UserRepository() {
        super();
    }

    @Inject
    public UserRepository(final EntityManager entityManager) {
        super(entityManager);
    }

    public List<User> findAll() {

        LOG.debug("Searching for Users");

        CriteriaQuery<User> criteriaQuery = getDefaultQuery();
        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);

        List<User> results = query.getResultList();

        LOG.debug("Located {} Users", results.size());

        return results;
    }

    public List<User> findPartial(@NotNull int offset, @NotNull int limit) {

        LOG.debug("Searching for Users");

        CriteriaQuery<User> criteriaQuery = getDefaultQuery();
        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);

        // Set limitations:
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<User> results = query.getResultList();

        LOG.debug("Located {} Users", results.size());

        return results;
    }

    public User findByUsername(@NotNull String username) {

        LOG.debug("Searching for User {}", username);

        CriteriaQuery<User> criteriaQuery = getFindByUsernameQuery(username);
        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);

        User result = query.getSingleResult();

        LOG.debug("Located User {}", result);

        return result;
    }

    // INTERNAL HELPER ------------------------------------------------------------------------------------------------

    private CriteriaQuery<User> getDefaultQuery() {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        // Only select table:
        Root<User> from = criteriaQuery.from(User.class);
        criteriaQuery.select(from);

        return criteriaQuery;
    }

    private CriteriaQuery<User> getFindByUsernameQuery(@NotNull String username) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> from = criteriaQuery.from(User.class);

        // Order by date:
        criteriaQuery.select(from);
        criteriaQuery.where(criteriaBuilder.equal(from.get("username"), username));

        return criteriaQuery;
    }
}