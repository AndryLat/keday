package dev.andrylat.kedat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.testevent.model.TestAck;
import dev.andrylat.kedat.testevent.model.TestData;
import dev.andrylat.kedat.websocket.utils.TestWebSocketHandler;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
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
    webEnvironment = WebEnvironment.DEFINED_PORT,
    classes = {
      dev.andrylat.kedat.KedayApplication.class,
      dev.andrylat.kedat.testevent.websocket.config.WebSocketConfig.class
    })
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = "test-data", kraft = true)
public class WebsocketComponentTest {

  @LocalServerPort private int port;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Test
  void shouldReceiveCommitAckWhenSuccessfullExecution() throws IOException {
    // given
    log.error("KAFKA CLUSTE URL {}", embeddedKafkaBroker.getBrokersAsString());
    Map<String, Object> configs =
        new HashMap<String, Object>(
            KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
    var consumer =
        new DefaultKafkaConsumerFactory<String, String>(
                configs, new StringDeserializer(), new StringDeserializer())
            .createConsumer();
    consumer.subscribe(Collections.singleton("test-data"));

    var webSocketClient = new StandardWebSocketClient();
    var webSocketHandler = new TestWebSocketHandler();
    var execute = webSocketClient.execute(webSocketHandler, "ws://localhost:" + port + "/test", "");

    var testData = new TestData("some string", true, 100);
    var testDataAsJson = new ObjectMapper().writeValueAsString(testData);

    var expectedAck = new TestAck(true);

    // when && then

    execute
        .thenAccept(
            websocketSession -> {
              try {
                websocketSession.sendMessage(new TextMessage(testDataAsJson));
              } catch (IOException ex) {
                throw new UncheckedIOException(ex);
              }
            })
        .thenRun(
            () -> {
              Awaitility.await()
                  .atMost(5, TimeUnit.SECONDS)
                  .until(
                      () -> {
                        var lastMessage = webSocketHandler.getLastMessage();

                        log.error("lastMessage: {}", lastMessage);
                        System.out.println("last message: " + lastMessage);

                        TestAck actualAck = null;
                        try {
                          actualAck = new ObjectMapper().readValue(lastMessage, TestAck.class);
                        } catch (JsonProcessingException | IllegalArgumentException e) {
                          return false;
                        }

                        assertEquals(expectedAck, actualAck);

                        ConsumerRecords<String, String> records =
                            consumer.poll(Duration.of(10, ChronoUnit.SECONDS));

                        assertEquals(1, records.count());
                        assertEquals(testDataAsJson, records.iterator().next().value());
                        return true;
                      });
            })
        .join();
  }
}

// TODO create test config and move${spring.embedded.kafka.brokers} to test
// config. Use kafka container in general config
