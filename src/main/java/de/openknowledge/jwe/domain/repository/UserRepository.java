package de.openknowledge.jwe.domain.repository;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.infrastructure.domain.repository.AbstractRepository;
import de.openknowledge.jwe.infrastructure.domain.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@Repository
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

        return findPartial(0, 100);
    }

    public List<User> findPartial(int offset, int limit) {

        LOG.debug("Searching for Users");

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> from = criteriaQuery.from(User.class);
        criteriaQuery.select(from);

        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<User> results = query.getResultList();

        LOG.debug("Located {} Users", results.size());

        return results;
    }

    public User findByUsername(String username) {

        LOG.debug("Searching for User {}", username);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> from = criteriaQuery.from(User.class);
        criteriaQuery.select(from);
        criteriaQuery.where(criteriaBuilder.equal(from.get("username"), username));

        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
        User result = query.getSingleResult();

        LOG.debug("Located User {}", result);

        return result;
    }

    public List<User> findByKeyword(String keyword) {

        LOG.debug("Searching for User containing the keyword {} in it's username", keyword);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> from = criteriaQuery.from(User.class);
        criteriaQuery.select(from);
        criteriaQuery.where(criteriaBuilder.like(from.get("username"), "%" + keyword + "%"));

        TypedQuery<User> query = entityManager.createQuery(criteriaQuery);
        List<User> result = query.getResultList();

        LOG.debug("Located Users {}", result);

        return result;
    }
}