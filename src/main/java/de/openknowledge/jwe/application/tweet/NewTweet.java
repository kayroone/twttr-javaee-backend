package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.model.tweet.Tweet;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * DTO represents a new {@link Tweet}.
 */

@Schema
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
