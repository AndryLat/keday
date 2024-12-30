package dev.andrylat.kedat.streams.alert.serdes;

import dev.andrylat.kedat.streams.alert.model.AvgAggregator;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class AggregatorSerDes extends Serdes.WrapperSerde<AvgAggregator> {
  public AggregatorSerDes() {
    super(new JsonSerializer<>(), new JsonDeserializer<>(AvgAggregator.class));
  }
}
