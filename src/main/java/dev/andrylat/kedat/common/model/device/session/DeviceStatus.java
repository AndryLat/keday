package dev.andrylat.kedat.common.model.device.session;

public enum DeviceStatus {
  ONLINE,
  OFFLINE;

  public static DeviceStatus of(String value) {
    return switch (value.toLowerCase()) {
      case "online" -> ONLINE;
      case "offline" -> OFFLINE;
      default -> throw new IllegalArgumentException("Invalid status value: " + value);
    };
  }
}
