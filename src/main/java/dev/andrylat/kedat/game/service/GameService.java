package dev.andrylat.kedat.game.service;

import dev.andrylat.kedat.common.model.game.Game;

public interface GameService {
  void produce(Game game);

  void remove(String gameId);
}
