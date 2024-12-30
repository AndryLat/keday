package dev.andrylat.kedat.streams.alert.mapper;

import dev.andrylat.kedat.common.model.alert.Alert;
import dev.andrylat.kedat.common.model.alert.Target;
import dev.andrylat.kedat.common.service.DateTimeService;
import dev.andrylat.kedat.streams.alert.model.AvgSum;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AvgSumToAlertMapper
    implements KeyValueMapper<String, AvgSum, KeyValue<String, Alert>> {
  private final DateTimeService dateTimeService;

  @Override
  public KeyValue<String, Alert> apply(String readOnlyKey, AvgSum value) {
    var alert =
        Alert.builder()
            .timestamp(dateTimeService.now())
            .priority("medium")
            .additionalInfo("Temperature of CPU is high during last 5 minutes")
            .alertType("hardware_overheating")
            .target(Target.builder().deviceId(value.getDeviceId()).build())
            .build();

    var newKey = UUID.randomUUID().toString();

    return KeyValue.pair(newKey, alert);
  }
}
