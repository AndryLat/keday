package dev.andrylat.kedat.gamesession.service;

import dev.andrylat.kedat.common.model.game.GameSession;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class GameSessionServiceKafka implements GameSessionService {

  private final String gameSessionTopicName;
  private final KafkaTemplate<String, GameSession> kafkaTemplate;

  public GameSessionServiceKafka(
      @Value("${kafka.topics.game-sessions.name}") String gameSessionsTopicName,
      KafkaTemplate<String, GameSession> kafkaTemplate) {
    this.gameSessionTopicName = gameSessionsTopicName;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public CompletableFuture<SendResult<String, GameSession>> sendSession(GameSession gameSession) {
    return kafkaTemplate.send(gameSessionTopicName, gameSession);
  }
}
