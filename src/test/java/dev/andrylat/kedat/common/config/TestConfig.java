package dev.andrylat.kedat.common.config;

import dev.andrylat.kedat.common.Constants;
import dev.andrylat.kedat.common.service.DateTimeService;
import java.time.Instant;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

  @Primary
  @Bean
  public DateTimeService dateTimeService() {
    return new DateTimeService() {
      @Override
      public Instant now() {
        return Instant.parse(Constants.FIXED_TIME);
      }
    };
  }
}
