package dev.andrylat.kedat.common.kafka.producer;

import java.util.concurrent.CompletableFuture;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@AllArgsConstructor
public class KafkaProducerServiceImpl<T> implements KafkaProducerService<T> {
    private final KafkaTemplate<String, T> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<String, T>> sendEvent(String topicName, T event) {
        return this.kafkaTemplate.send(topicName, event);
    }
}
