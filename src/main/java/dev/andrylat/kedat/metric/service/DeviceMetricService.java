package dev.andrylat.kedat.metric.service;

import dev.andrylat.kedat.common.model.metric.DeviceMetric;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

public interface DeviceMetricService {
  CompletableFuture<SendResult<String, DeviceMetric>> sendMetric(DeviceMetric deviceMetric);
}
