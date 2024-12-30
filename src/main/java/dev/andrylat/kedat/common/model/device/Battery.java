package dev.andrylat.kedat.common.model.device;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Battery {
  private String status;
  private int chargeLevel;
}
