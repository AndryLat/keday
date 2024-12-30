package dev.andrylat.kedat.common.model.metric;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.andrylat.kedat.common.InstantConstants;
import java.time.Instant;
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
public class DeviceMetric {
  private String messageId;
  private String deviceId;

  @JsonFormat(
      pattern = InstantConstants.DATE_PATTERN,
      timezone = InstantConstants.DEFAULT_TIME_ZONE)
  private Instant timestamp;

  private CpuMetric cpu;
  private GpuMetric gpu;
  private RamMetric ram;
  private DiskMetric disk;
  private NetworkMetric network;
  private BatteryMetric battery;
  private FanMetric fan;
  private GameMetric game;
}
