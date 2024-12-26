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
    webEnvironment = WebEnvironment.DEFINED_PORT,
    classes = {
      dev.andrylat.kedat.KedatApplication.class,
      dev.andrylat.kedat.testevent.websocket.config.WebSocketConfig.class
    })
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = "test-data", kraft = true)
public class WebsocketComponentTest {

  @LocalServerPort private int port;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

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
    consumer.subscribe(Collections.singleton("test-data"));

    // Websocket client configuration
    var webSocketClient = new StandardWebSocketClient();
    var webSocketHandler = new TestWebSocketHandler();
    var execute = webSocketClient.execute(webSocketHandler, "ws://localhost:" + port + "/test", "");

    var testData = new TestData("some string", true, 100);
    var testDataAsJson = new ObjectMapper().writeValueAsString(testData);

    var expectedAck = new TestAck(true);

    // WHEN && THEN

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
              // Every second we are checking if  test is passed and returns true as a result. If
              // not - try again. We do it during 5 seconds after it test will be failed.
              Awaitility.await()
                  .atMost(5, TimeUnit.SECONDS)
                  .until(
                      () -> {
                        var lastMessage = webSocketHandler.getLastMessage();

                        log.error("lastMessage: {}", lastMessage);

                        TestAck actualAck = null;
                        try {
                          actualAck = new ObjectMapper().readValue(lastMessage, TestAck.class);
                        } catch (JsonProcessingException | IllegalArgumentException e) {
                          return false;
                        }

                        assertEquals(expectedAck, actualAck);

                        var records = consumer.poll(Duration.of(10, ChronoUnit.SECONDS));

                        assertEquals(1, records.count());
                        assertEquals(testDataAsJson, records.iterator().next().value());
                        return true;
                      });
            })
        .join();
  }
}
