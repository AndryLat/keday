package dev.andrylat.kedat.common.model.metric;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class RamMetric {
  private int usedMB;
  private int availableMB;
  private int pageFaults;

  // Getters and setters
}
