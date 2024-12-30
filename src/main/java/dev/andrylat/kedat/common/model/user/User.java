package dev.andrylat.kedat.common.model.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.andrylat.kedat.common.InstantConstants;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class User {
  private String userId;
  private String username;
  private String email;

  @JsonFormat(
      pattern = InstantConstants.DATE_PATTERN,
      timezone = InstantConstants.DEFAULT_TIME_ZONE)
  private Instant registrationDate;

  @JsonFormat(
      pattern = InstantConstants.DATE_PATTERN,
      timezone = InstantConstants.DEFAULT_TIME_ZONE)
  private Instant lastLogin;

  private Preferences preferences;
}
