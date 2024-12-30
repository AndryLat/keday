package dev.andrylat.kedat.common.model.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.andrylat.kedat.common.InstantConstants;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Device {
  private String deviceId;
  private String deviceType;
  private String operatingSystem;
  private Cpu cpu;
  private Ram ram;
  private Gpu gpu;
  private Disk disk;
  private Battery battery;
  private Network network;

  @JsonFormat(
      pattern = InstantConstants.DATE_PATTERN,
      timezone = InstantConstants.DEFAULT_TIME_ZONE)
  private Instant lastSeen;
}
