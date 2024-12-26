package dev.andrylat.kedat.testevent.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.testevent.model.TestAck;
import dev.andrylat.kedat.testevent.model.TestData;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@AllArgsConstructor
@Component
public class TestDataMessageSocketHandler extends TextWebSocketHandler {
  private static final String TEST_DATA_TOPIC_NAME = "test-data";

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, TestData> kafkaTemplate;

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    log.debug("Received message: {}", message.getPayload());

    var eventSendingResult = sendTestDataEvent(message.getPayload());
    
    var ack = new TestAck(eventSendingResult);
    log.debug("Ack: {}", ack);

    var resultToClient = new TextMessage(objectMapper.writeValueAsString(ack));
    session.sendMessage(resultToClient);
  }

  private boolean sendTestDataEvent(String testDataMessage) {
    try {
      var testData = objectMapper.readValue(testDataMessage, TestData.class);
  
      var kafkaFuture = kafkaTemplate.send(TEST_DATA_TOPIC_NAME, testData);
      kafkaFuture.join();
  
      return !kafkaFuture.isCompletedExceptionally();
      
      } catch (IOException ex) {
        log.error("Error during message deserialization: {}", ex.getMessage(), ex);
        return false;
      }
  }
}
