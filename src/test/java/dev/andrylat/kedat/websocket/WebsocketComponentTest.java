package dev.andrylat.kedat.websocket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.andrylat.kedat.testevent.model.TestAck;
import dev.andrylat.kedat.testevent.model.TestData;
import dev.andrylat.kedat.websocket.utils.TestWebSocketHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class WebsocketComponentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void shouldReceiveCommitAckWhenSuccessfullExecution() throws IOException {
        // given
        embeddedKafkaBroker.addTopics("test-data");

        Map<String, Object> configs = new HashMap<String, Object>(
                KafkaTestUtils.consumerProps("consumer", "false", embeddedKafkaBroker));
        var consumer = new DefaultKafkaConsumerFactory<String, String>(
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
                                    .untilAsserted(
                                            () -> {
                                                var lastMessage = webSocketHandler.getLastMessage();

                                                TestAck actualAck = null;
                                                try {
                                                    actualAck = new ObjectMapper().readValue(lastMessage,
                                                            TestAck.class);
                                                } catch (JsonProcessingException e) {
                                                    fail();
                                                }

                                                assertEquals(expectedAck, actualAck);

                                                ConsumerRecords<String, String> records = consumer
                                                        .poll(Duration.of(10, ChronoUnit.SECONDS));

                                                assertEquals(1, records.count());
                                                assertEquals(testData, records.iterator().next().value());
                                            });
                        })
                .join();
    }
}
