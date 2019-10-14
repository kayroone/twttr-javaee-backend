package de.jwiegmann.twttr.application.tweet;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Lightweight Tweet DTO representing a new Tweet.
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
