package dev.andrylat.kedat.devicesession;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.common.model.CommitMessage;
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
public class DeviceSessionComponentTest {
  private static final String DEVICE_SESSIONS_TOPIC_NAME = "device-sessions";
  private static final String DEVICE_SESSION_ENDPOINT = "/ws/device/session";

  @LocalServerPort private int port;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private CommitMessageWebsocketTestHandler commitMessageHandler;

  @Test
  void shouldReceiveCommitAckWhenSuccessfullExecution() throws IOException {
    // GIVEN

    var configs =
        new HashMap<String, Object>(
            KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));

    var consumer =
        new DefaultKafkaConsumerFactory<String, String>(
                configs, new StringDeserializer(), new StringDeserializer())
            .createConsumer();
    consumer.subscribe(Collections.singleton(DEVICE_SESSIONS_TOPIC_NAME));

    // Websocket client configuration
    var webSocketClient = new StandardWebSocketClient();
    var execute =
        webSocketClient.execute(
            commitMessageHandler, "ws://localhost:" + port + DEVICE_SESSION_ENDPOINT, "");

    var messageId = UUID.randomUUID().toString();
    var deviceSession = DeviceSessionFixtures.buildDeviceSession(messageId);
    var deviceSessionAsJson = objectMapper.writeValueAsString(deviceSession);

    var expectedCommitMessage =
        CommitMessage.builder()
            .messageId(messageId)
            .isSuccessfully(true)
            .timestamp(DeviceSessionFixtures.getBaseTime())
            .build();

    // WHEN && THEN

    execute
        .thenApply(
            websocketSession -> {
              try {
                websocketSession.sendMessage(new TextMessage(deviceSessionAsJson));
                log.info("Message is sent: {}", deviceSessionAsJson);
              } catch (IOException ex) {
                log.error("Message is not sent: {}", ex);
                throw new UncheckedIOException(ex);
              }
              return websocketSession;
            })
        .thenApply(
            websocketSession -> {
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
                        assertEquals(deviceSessionAsJson, records.iterator().next().value());
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
