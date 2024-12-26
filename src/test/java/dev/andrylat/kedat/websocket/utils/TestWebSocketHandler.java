package dev.andrylat.kedat.websocket.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class TestWebSocketHandler extends TextWebSocketHandler {

  @Getter private WebSocketSession currentSession;
  @Getter private String lastMessage;

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    log.info("Handling new text message");

    lastMessage = message.getPayload();
    log.info("Received message:{}", lastMessage);
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    currentSession = session;
    log.info("Connection is established.");
  }
}
