package de.openknowledge.jwe.domain.repository;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
import de.openknowledge.jwe.infrastructure.domain.repository.AbstractRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Repository to access {@link Tweet} entities.
 */

@ApplicationScoped
public class TweetRepository extends AbstractRepository<Tweet> implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(TweetRepository.class);

    public TweetRepository() {
        super();
    }

    @Inject
    public TweetRepository(final EntityManager entityManager) {
        super(entityManager);
    }

    public List<Tweet> findAll() {

        LOG.debug("Searching for Tweets");

        CriteriaQuery<Tweet> criteriaQuery = getOrderByDateQuery();
        TypedQuery<Tweet> query = entityManager.createQuery(criteriaQuery);

        List<Tweet> results = query.getResultList();

        LOG.debug("Located {} Tweets", results.size());

        return results;
    }

    public List<Tweet> findPartial(int offset, int limit) {

        LOG.debug("Searching for Tweets with offset " + offset + " and limit " + limit);

        CriteriaQuery<Tweet> criteriaQuery = getDefaultQuery();
        TypedQuery<Tweet> query = entityManager.createQuery(criteriaQuery);

        // Set limitations:
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Tweet> results = query.getResultList();

        LOG.debug("Located {} Tweets", results.size());

        return results;
    }

    public List<Tweet> findPartialOrderByDateTime(int offset, int limit) {

        LOG.debug("Searching for Tweets with offset " + offset + " and limit " + limit);

        CriteriaQuery<Tweet> criteriaQuery = getOrderByDateQuery();
        TypedQuery<Tweet> query = entityManager.createQuery(criteriaQuery);

        // Set limitations:
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Tweet> results = query.getResultList();

        LOG.debug("Located {} Tweets", results.size());

        return results;
    }

    public List<Tweet> findPartialByIdsOrderByDateTime(int offset, int limit, List<Long> ids) {

        LOG.debug("Searching for Tweets with offset " + offset + " and limit " + limit);

        CriteriaQuery<Tweet> criteriaQuery = getFindByIdsOrderByDateQuery(ids);
        TypedQuery<Tweet> query = entityManager.createQuery(criteriaQuery);

        // Set limitations:
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Tweet> results = query.getResultList();

        LOG.debug("Located {} Tweets", results.size());

        return results;
    }

    // INTERNAL HELPER ------------------------------------------------------------------------------------------------

    private CriteriaQuery<Tweet> getDefaultQuery() {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tweet> criteriaQuery = criteriaBuilder.createQuery(Tweet.class);

        // Only select table:
        Root<Tweet> from = criteriaQuery.from(Tweet.class);
        criteriaQuery.select(from);

        return criteriaQuery;
    }

    private CriteriaQuery<Tweet> getOrderByDateQuery() {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tweet> criteriaQuery = criteriaBuilder.createQuery(Tweet.class);

        Root<Tweet> from = criteriaQuery.from(Tweet.class);

        // Order by date:
        criteriaQuery.select(from);
        criteriaQuery.orderBy(criteriaBuilder.desc(from.get("date")));

        return criteriaQuery;
    }

    private CriteriaQuery<Tweet> getFindByIdsOrderByDateQuery(List<Long> ids) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tweet> criteriaQuery = criteriaBuilder.createQuery(Tweet.class);

        Root<Tweet> from = criteriaQuery.from(Tweet.class);

        // Order by date:
        criteriaQuery.select(from);
        criteriaQuery.orderBy(criteriaBuilder.desc(from.get("date")));

        criteriaQuery.where(from.get("id").in(ids));

        return criteriaQuery;
    }
}