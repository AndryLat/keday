package dev.andrylat.kedat.devicesession;

import dev.andrylat.kedat.common.Constants;
import dev.andrylat.kedat.common.model.device.session.DeviceSession;
import dev.andrylat.kedat.common.model.device.session.DeviceStatus;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DeviceSessionFixtures {
  public static final String DEVICE_UUID = "bbbbbbbb-d9e9-41f8-ad29-ebf907121312";
  public static final String USER_UUID = "1111aaaa-197b-45d4-926b-889c65e36ff5";

  public static DeviceSession buildDeviceSession(String messageId) {
    return DeviceSession.builder()
        .messageId(messageId)
        .deviceId(DEVICE_UUID)
        .userId(USER_UUID)
        .time(getBaseTime())
        .status(DeviceStatus.ONLINE)
        .build();
  }

  public static Instant getBaseTime() {
    return Instant.parse(Constants.FIXED_TIME);
  }

  public static Instant getTimePlusNSeconds(int seconds) {
    return getBaseTime().plus(seconds, ChronoUnit.SECONDS);
  }

  public static Instant getTimeMinusNSeconds(int seconds) {
    return getBaseTime().minus(seconds, ChronoUnit.SECONDS);
  }
}
