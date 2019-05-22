package de.openknowledge.jwe.domain.repository;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
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
 * Repository to access {@link Tweet} entities.
 */

@Repository
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

        return findPartial(0, 100);
    }

    public List<Tweet> findPartial(final int offset, final int limit) {

        LOG.debug("Searching for Tweets with offset " + offset + " and limit " + limit);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tweet> criteriaQuery = criteriaBuilder.createQuery(Tweet.class);

        Root<Tweet> from = criteriaQuery.from(Tweet.class);
        criteriaQuery.select(from);

        TypedQuery<Tweet> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Tweet> results = query.getResultList();

        LOG.debug("Located {} Tweets", results.size());

        return results;
    }

    public List<Tweet> findPartialOrderByDate(final int offset, final int limit) {

        LOG.debug("Searching for Tweets with offset " + offset + " and limit " + limit);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tweet> criteriaQuery = criteriaBuilder.createQuery(Tweet.class);

        Root<Tweet> from = criteriaQuery.from(Tweet.class);
        criteriaQuery.select(from);
        criteriaQuery.orderBy(criteriaBuilder.desc(from.get("date")));

        TypedQuery<Tweet> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Tweet> results = query.getResultList();

        LOG.debug("Located {} Tweets", results.size());

        return results;
    }

    public List<Tweet> findPartialByIdsOrderByDate(final int offset, final int limit, final List<Long> ids) {

        LOG.debug("Searching for Tweets with offset " + offset + " and limit " + limit);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tweet> criteriaQuery = criteriaBuilder.createQuery(Tweet.class);

        Root<Tweet> from = criteriaQuery.from(Tweet.class);
        criteriaQuery.select(from);
        criteriaQuery.orderBy(criteriaBuilder.desc(from.get("date")));
        criteriaQuery.where(from.get("id").in(ids));

        TypedQuery<Tweet> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Tweet> results = query.getResultList();

        LOG.debug("Located {} Tweets", results.size());

        return results;
    }
}