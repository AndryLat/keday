package dev.andrylat.kedat.web;

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

    return buildTopic(topicName, partitions, replicas, retention, null, null, null);
  }

  @Bean
  public NewTopic deviceSessionTopic(
      @Value("${kafka.topics.device-sessions.name}") String topicName,
      @Value("${kafka.topics.device-sessions.partitions}") Integer partitions,
      @Value("${kafka.topics.device-sessions.replicas}") Integer replicas,
      @Value("${kafka.topics.device-sessions.retention}") String retention) {

    return buildTopic(topicName, partitions, replicas, retention, null, null, null);
  }

  @Bean
  public NewTopic gameSessionTopic(
      @Value("${kafka.topics.game-sessions.name}") String topicName,
      @Value("${kafka.topics.game-sessions.partitions}") Integer partitions,
      @Value("${kafka.topics.game-sessions.replicas}") Integer replicas,
      @Value("${kafka.topics.game-sessions.retention}") String retention) {

    return buildTopic(topicName, partitions, replicas, retention, null, null, null);
  }

  @Bean
  public NewTopic users(
      @Value("${kafka.topics.users.name}") String topicName,
      @Value("${kafka.topics.users.partitions}") Integer partitions,
      @Value("${kafka.topics.users.replicas}") Integer replicas,
      @Value("${kafka.topics.users.compact.enabled}") Boolean isCompact,
      @Value("${kafka.topics.users.compact.segment}") String segmentTime,
      @Value("${kafka.topics.users.compact.deletion-retention}") String deletionRetentionTime) {
    return buildTopic(
        topicName, partitions, replicas, null, isCompact, segmentTime, deletionRetentionTime);
  }

  @Bean
  public NewTopic devices(
      @Value("${kafka.topics.devices.name}") String topicName,
      @Value("${kafka.topics.devices.partitions}") Integer partitions,
      @Value("${kafka.topics.devices.replicas}") Integer replicas,
      @Value("${kafka.topics.devices.compact.enabled}") Boolean isCompact,
      @Value("${kafka.topics.devices.compact.segment}") String segmentTime,
      @Value("${kafka.topics.devices.compact.deletion-retention}") String deletionRetentionTime) {
    return buildTopic(
        topicName, partitions, replicas, null, isCompact, segmentTime, deletionRetentionTime);
  }

  @Bean
  public NewTopic games(
      @Value("${kafka.topics.games.name}") String topicName,
      @Value("${kafka.topics.games.partitions}") Integer partitions,
      @Value("${kafka.topics.games.replicas}") Integer replicas,
      @Value("${kafka.topics.games.compact.enabled}") Boolean isCompact,
      @Value("${kafka.topics.games.compact.segment}") String segmentTime,
      @Value("${kafka.topics.games.compact.deletion-retention}") String deletionRetentionTime) {
    return buildTopic(
        topicName, partitions, replicas, null, isCompact, segmentTime, deletionRetentionTime);
  }

  private NewTopic buildTopic(
      String topicName,
      Integer partitions,
      Integer replicas,
      String retention,
      Boolean isCompact,
      String segmentTime,
      String deletionRetentionTime) {
    var topicBuilder = TopicBuilder.name(topicName).partitions(partitions).replicas(replicas);

    if (retention != null) {
      topicBuilder.config(TopicConfig.RETENTION_MS_CONFIG, retention);
    }

    if (isCompact != null && isCompact) {
      topicBuilder.compact();
    }

    if (segmentTime != null) {
      topicBuilder.config(TopicConfig.SEGMENT_MS_CONFIG, segmentTime);
    }

    if (deletionRetentionTime != null) {
      topicBuilder.config(TopicConfig.DELETE_RETENTION_MS_CONFIG, deletionRetentionTime);
    }

    var newTopic = topicBuilder.build();
    log.info("New topic is created: {}", newTopic);

    return newTopic;
  }
}
