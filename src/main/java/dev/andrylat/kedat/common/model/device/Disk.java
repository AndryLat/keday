package dev.andrylat.kedat.common.model.device;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Disk {
  private String type;
  private String model;
  private int totalGB;
  private int usedGB;
  private String healthStatus;
}
