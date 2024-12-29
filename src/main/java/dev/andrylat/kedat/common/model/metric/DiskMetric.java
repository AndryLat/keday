package dev.andrylat.kedat.common.model.metric;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class DiskMetric {
  private double readSpeedMBps;
  private double writeSpeedMBps;
  private double usagePercentage;
  private double temperatureCelsius;
  private double ssdHealthPercentage;

  // Getters and setters
}
