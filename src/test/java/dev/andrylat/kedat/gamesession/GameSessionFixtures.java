package dev.andrylat.kedat.gamesession;

import dev.andrylat.kedat.common.Constants;
import dev.andrylat.kedat.common.model.game.GameSession;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GameSessionFixtures {
  public static final String DEVICE_UUID = "cccccccc-d9e9-41f8-ad29-ebf907121312";
  public static final String GAME_UUID = "0000bbbb-9422-42f9-bd6f-0944c616683d";
  public static final String SESSION_UUID = "2222cccc-32c2-4500-85ee-2a6a9c903363";

  public static GameSession buildGameSessionStarted(String messageId) {
    return GameSession.builder()
        .messageId(messageId)
        .sessionId(SESSION_UUID)
        .gameId(GAME_UUID)
        .deviceId(DEVICE_UUID)
        .startTime(getBaseTime())
        .build();
  }

  public static GameSession buildGameSessionFinished(String messageId) {
    return GameSession.builder()
        .messageId(messageId)
        .sessionId(SESSION_UUID)
        .gameId(GAME_UUID)
        .deviceId(DEVICE_UUID)
        .endTime(getTimePlusNSeconds(5))
        .reason("crashed")
        .reasonDetails("Unsupported CPU architecture")
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
