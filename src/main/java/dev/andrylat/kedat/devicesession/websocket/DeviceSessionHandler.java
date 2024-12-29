package dev.andrylat.kedat.devicesession.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.common.model.CommitMessage;
import dev.andrylat.kedat.common.model.device.DeviceSession;
import dev.andrylat.kedat.common.service.DateTimeService;
import dev.andrylat.kedat.devicesession.service.DeviceSessionService;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@AllArgsConstructor
@Component
public class DeviceSessionHandler extends TextWebSocketHandler {
  public static final String DEVICE_SESSION_ENDPOINT = "/ws/device/session";

  private final ObjectMapper objectMapper;
  private final DateTimeService dateTimeService;
  private final DeviceSessionService deviceSessionService;

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    log.debug("Connection is closed for web socket session {}" + session.getId());
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    log.debug("Connection is opened for web socket session {}", session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    log.trace("Received message from session {}: {}", session.getId(), message.getPayload());

    var deviceSession = objectMapper.readValue(message.getPayload(), DeviceSession.class);
    deviceSessionService
        .sendSession(deviceSession)
        .handleAsync((result, ex) -> processResult(session, result, ex));
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    log.error("WebSocker transport error is caught. Session ID: {}.", session.getId(), exception);
  }

  private SendResult<String, DeviceSession> processResult(
      WebSocketSession session, SendResult<String, DeviceSession> result, Throwable throwable) {
    var deviceSession = result.getProducerRecord().value();
    log.error("Processing result for message {}", deviceSession.getMessageId());

    boolean sendingResult = true;
    if (throwable != null) {
      log.error(
          "Message was not sent to Kafka. Message ID: {}. Reason: {}.",
          deviceSession.getMessageId(),
          throwable.getMessage(),
          throwable);
      sendingResult = false;
    }

    log.error(
        "Message {} is sent to Kafka  result: {}", deviceSession.getMessageId(), sendingResult);

    var commitMessage =
        CommitMessage.builder()
            .messageId(deviceSession.getMessageId())
            .timestamp(dateTimeService.now())
            .isSuccessfully(sendingResult)
            .build();

    try {
      var commitMessageAsJson = objectMapper.writeValueAsString(commitMessage);
      session.sendMessage(new TextMessage(commitMessageAsJson));
      log.error("Commit message is sent back. Message: {}", commitMessageAsJson);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return result;
  }
}
// TODO think about error handling here
