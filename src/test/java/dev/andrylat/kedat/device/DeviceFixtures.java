package dev.andrylat.kedat.device;

import dev.andrylat.kedat.common.Constants;
import dev.andrylat.kedat.common.model.device.Battery;
import dev.andrylat.kedat.common.model.device.Cpu;
import dev.andrylat.kedat.common.model.device.Device;
import dev.andrylat.kedat.common.model.device.Disk;
import dev.andrylat.kedat.common.model.device.Gpu;
import dev.andrylat.kedat.common.model.device.Network;
import dev.andrylat.kedat.common.model.device.Ram;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DeviceFixtures {
  public static final String DEVICE_UUID = "3333bbbb-122c-42d9-ada1-7a0683452f9d";

  public static String getDeviceJson() {
    return """
        {
            "deviceId": "%s",
            "deviceType": "Laptop",
            "operatingSystem": "Windows 10",
            "cpu": {
              "model": "Intel i7-9700K",
              "cores": 8,
              "speedGHz": 3.6
            },
            "ram": {
              "totalMB": 16384
            },
            "gpu": {
              "model": "NVIDIA GTX 1660 Ti",
              "vramMB": 6144
            },
            "disk": {
              "type": "SSD",
              "model": "Samsung 970 EVO",
              "totalGB": 512,
              "usedGB": 256,
              "healthStatus": "Healthy"
            },
            "battery": {
              "status": "Charging",
              "chargeLevel": 85
            },
            "network": {
              "status": "Online",
              "latencyMs": 30,
              "downloadSpeedMbps": 100,
              "uploadSpeedMbps": 20
            },
            "lastSeen": "2025-01-01 06:00:00.000"
        }
                          """
        .formatted(DEVICE_UUID);
  }

  public static Device buildDevice() {
    return Device.builder()
        .deviceId(DEVICE_UUID)
        .deviceType("Laptop")
        .operatingSystem("Windows 10")
        .cpu(Cpu.builder().model("Intel i7-9700K").cores(8).speedGHz(3.6).build())
        .ram(Ram.builder().totalMB(16384).build())
        .gpu(Gpu.builder().model("NVIDIA GTX 1660 Ti").vramMB(6144).build())
        .disk(
            Disk.builder()
                .type("SSD")
                .model("Samsung 970 EVO")
                .totalGB(512)
                .usedGB(256)
                .healthStatus("Healthy")
                .build())
        .battery(Battery.builder().chargeLevel(85).status("Charging").build())
        .network(
            Network.builder()
                .status("Online")
                .latencyMs(30)
                .downloadSpeedMbps(100)
                .uploadSpeedMbps(20)
                .build())
        .lastSeen(getBaseTime())
        .build();
  }

  public static String getUpdatedDeviceJson() {
    return """
        {
            "deviceId": "%s",
            "deviceType": "Laptop",
            "operatingSystem": "Windows 10",
            "cpu": {
              "model": "Intel i7-9700K",
              "cores": 8,
              "speedGHz": 3.6
            },
            "ram": {
              "totalMB": 16384
            },
            "gpu": {
              "model": "NVIDIA GTX 1660 Ti",
              "vramMB": 6144
            },
            "disk": {
              "type": "SSD",
              "model": "Samsung 970 EVO",
              "totalGB": 512,
              "usedGB": 256,
              "healthStatus": "Healthy"
            },
            "battery": {
              "status": "Charging",
              "chargeLevel": 11
            },
            "network": {
              "status": "Online",
              "latencyMs": 30,
              "downloadSpeedMbps": 100,
              "uploadSpeedMbps": 20
            },
            "lastSeen": "2025-01-01 06:00:00.000"
        }
                          """
        .formatted(DEVICE_UUID);
  }

  public static Device buildUpdatedDevice() {
    return Device.builder()
        .deviceId(DEVICE_UUID)
        .deviceType("Laptop")
        .operatingSystem("Windows 10")
        .cpu(Cpu.builder().model("Intel i7-9700K").cores(8).speedGHz(3.6).build())
        .ram(Ram.builder().totalMB(16384).build())
        .gpu(Gpu.builder().model("NVIDIA GTX 1660 Ti").vramMB(6144).build())
        .disk(
            Disk.builder()
                .type("SSD")
                .model("Samsung 970 EVO")
                .totalGB(512)
                .usedGB(256)
                .healthStatus("Healthy")
                .build())
        .battery(Battery.builder().chargeLevel(11).status("Charging").build())
        .network(
            Network.builder()
                .status("Online")
                .latencyMs(30)
                .downloadSpeedMbps(100)
                .uploadSpeedMbps(20)
                .build())
        .lastSeen(getBaseTime())
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
