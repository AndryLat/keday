package dev.andrylat.kedat.game.service;

import dev.andrylat.kedat.common.exception.KafkaMessageIsNotSentException;
import dev.andrylat.kedat.common.model.game.Game;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameServiceKafka implements GameService {
  private final String gamesTopicName;
  private final KafkaTemplate<String, Game> kafkaTemplate;

  public GameServiceKafka(
      @Value("${kafka.topics.games.name}") String gamesTopicName,
      KafkaTemplate<String, Game> kafkaTemplate) {
    this.gamesTopicName = gamesTopicName;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void produce(Game game) {
    kafkaTemplate
        .send(gamesTopicName, game.getGameId(), game)
        .handle(
            (result, throwable) -> {
              if (throwable != null) {
                throw new KafkaMessageIsNotSentException(
                    "Game with ID " + game.getGameId() + " is not processed.", throwable);
              }
              return result;
            });
  }

  @Override
  public void remove(String gameId) {
    kafkaTemplate.send(gamesTopicName, gameId, null);
  }
}
