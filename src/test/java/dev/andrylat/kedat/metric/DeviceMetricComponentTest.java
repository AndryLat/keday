package dev.andrylat.kedat.metric;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.common.model.websocket.CommitMessage;
import dev.andrylat.kedat.common.websocket.utils.CommitMessageWebsocketTestHandler;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Slf4j
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {
      dev.andrylat.kedat.KedatApplication.class,
      dev.andrylat.kedat.config.websocket.WebSocketConfig.class,
      dev.andrylat.kedat.common.config.TestConfig.class
    })
@DirtiesContext
@EmbeddedKafka(kraft = true)
public class DeviceMetricComponentTest {
  private static final String DEVICE_METRICS_TOPIC_NAME = "device-metrics";
  private static final String DEVICE_METRICS_ENDPOINT = "/ws/device/metric";

  @LocalServerPort private int port;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private CommitMessageWebsocketTestHandler commitMessageHandler;

  @Test
  void shouldReceiveCommitAckWhenSuccessfullExecution() throws IOException {
    // GIVEN

    // Creation of embedded Kafka consumer for checking results of message production in main code.
    var configs =
        new HashMap<String, Object>(
            KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));

    var consumer =
        new DefaultKafkaConsumerFactory<String, String>(
                configs, new StringDeserializer(), new StringDeserializer())
            .createConsumer();
    consumer.subscribe(Collections.singleton(DEVICE_METRICS_TOPIC_NAME));

    // Websocket client configuration
    var webSocketClient = new StandardWebSocketClient();
    var execute =
        webSocketClient.execute(
            commitMessageHandler, "ws://localhost:" + port + DEVICE_METRICS_ENDPOINT, "");

    var messageId = UUID.randomUUID().toString();
    var deviceMetric = DeviceMetricFixtures.buildDeviceMetric(messageId);
    var deviceMetricAsJson = objectMapper.writeValueAsString(deviceMetric);

    var expectedCommitMessage =
        CommitMessage.builder()
            .messageId(messageId)
            .isSuccessfully(true)
            .timestamp(DeviceMetricFixtures.getBaseTime())
            .build();

    // WHEN && THEN

    execute
        .thenApply(
            websocketSession -> {
              try {
                websocketSession.sendMessage(new TextMessage(deviceMetricAsJson));
                log.info("Message is sent: {}", deviceMetricAsJson);
              } catch (IOException ex) {
                log.error("Message is not sent: {}", ex);
                throw new UncheckedIOException(ex);
              }
              return websocketSession;
            })
        .thenApply(
            websocketSession -> {
              // Every second we are checking if  test is passed and returns true as a result. If
              // not - try again. We do it during 5 seconds after it test will be failed.
              Awaitility.await()
                  .atMost(5, TimeUnit.SECONDS)
                  .ignoreException(NoSuchElementException.class)
                  .until(
                      () -> {
                        if (commitMessageHandler.getCommitMessages().isEmpty()) {
                          return false;
                        }

                        var commitMessage = commitMessageHandler.getCommitMessages().getFirst();
                        assertEquals(expectedCommitMessage, commitMessage);

                        var records = consumer.poll(Duration.of(10, ChronoUnit.SECONDS));

                        assertEquals(1, records.count());
                        assertEquals(deviceMetricAsJson, records.iterator().next().value());
                        return true;
                      });
              return websocketSession;
            })
        .thenAccept(
            websocketSession -> {
              try {
                websocketSession.close();
              } catch (IOException e) {
                throw new UncheckedIOException(e);
              }
            })
        .join();
  }
}
