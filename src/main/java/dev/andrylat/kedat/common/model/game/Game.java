package dev.andrylat.kedat.common.model.game;

import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Game {
  private String gameId;
  private String name;
  private String genre;
  private List<String> platforms;
  private List<String> gameModes;
  private Boolean isOnline;
  private int downloadSizeMB;
}
