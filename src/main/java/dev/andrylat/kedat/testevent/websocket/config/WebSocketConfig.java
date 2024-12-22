package dev.andrylat.kedat.testevent.websocket.config;

import dev.andrylat.kedat.common.kafka.producer.KafkaProducerService;
import dev.andrylat.kedat.testevent.model.TestData;
import dev.andrylat.kedat.testevent.websocket.SocketHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  private final KafkaProducerService<TestData> producerService;

  

  public WebSocketConfig(KafkaProducerService<TestData> producerService) {
    this.producerService = producerService;
}



public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(new SocketHandler(producerService), "/test");
  }
}
