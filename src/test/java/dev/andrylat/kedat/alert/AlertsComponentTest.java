package dev.andrylat.kedat.alert;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.andrylat.kedat.common.config.TestConfig;
import dev.andrylat.kedat.common.model.alert.Alert;
import dev.andrylat.kedat.common.model.metric.DeviceMetric;
import dev.andrylat.kedat.streams.KafkaStreamsConfig;
import dev.andrylat.kedat.streams.alert.AlertsKafkaStream;
import dev.andrylat.kedat.streams.alert.mapper.AvgSumToAlertMapper;
import dev.andrylat.kedat.streams.alert.serdes.AggregatorSerDes;
import dev.andrylat.kedat.streams.alert.serdes.AlertSerDes;
import dev.andrylat.kedat.streams.alert.serdes.AvgSumSerDes;
import dev.andrylat.kedat.streams.alert.serdes.DeviceMetricSerdes;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.TestRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

@SpringBootTest(
    classes = {
      KafkaStreamsConfig.class,
      AlertsKafkaStream.class,
      AvgSumToAlertMapper.class,
      AlertSerDes.class,
      AvgSumSerDes.class,
      AggregatorSerDes.class,
      DeviceMetricSerdes.class,
      TestConfig.class
    })
@Slf4j
public class AlertsComponentTest {
  @Value("${kafka.topics.device-metrics.name}")
  private String deviceMetricsTopicName;

  @Value("${kafka.topics.alerts.name}")
  private String alertsTopicName;

  @Autowired private StreamsBuilder streamsBuilder;

  @Autowired private KafkaStreamsConfiguration streamsConfig;

  private final Serde<String> stringSerde = Serdes.String();
  @Autowired private Serde<DeviceMetric> deviceMetricSerde;
  @Autowired private Serde<Alert> alertSerde;

  private TopologyTestDriver testDriver;

  private TestInputTopic<String, DeviceMetric> deviceMetricInputTopic;
  private TestOutputTopic<String, Alert> alertsOutputTopic;

  private Random random = new Random(666);

  @BeforeEach
  void setUp() {
    var topology = streamsBuilder.build();
    log.info("Alerts topology: {}", topology.describe());

    testDriver = new TopologyTestDriver(topology, streamsConfig.asProperties());

    deviceMetricInputTopic =
        testDriver.createInputTopic(
            deviceMetricsTopicName, stringSerde.serializer(), deviceMetricSerde.serializer());
    alertsOutputTopic =
        testDriver.createOutputTopic(
            alertsTopicName, stringSerde.deserializer(), alertSerde.deserializer());
  }

  @AfterEach
  void tearDown() {
    if (testDriver != null) {
      testDriver.close();
    }
  }

  @Test
  void shouldProduceAlertWhenCpuTempHigher90DegreesDuring5Minutes() {
    // GIVEN
    var baseTime = AlertFixtures.getBaseTime();

    // WHEN
    IntStream.range(1, 300)
        .forEach(
            counter -> {
              var randomTempOffset = random.nextDouble() * 5 + (0.1 * counter);
              var timestamp = baseTime.plusSeconds(counter * 2);
              var deviceMetric =
                  AlertFixtures.buildDeviceMetric(
                      UUID.randomUUID().toString(), timestamp, 88 + randomTempOffset);
              deviceMetricInputTopic.pipeInput(deviceMetric.getDeviceId(), deviceMetric, timestamp);
            });
    // THEN
    List<TestRecord<String, Alert>> results = alertsOutputTopic.readRecordsToList();

    assertEquals(1, results.size());

    var alert = results.getFirst().value();

    assertEquals(AlertFixtures.getBaseTime(), alert.getTimestamp());
    assertEquals("medium", alert.getPriority());
    assertEquals(AlertFixtures.DEVICE_UUID, alert.getTarget().getDeviceId());
  }

  @Test
  void shouldNotProduceAlertWhenCpuTempIsNotHigher90DegreesDuring5Minutes() {
    // GIVEN
    Instant baseTime = AlertFixtures.getBaseTime();

    // WHEN
    IntStream.range(1, 300)
        .forEach(
            counter -> {
              var randomTempOffset = random.nextDouble() * 5 + (0.1 * counter);
              var timestamp = baseTime.plusSeconds(counter * 2);
              var deviceMetric =
                  AlertFixtures.buildDeviceMetric(
                      UUID.randomUUID().toString(), timestamp, 15 + randomTempOffset);
              deviceMetricInputTopic.pipeInput(deviceMetric.getDeviceId(), deviceMetric, timestamp);
            });
            
    // THEN
    var results = alertsOutputTopic.readRecordsToList();
    assertEquals(0, results.size());
  }


}
