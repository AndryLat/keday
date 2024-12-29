package dev.andrylat.kedat.gamesession.service;

import dev.andrylat.kedat.common.model.game.GameSession;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

public interface GameSessionService {
  CompletableFuture<SendResult<String, GameSession>> sendSession(GameSession gameSession);
}
