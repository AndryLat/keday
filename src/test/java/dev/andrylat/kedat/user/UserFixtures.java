package dev.andrylat.kedat.user;

import dev.andrylat.kedat.common.Constants;
import dev.andrylat.kedat.common.model.user.NotificationSettings;
import dev.andrylat.kedat.common.model.user.Preferences;
import dev.andrylat.kedat.common.model.user.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UserFixtures {
  public static final String USER_UUID = "1111aaaa-09f9-446a-bcf8-acc2614cd47d";

  public static String getUserJson() {
    return """
                {
                    "userId": "%s",
                    "username": "testUser",
                    "email": "testEmail@gmail.com",
                    "registrationDate": "2025-01-01 06:00:00.000",
                    "lastLogin": "2025-01-15 06:00:00.000",
                    "preferences": {
                        "language": "UA",
                        "notificationSettings": {
                            "enableNotifications": false,
                            "muteDuringGameplay": true
                        },
                        "theme": "dark"
                    }
                }
                                  """
        .formatted(USER_UUID);
  }

  public static User buildUser() {
    return User.builder()
        .userId(USER_UUID)
        .username("testUser")
        .email("testEmail@gmail.com")
        .registrationDate(getBaseTime())
        .lastLogin(getBaseTime().plus(14, ChronoUnit.DAYS))
        .preferences(
            Preferences.builder()
                .language("UA")
                .theme("dark")
                .notificationSettings(
                    NotificationSettings.builder()
                        .enableNotifications(false)
                        .muteDuringGameplay(true)
                        .build())
                .build())
        .build();
  }

  public static String getUpdatedUserJson() {
    return """
                {
                    "userId": "%s",
                    "username": "newtestUser",
                    "email": "bewtestEmail@gmail.com",
                    "registrationDate": "2025-01-01 06:00:00.000",
                    "lastLogin": "2025-01-18 06:00:00.000",
                    "preferences": {
                        "language": "EN",
                        "notificationSettings": {
                            "enableNotifications": true,
                            "muteDuringGameplay": false
                        },
                        "theme": "light"
                    }
                }
                                  """
        .formatted(USER_UUID);
  }

  public static User buildUpdatedUser() {
    return User.builder()
        .userId(USER_UUID)
        .username("newtestUser")
        .email("bewtestEmail@gmail.com")
        .registrationDate(getBaseTime())
        .lastLogin(getBaseTime().plus(17, ChronoUnit.DAYS))
        .preferences(
            Preferences.builder()
                .language("EN")
                .theme("light")
                .notificationSettings(
                    NotificationSettings.builder()
                        .enableNotifications(true)
                        .muteDuringGameplay(false)
                        .build())
                .build())
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
