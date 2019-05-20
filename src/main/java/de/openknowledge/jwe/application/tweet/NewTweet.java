package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.model.tweet.Tweet;

/**
 * DTO represents a new {@link Tweet}.
 */

public class NewTweet extends AbstractTweet {

    public NewTweet() {
        super();
    }

    @Override
    public String toString() {

        return "NewTweet {" +
                "message='" + getMessage() + '\'' +
                ", author='" + getAuthor() + '\'' +
                ", postTime='" + getPostTime() + '\'' +
                '}';
    }
}
