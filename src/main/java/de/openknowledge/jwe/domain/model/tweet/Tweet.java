package de.openknowledge.jwe.domain.model.tweet;

import de.openknowledge.jwe.domain.model.user.User;
import de.openknowledge.jwe.infrastructure.domain.builder.DefaultBuilder;
import de.openknowledge.jwe.infrastructure.domain.entity.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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

    @ManyToOne
    @JoinColumn(name = "TWEET_ROOT_TWEET")
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

    public void setPostTime(LocalDateTime postTime) {
        this.postTime = postTime;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<User> getLiker() { return liker; }

    public void setLiker(Set<User> liker) { this.liker = liker; }

    public Tweet getRootTweet() {
        return rootTweet;
    }

    public void setRootTweet(Tweet rootTweet) {
        this.rootTweet = rootTweet;
    }

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

        public TweetBuilder withLiker(final Set<User> liker) {
            this.instance.liker = notNull(liker, "Message must not be null");
            return this;
        }

        public TweetBuilder withRootTweet(final Tweet tweet) {
            this.instance.rootTweet = notNull(tweet, "Tweet must not be null");
            return this;
        }
    }
}
