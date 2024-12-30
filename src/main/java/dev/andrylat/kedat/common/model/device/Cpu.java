package dev.andrylat.kedat.common.model.device;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Cpu {
  private String model;
  private int cores;
  private double speedGHz;
}
