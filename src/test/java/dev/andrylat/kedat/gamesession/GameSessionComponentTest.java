package dev.andrylat.kedat.gamesession;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.common.model.game.GameSession;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
public class GameSessionComponentTest {
  private static final String GAME_SESSIONS_TOPIC_NAME = "game-sessions";
  private static final String GAME_SESSION_ENDPOINT = "/ws/game/session";

  @LocalServerPort private int port;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired private ObjectMapper objectMapper;
  @Autowired private CommitMessageWebsocketTestHandler commitMessageHandler;

  @BeforeEach
  void clearMessages() {
    commitMessageHandler.getCommitMessages().clear();
  }

  @ParameterizedTest
  @MethodSource("getDifferentGameSessionEvents")
  void shouldReceiveCommitAckWhenSuccessfullExecution(String messageId, GameSession gameSession)
      throws IOException {
    // GIVEN

    var configs =
        new HashMap<String, Object>(
            KafkaTestUtils.consumerProps(
                UUID.randomUUID().toString(), "false", embeddedKafkaBroker));

    var consumer =
        new DefaultKafkaConsumerFactory<String, String>(
                configs, new StringDeserializer(), new StringDeserializer())
            .createConsumer();
    consumer.subscribe(Collections.singleton(GAME_SESSIONS_TOPIC_NAME));

    // Websocket client configuration
    var webSocketClient = new StandardWebSocketClient();
    var execute =
        webSocketClient.execute(
            commitMessageHandler, "ws://localhost:" + port + GAME_SESSION_ENDPOINT, "");

    var gameSessionAsJson = objectMapper.writeValueAsString(gameSession);

    var expectedCommitMessageGame =
        CommitMessage.builder()
            .messageId(messageId)
            .isSuccessfully(true)
            .timestamp(GameSessionFixtures.getBaseTime())
            .build();

    // WHEN && THEN

    execute
        .thenApply(
            websocketSession -> {
              try {
                websocketSession.sendMessage(new TextMessage(gameSessionAsJson));
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

                        var commitMessages = commitMessageHandler.getCommitMessages();

                        assertTrue(commitMessages.contains(expectedCommitMessageGame));

                        var records = consumer.poll(Duration.of(5, ChronoUnit.SECONDS));
                        var isEventFound = new AtomicBoolean();
                        records.forEach(
                            record -> {
                              if (record.value().equals(gameSessionAsJson)) {
                                isEventFound.set(true);
                              }
                            });
                        return isEventFound.get();
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

  private static Stream<Arguments> getDifferentGameSessionEvents() {
    return Stream.of(
        Arguments.of(
            "1d2ca806-f541-429e-9c6f-def5999d8d2b",
            GameSessionFixtures.buildGameSessionStarted("1d2ca806-f541-429e-9c6f-def5999d8d2b")),
        Arguments.of(
            "8ce60d98-79f6-4a8b-a68a-42e1719a0e28",
            GameSessionFixtures.buildGameSessionFinished("8ce60d98-79f6-4a8b-a68a-42e1719a0e28")));
  }
}
