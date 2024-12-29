package dev.andrylat.kedat.common.service;

import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class DateTimeServiceUtc implements DateTimeService {

  @Override
  public Instant now() {
    return Instant.now(Clock.systemUTC());
  }
}
