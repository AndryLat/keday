package dev.andrylat.kedat.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
public class KafkaTopicCreationConfig {

  @Bean
  public NewTopic deviceMetricTopic(
      @Value("${kafka.topics.device-metrics.name}") String topicName,
      @Value("${kafka.topics.device-metrics.partitions}") Integer partitions,
      @Value("${kafka.topics.device-metrics.replicas}") Integer replicas,
      @Value("${kafka.topics.device-metrics.retention}") String retention) {

    return buildTopic(topicName, partitions, replicas, retention);
  }

  @Bean
  public NewTopic deviceSessionTopic(
      @Value("${kafka.topics.device-sessions.name}") String topicName,
      @Value("${kafka.topics.device-sessions.partitions}") Integer partitions,
      @Value("${kafka.topics.device-sessions.replicas}") Integer replicas,
      @Value("${kafka.topics.device-sessions.retention}") String retention) {

    return buildTopic(topicName, partitions, replicas, retention);
  }

  @Bean
  public NewTopic gameSessionTopic(
      @Value("${kafka.topics.game-sessions.name}") String topicName,
      @Value("${kafka.topics.game-sessions.partitions}") Integer partitions,
      @Value("${kafka.topics.game-sessions.replicas}") Integer replicas,
      @Value("${kafka.topics.game-sessions.retention}") String retention) {

    return buildTopic(topicName, partitions, replicas, retention);
  }

  private NewTopic buildTopic(
      String topicName, Integer partitions, Integer replicas, String retention) {
    var topicBuilder = TopicBuilder.name(topicName).partitions(partitions).replicas(replicas);

    if (retention != null) {
      topicBuilder.config(TopicConfig.RETENTION_MS_CONFIG, retention);
    }

    var newTopic = topicBuilder.build();
    log.info("New topic is created: {}", newTopic);

    return newTopic;
  }
}
