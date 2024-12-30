package dev.andrylat.kedat.device.service;

import dev.andrylat.kedat.common.exception.KafkaMessageIsNotSentException;
import dev.andrylat.kedat.common.model.device.Device;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceKafka implements DeviceService {
  private final String deviceTopicName;
  private final KafkaTemplate<String, Device> kafkaTemplate;

  public DeviceServiceKafka(
      @Value("${kafka.topics.devices.name}") String usersTopicName,
      KafkaTemplate<String, Device> kafkaTemplate) {
    this.deviceTopicName = usersTopicName;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void produce(Device device) {
    kafkaTemplate
        .send(deviceTopicName, device.getDeviceId(), device)
        .handle(
            (result, throwable) -> {
              if (throwable != null) {
                throw new KafkaMessageIsNotSentException(
                    "Device with ID " + device.getDeviceId() + " is not processed.", throwable);
              }
              return result;
            });
  }

  @Override
  public void remove(String deviceId) {
    kafkaTemplate.send(deviceTopicName, deviceId, null);
  }
}
