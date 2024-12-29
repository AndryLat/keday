package dev.andrylat.kedat.common.model.metric;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class GameMetric {
  private String resolution;
  private int fps;
  private double frameTimeMs;
  private int networkLatencyMs;

  // Getters and setters
}
