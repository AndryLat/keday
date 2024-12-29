package dev.andrylat.kedat.metric;

import dev.andrylat.kedat.common.Constants;
import dev.andrylat.kedat.common.model.metric.BatteryMetric;
import dev.andrylat.kedat.common.model.metric.CpuMetric;
import dev.andrylat.kedat.common.model.metric.DeviceMetric;
import dev.andrylat.kedat.common.model.metric.DiskMetric;
import dev.andrylat.kedat.common.model.metric.FanMetric;
import dev.andrylat.kedat.common.model.metric.GameMetric;
import dev.andrylat.kedat.common.model.metric.GpuMetric;
import dev.andrylat.kedat.common.model.metric.NetworkMetric;
import dev.andrylat.kedat.common.model.metric.RamMetric;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DeviceMetricFixtures {
  public static final String DEVICE_UUID = "aaaaaaaa-9528-43eb-a6a1-7aa0d0e3422f";

  public static DeviceMetric buildDeviceMetric(String messageId) {
    return DeviceMetric.builder()
        .messageId(messageId)
        .deviceId(DEVICE_UUID)
        .timestamp(getBaseTime())
        .cpu(
            CpuMetric.builder()
                .usagePercentage(13.3)
                .temperatureCelsius(98.2)
                .frequencyGHz(1.261)
                .activeThreads(4218)
                .build())
        .gpu(
            GpuMetric.builder()
                .usagePercentage(1.23)
                .temperatureCelsius(66)
                .vramUsageMB(1362)
                .build())
        .ram(RamMetric.builder().availableMB(14782).usedMB(9212).pageFaults(1).build())
        .disk(
            DiskMetric.builder()
                .readSpeedMBps(12.33)
                .writeSpeedMBps(1.344)
                .usagePercentage(35.34)
                .temperatureCelsius(34)
                .ssdHealthPercentage(95.5)
                .build())
        .network(
            NetworkMetric.builder()
                .downloadSpeedMBps(1.22)
                .uploadSpeedMBps(0.12)
                .packetLossPercentage(0.23)
                .latencyMs(16)
                .build())
        .battery(BatteryMetric.builder().chargePercentage(96).status("charging").build())
        .fan(FanMetric.builder().speedRPM(3200).build())
        .game(
            GameMetric.builder()
                .resolution("1920x1080")
                .fps(35)
                .frameTimeMs(11)
                .networkLatencyMs(0)
                .build())
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
