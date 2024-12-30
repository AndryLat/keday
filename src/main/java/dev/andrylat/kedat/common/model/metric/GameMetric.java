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
public class GameMetric {
  private String resolution;
  private int fps;
  private double frameTimeMs;
  private int networkLatencyMs;

  // Getters and setters
}
