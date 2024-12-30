package dev.andrylat.kedat.streams.alert.serdes;

import dev.andrylat.kedat.common.model.alert.Alert;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class AlertSerDes extends Serdes.WrapperSerde<Alert> {
  public AlertSerDes() {
    super(new JsonSerializer<>(), new JsonDeserializer<>(Alert.class));
  }
}
