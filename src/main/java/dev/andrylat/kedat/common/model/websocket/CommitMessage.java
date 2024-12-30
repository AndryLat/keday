package dev.andrylat.kedat.common.model.websocket;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.andrylat.kedat.common.InstantConstants;
import java.time.Instant;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class CommitMessage {
  private String messageId;
  private Boolean isSuccessfully;

  @JsonFormat(
      pattern = InstantConstants.DATE_PATTERN,
      timezone = InstantConstants.DEFAULT_TIME_ZONE)
  private Instant timestamp;
}
