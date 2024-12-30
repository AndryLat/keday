package dev.andrylat.kedat.game.rest.v1;

import dev.andrylat.kedat.common.model.game.Game;
import dev.andrylat.kedat.game.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/games")
@AllArgsConstructor
public class GameRestController {
  private final GameService gameService;

  @PostMapping
  @ResponseStatus(value = HttpStatus.CREATED)
  void add(@RequestBody Game newGame) {
    gameService.produce(newGame);
  }

  @PutMapping("/{gameId}")
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  void update(@PathVariable String gameId, @RequestBody Game game) {
    gameService.produce(game);
  }

  @DeleteMapping("/{gameId}")
  @ResponseStatus(value = HttpStatus.OK)
  void remove(@PathVariable String gameId) {
    gameService.remove(gameId);
  }
}
