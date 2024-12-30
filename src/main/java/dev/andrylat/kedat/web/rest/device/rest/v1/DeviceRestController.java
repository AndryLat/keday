package dev.andrylat.kedat.web.rest.device.rest.v1;

import dev.andrylat.kedat.common.model.device.Device;
import dev.andrylat.kedat.web.rest.device.service.DeviceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
@AllArgsConstructor
public class DeviceRestController {
  private final DeviceService deviceService;

  @PostMapping
  @ResponseStatus(value = HttpStatus.CREATED)
  void add(@RequestBody Device newDevice) {
    deviceService.produce(newDevice);
  }

  @PutMapping("/{deviceId}")
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  void update(@PathVariable String deviceId, @RequestBody Device device) {
    deviceService.produce(device);
  }

  @DeleteMapping("/{deviceId}")
  @ResponseStatus(value = HttpStatus.OK)
  void remove(@PathVariable String deviceId) {
    deviceService.remove(deviceId);
  }
}
