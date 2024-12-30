package dev.andrylat.kedat.common.model.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class NotificationSettings {
  private boolean enableNotifications;
  private boolean muteDuringGameplay;
}
