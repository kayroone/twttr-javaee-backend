package de.openknowledge.jwe.domain.model.tweet;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.infrastructure.domain.builder.DefaultBuilder;
import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * An entity that represents a {@link Tweet}.
 */

@Entity
@Table(name = "TWEET")
public class Tweet extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
    @TableGenerator(name = "TABLE_GEN", table = "TABLE_GEN", pkColumnName = "ID_GEN",
            pkColumnValue = "VALUE_GEN", allocationSize = 1)
    @Column(name = "TWEET_ID", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "TWEET_POST_TIME", nullable = false)
    private LocalDateTime postTime;

    @NotNull
    @Column(name = "TWEET_MESSAGE", nullable = false)
    private String message;

    @OneToOne
    @NotNull
    @Column(name = "TWEET_AUTHOR", nullable = false)
    private User author;

    @OneToMany
    @Column(name = "TWEET_LIKER", nullable = false)
    private List<User> liker;

    @OneToMany
    @Column(name = "TWEET_RETWEETS", nullable = false)
    private List<Tweet> retweets;

    // CONSTRUCTOR ----------------------------------------------------------------------------------------------------

    protected Tweet() {
        super();
    }

    // GETTER AND SETTER ----------------------------------------------------------------------------------------------

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPostTime() {
        return postTime;
    }

    public void setPostTime(LocalDateTime postTime) {
        this.postTime = postTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public List<Tweet> getRetweets() {
        return retweets;
    }

    public void setRetweets(List<Tweet> retweets) {
        this.retweets = retweets;
    }

    public List<User> getLiker() {
        return liker;
    }

    public void setLiker(List<User> liker) {
        this.liker = liker;
    }

    // BUILDER --------------------------------------------------------------------------------------------------------

    /**
     * Builder for the entity {@link Tweet}.
     */

    public static class TweetBuilder extends DefaultBuilder<Tweet> {

        public TweetBuilder() {
            super();
        }

        public TweetBuilder withPostTime(final LocalDateTime postTime) {
            this.instance.postTime = notNull(postTime, "Post time must not be null");
            return this;
        }

        public TweetBuilder withAuthor(final User author) {
            this.instance.author = notNull(author, "Author must not be null");
            return this;
        }

        public TweetBuilder withMessage(final String message) {
            this.instance.message = notNull(message, "Message must not be null");
            return this;
        }

        public TweetBuilder withRetweeterList(final List<User> retweeter) {
            this.instance.retweeter = retweeter;
            return this;
        }

        public TweetBuilder withLikerList(final List<User> liker) {
            this.instance.liker = liker;
            return this;
        }
    }
}
