package dev.andrylat.kedat.web.websocket;

import dev.andrylat.kedat.web.websocket.devicesession.websocket.DeviceSessionHandler;
import dev.andrylat.kedat.web.websocket.gamesession.websocket.GameSessionHandler;
import dev.andrylat.kedat.web.websocket.metric.websocket.DeviceMetricHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@AllArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
  private final DeviceMetricHandler deviceMetricHandler;
  private final DeviceSessionHandler deviceSessionHandler;
  private final GameSessionHandler gameSessionHandler;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(deviceMetricHandler, DeviceMetricHandler.DEVICE_METRIC_ENDPOINT);
    registry.addHandler(deviceSessionHandler, DeviceSessionHandler.DEVICE_SESSION_ENDPOINT);
    registry.addHandler(gameSessionHandler, GameSessionHandler.GAME_SESSION_ENDPOINT);
  }
}
