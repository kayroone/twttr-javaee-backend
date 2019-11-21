package de.jwiegmann.twttr.domain.user;

import de.jwiegmann.twttr.infrastructure.domain.repository.AbstractRepository;
import de.jwiegmann.twttr.infrastructure.domain.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/** Repository to access {@link User} entities. */
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

  private List<User> findPartial(final int offset, final int limit) {

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

  public User findByUsername(final String username) {

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

  public User getReferenceByUsername(final String username) {

    LOG.debug("Searching for User {}", username);

    User user = findByUsername(username);
    User reference = getReference(user.getId());

    LOG.debug("Located User {}", reference);

    return reference;
  }

  public List<User> search(final String keyword, final int firstResult, final int maxResults) {

    LOG.debug("Locating Users with search {}", keyword);

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> cq = cb.createQuery(User.class);

    Root<User> from = cq.from(User.class);

    Predicate predicateUserName =
        cb.like(cb.lower(from.get("username")), "%" + keyword.toLowerCase() + "%");
    cq.select(from).where(cb.or(predicateUserName));

    TypedQuery<User> query = entityManager.createQuery(cq);

    if (firstResult != -1) {
      query.setFirstResult(firstResult);
    }
    if (maxResults != -1) {
      query.setMaxResults(maxResults);
    }

    List<User> users = query.getResultList();

    LOG.debug("Found Users {}", users);

    return Collections.unmodifiableList(users);
  }
}
