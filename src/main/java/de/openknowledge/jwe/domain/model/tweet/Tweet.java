package de.openknowledge.jwe.domain.model.tweet;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.infrastructure.domain.builder.DefaultBuilder;
import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * An entity that represents a {@link Tweet}.
 */

@Entity
@Table(name = "TAB_TWEET")
public class Tweet extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TWEET_ID", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "TWEET_POST_TIME", nullable = false)
    private LocalDateTime postTime;

    @NotNull
    @Column(name = "TWEET_MESSAGE", nullable = false)
    private String message;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "TWEET_AUTHOR")
    private User author;

    @ManyToMany(mappedBy = "likes")
    private Set<User> liker;

    @OneToMany(mappedBy = "rootTweet")
    private Set<Tweet> retweets;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Tweet rootTweet;

    public static TweetBuilder newBuilder() {
        return new TweetBuilder();
    }

    protected Tweet() {
        super();
    }

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

    public String getMessage() { return message; }

    public User getAuthor() {
        return author;
    }

    public Set<User> getLiker() { return liker; }

    public Set<Tweet> getRetweets() { return retweets; }

    public Tweet getRootTweet() {
        return rootTweet;
    }

    /**
     * Builder for the entity {@link Tweet}.
     */

    public static class TweetBuilder extends DefaultBuilder<Tweet> {

        public TweetBuilder() {

            super();

            this.instance.liker = new HashSet<>();
            this.instance.retweets = new HashSet<>();
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

        public TweetBuilder withLiker(final Set<User> liker) {
            this.instance.liker = notNull(liker, "Message must not be null");
            return this;
        }

        public TweetBuilder withLike(final User liker) {
            notNull(liker, "Message must not be null");
            this.instance.liker.add(liker);
            return this;
        }

        public TweetBuilder withRetweets(final Set<Tweet> retweets) {
            this.instance.retweets = notNull(retweets, "Message must not be null");
            return this;
        }

        public TweetBuilder withRetweet(final Tweet retweet) {
            notNull(retweet, "Message must not be null");
            this.instance.retweets.add(retweet);
            return this;
        }

        public TweetBuilder withRootTweet(final Tweet tweet) {
            this.instance.rootTweet = notNull(tweet, "Tweet must not be null");
            return this;
        }
    }
}
