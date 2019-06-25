package de.openknowledge.jwe.application.tweet;

import de.openknowledge.jwe.domain.tweet.Tweet;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Lightweight Tweet DTO representing a new {@link Tweet}.
 */

@Schema
class TweetNewDTO extends AbstractTweet {

    public TweetNewDTO() {
        super();
    }

    @Override
    public String toString() {

        return "TweetNewDTO {" +
                "message='" + getMessage() + '\'' +
                ", postTime='" + getPostTime() + '\'' +
                '}';
    }
}
