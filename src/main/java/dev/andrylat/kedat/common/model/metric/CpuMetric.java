package dev.andrylat.kedat.common.model.metric;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CpuMetric {
  private double usagePercentage;
  private double temperatureCelsius;
  private double frequencyGHz;
  private int activeThreads;

  // Getters and setters
}
