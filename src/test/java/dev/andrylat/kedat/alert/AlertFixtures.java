package dev.andrylat.kedat.alert;

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

public class AlertFixtures {
  public static final String DEVICE_UUID = "ccccaaaa-478a-4ee5-88cd-38060e2eb02f";

  public static DeviceMetric buildDeviceMetric(String messageId, Instant timestamp, double temp) {
    return DeviceMetric.builder()
        .messageId(messageId)
        .deviceId(DEVICE_UUID)
        .timestamp(timestamp)
        .cpu(
            CpuMetric.builder()
                .usagePercentage(13.3)
                .temperatureCelsius(temp)
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
}
