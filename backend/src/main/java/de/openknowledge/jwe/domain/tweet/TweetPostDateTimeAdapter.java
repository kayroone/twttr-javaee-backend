package de.openknowledge.jwe.domain.tweet;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * JAXB XML adapter for marshalling the JDK 1.8 type {@code LocalDateTime}.
 *
 * (De-)serializes format: yyyy-MM-ddTHH:mm:ss.SSSZ
 */
public class TweetPostDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(final String value) {
        notNull(value, "tweet must not be null");
        return LocalDateTime.parse(value, FORMATTER);
    }

    @Override
    public String marshal(final LocalDateTime value) {
        notNull(value, "tweet must not be null");
        return String.format("%s.000Z", value.format(FORMATTER));
    }
}
