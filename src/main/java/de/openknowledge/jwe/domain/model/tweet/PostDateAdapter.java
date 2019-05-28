package de.openknowledge.jwe.domain.model.tweet;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostDateAdapter extends XmlAdapter<String, LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public LocalDateTime unmarshal(final String value) {
        return LocalDateTime.parse(value, FORMATTER);
    }

    @Override
    public String marshal(final LocalDateTime value) {
        return String.format("%s.000Z", value.format(FORMATTER));
    }
}
