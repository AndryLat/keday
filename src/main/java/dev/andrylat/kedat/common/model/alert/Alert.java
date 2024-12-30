package dev.andrylat.kedat.common.model.alert;

import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@Jacksonized
public class Alert {
  private Instant timestamp;
  private String priority;
  private String additionalInfo;
  private String alertType;
  private Target target;
}
