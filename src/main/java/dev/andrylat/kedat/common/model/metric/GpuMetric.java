package dev.andrylat.kedat.common.model.metric;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class GpuMetric {
  private double usagePercentage;
  private double temperatureCelsius;
  private int vramUsageMB;

  // Getters and setters
}
