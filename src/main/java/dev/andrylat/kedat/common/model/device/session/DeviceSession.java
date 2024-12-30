package dev.andrylat.kedat.common.model.device.session;

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
public class DeviceSession {
  private String messageId;
  private String userId;
  private String deviceId;

  @JsonFormat(
      pattern = InstantConstants.DATE_PATTERN,
      timezone = InstantConstants.DEFAULT_TIME_ZONE)
  private Instant time;

  private DeviceStatus status;
}
