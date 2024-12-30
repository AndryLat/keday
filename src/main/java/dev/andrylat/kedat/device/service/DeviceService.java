package dev.andrylat.kedat.device.service;

import dev.andrylat.kedat.common.model.device.Device;

public interface DeviceService {
  void produce(Device device);

  void remove(String deviceId);
}
