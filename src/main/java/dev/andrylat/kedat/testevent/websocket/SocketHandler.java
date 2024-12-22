package dev.andrylat.kedat.testevent.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.common.kafka.producer.KafkaProducerService;
import dev.andrylat.kedat.testevent.model.TestAck;
import dev.andrylat.kedat.testevent.model.TestData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@AllArgsConstructor
public class SocketHandler extends TextWebSocketHandler {
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final KafkaProducerService<TestData> producerService;

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    log.debug("Received message: {}", message.getPayload());
    var testData = new ObjectMapper().readValue(message.getPayload(), TestData.class);

    SendResult<String, TestData> result = producerService.sendEvent("test-data", testData).join();

    var ack = new TestAck(true);
    var resultToClient = new TextMessage(objectMapper.writeValueAsString(ack));
    session.sendMessage(resultToClient);
  }
}
