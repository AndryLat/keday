package dev.andrylat.kedat.game;

import dev.andrylat.kedat.common.Constants;
import dev.andrylat.kedat.common.model.game.Game;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class GameFixtures {
  public static final String GAME_UUID = "ddddaaaa-b55f-4900-8140-854755b0513a";

  public static String getGameJson() {
    return """
        {
          "gameId": "%s",
          "name": "Game Name",
          "genre": "Action",
          "platforms": ["PC", "Mac"],
          "gameModes": ["Single Player", "Multiplayer"],
          "isOnline": true,
          "downloadSizeMB": 5000
        }
                                          """
        .formatted(GAME_UUID);
  }

  public static Game buildGame() {
    return Game.builder()
        .gameId(GAME_UUID)
        .name("Game Name")
        .genre("Action")
        .platforms(List.of("PC", "Mac"))
        .gameModes(List.of("Single Player", "Multiplayer"))
        .isOnline(true)
        .downloadSizeMB(5000)
        .build();
  }

  public static String getUpdatedGameJson() {
    return """
        {
          "gameId": "%s",
          "name": "Game Name X",
          "genre": "Action | RPG",
          "platforms": ["PC", "Mac"],
          "gameModes": ["Single Player", "Multiplayer", "Online"],
          "isOnline": true,
          "downloadSizeMB": 6443
        }
                          """
        .formatted(GAME_UUID);
  }

  public static Game buildUpdatedGame() {
    return Game.builder()
        .gameId(GAME_UUID)
        .name("Game Name X")
        .genre("Action | RPG")
        .platforms(List.of("PC", "Mac"))
        .gameModes(List.of("Single Player", "Multiplayer", "Online"))
        .isOnline(true)
        .downloadSizeMB(6443)
        .build();
  }

  public static Instant getBaseTime() {
    return Instant.parse(Constants.FIXED_TIME);
  }

  public static Instant getTimePlusNSeconds(int seconds) {
    return getBaseTime().plus(seconds, ChronoUnit.SECONDS);
  }

  public static Instant getTimeMinusNSeconds(int seconds) {
    return getBaseTime().minus(seconds, ChronoUnit.SECONDS);
  }
}
