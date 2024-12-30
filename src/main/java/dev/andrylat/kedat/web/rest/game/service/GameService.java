package dev.andrylat.kedat.web.rest.game.service;

import dev.andrylat.kedat.common.model.game.Game;

public interface GameService {
  void produce(Game game);

  void remove(String gameId);
}
