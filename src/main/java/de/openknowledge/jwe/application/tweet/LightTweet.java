package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.model.tweet.Tweet;

public class LightTweet extends AbstractTweet {

    private Long id;

    private Long author;

    public LightTweet(Tweet tweet) {

        super();

        this.id = tweet.getId();
        this.author = tweet.getAuthor().getId();
        setMessage(tweet.getMessage());
    }

    public Long getId() {
        return id;
    }

    public Long getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "LightTweet{" +
                "id=" + id +
                ", author=" + author +
                ", message=" + getMessage() +
                ", postTime=" + getPostTime() +
                '}';
    }
}