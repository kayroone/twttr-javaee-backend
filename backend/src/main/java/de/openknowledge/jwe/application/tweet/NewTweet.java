package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.tweet.Tweet;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Lightweight Tweet DTO representing a new {@link Tweet}.
 */

@Schema
class NewTweet extends AbstractTweet {

    public NewTweet() {
        super();
    }

    @Override
    public String toString() {

        return "NewTweet {" +
                "message='" + getMessage() + '\'' +
                ", postTime='" + getPostTime() + '\'' +
                '}';
    }
}
