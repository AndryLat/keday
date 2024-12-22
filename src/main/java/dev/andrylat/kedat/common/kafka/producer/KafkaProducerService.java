package dev.andrylat.kedat.common.kafka.producer;

import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

public interface KafkaProducerService<T> {
  CompletableFuture<SendResult<String, T>> sendEvent(String topicName, T event);
}
