package dev.andrylat.kedat.metric.service;

import dev.andrylat.kedat.common.model.metric.DeviceMetric;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class DeviceMetricServiceKafka implements DeviceMetricService {

  private final String deviceMetricTopicName;
  private final KafkaTemplate<String, DeviceMetric> kafkaTemplate;

  public DeviceMetricServiceKafka(
      @Value("${kafka.topics.device-metrics.name}") String deviceMetricTopicName,
      KafkaTemplate<String, DeviceMetric> kafkaTemplate) {
    this.deviceMetricTopicName = deviceMetricTopicName;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public CompletableFuture<SendResult<String, DeviceMetric>> sendMetric(DeviceMetric deviceMetric) {
    return kafkaTemplate.send(deviceMetricTopicName, deviceMetric);
  }
}
