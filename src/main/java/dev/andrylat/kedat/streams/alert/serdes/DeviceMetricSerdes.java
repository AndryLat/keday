package dev.andrylat.kedat.streams.alert.serdes;

import dev.andrylat.kedat.common.model.metric.DeviceMetric;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class DeviceMetricSerdes extends Serdes.WrapperSerde<DeviceMetric> {
  public DeviceMetricSerdes() {
    super(new JsonSerializer<>(), new JsonDeserializer<>(DeviceMetric.class));
  }
}
