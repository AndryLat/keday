package dev.andrylat.kedat.gamesession.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.common.model.CommitMessage;
import dev.andrylat.kedat.common.model.game.GameSession;
import dev.andrylat.kedat.common.service.DateTimeService;
import dev.andrylat.kedat.gamesession.service.GameSessionService;
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
public class GameSessionHandler extends TextWebSocketHandler {
  public static final String GAME_SESSION_ENDPOINT = "/ws/game/session";

  private final ObjectMapper objectMapper;
  private final DateTimeService dateTimeService;
  private final GameSessionService gameSessionService;

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

    var gameSession = objectMapper.readValue(message.getPayload(), GameSession.class);
    gameSessionService
        .sendSession(gameSession)
        .handleAsync((result, ex) -> processResult(session, result, ex));
  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    log.error("WebSocker transport error is caught. Session ID: {}.", session.getId(), exception);
  }

  private SendResult<String, GameSession> processResult(
      WebSocketSession session, SendResult<String, GameSession> result, Throwable throwable) {
    var gameSession = result.getProducerRecord().value();
    log.error("Processing result for message {}", gameSession.getMessageId());

    boolean sendingResult = true;
    if (throwable != null) {
      log.error(
          "Message was not sent to Kafka. Message ID: {}. Reason: {}.",
          gameSession.getMessageId(),
          throwable.getMessage(),
          throwable);
      sendingResult = false;
    }

    log.error("Message {} is sent to Kafka  result: {}", gameSession.getMessageId(), sendingResult);

    var commitMessage =
        CommitMessage.builder()
            .messageId(gameSession.getMessageId())
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
