package dev.andrylat.kedat.common.model.metric;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@ToString
@EqualsAndHashCode
@Jacksonized
public class FanMetric {
  private int speedRPM;

  // Getters and setters
}
