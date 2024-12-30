package dev.andrylat.kedat.common.model.device;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Network {
  private String status;
  private int latencyMs;
  private int downloadSpeedMbps;
  private int uploadSpeedMbps;
}
