package de.jwiegmann.twttr.application.tweet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

@Provider
public class TweetDateTimeProvider implements ContextResolver<ObjectMapper> {

  private final ObjectMapper MAPPER;

  public TweetDateTimeProvider() {
    MAPPER = new ObjectMapper();
    MAPPER.registerModule(new JavaTimeModule());
    MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return MAPPER;
  }
}
