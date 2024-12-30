package dev.andrylat.kedat.common.model.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Preferences {
  private String language;
  private NotificationSettings notificationSettings;
  private String theme;
}
