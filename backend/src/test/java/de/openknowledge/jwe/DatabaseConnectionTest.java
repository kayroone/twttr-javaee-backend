package de.openknowledge.jwe;

import de.openknowledge.jwe.application.tweet.TweetResource;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.Metamodel;

/**
 * Integration test class for the resource {@link TweetResource}.
 */
public class DatabaseConnectionTest {

    @Test
    public void getConnection() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test-local");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Metamodel metamodel = entityManager.getMetamodel();
        System.out.println(metamodel);
    }

}
