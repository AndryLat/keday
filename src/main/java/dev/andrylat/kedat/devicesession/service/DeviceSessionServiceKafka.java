package dev.andrylat.kedat.devicesession.service;

import dev.andrylat.kedat.common.model.device.DeviceSession;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class DeviceSessionServiceKafka implements DeviceSessionService {

  private final String deviceSessionTopicName;
  private final KafkaTemplate<String, DeviceSession> kafkaTemplate;

  public DeviceSessionServiceKafka(
      @Value("${kafka.topics.device-sessions.name}") String deviceSessionsTopicName,
      KafkaTemplate<String, DeviceSession> kafkaTemplate) {
    this.deviceSessionTopicName = deviceSessionsTopicName;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public CompletableFuture<SendResult<String, DeviceSession>> sendSession(
      DeviceSession deviceSession) {
    return kafkaTemplate.send(deviceSessionTopicName, deviceSession);
  }
}
