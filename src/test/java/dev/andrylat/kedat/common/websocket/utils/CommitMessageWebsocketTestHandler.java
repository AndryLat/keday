package dev.andrylat.kedat.common.websocket.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.common.model.websocket.CommitMessage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommitMessageWebsocketTestHandler extends TextWebSocketHandler {
  private final ObjectMapper objectMapper;

  @Getter private final List<CommitMessage> commitMessages = new ArrayList<>();

  @Getter private WebSocketSession currentSession;

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    log.info("Received commit message: {}", message.getPayload());

    var commitMessage = objectMapper.readValue(message.getPayload(), CommitMessage.class);
    commitMessages.add(commitMessage);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    currentSession = session;

    log.info("Connection is established. Session ID: {}", session.getId());
  }
}
