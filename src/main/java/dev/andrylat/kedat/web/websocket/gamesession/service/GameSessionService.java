package dev.andrylat.kedat.web.websocket.gamesession.service;

import dev.andrylat.kedat.common.model.game.GameSession;
import java.util.concurrent.CompletableFuture;
import org.springframework.kafka.support.SendResult;

public interface GameSessionService {
  CompletableFuture<SendResult<String, GameSession>> sendSession(GameSession gameSession);
}
