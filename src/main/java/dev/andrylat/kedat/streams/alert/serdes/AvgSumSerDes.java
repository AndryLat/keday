package dev.andrylat.kedat.streams.alert.serdes;

import dev.andrylat.kedat.streams.alert.model.AvgSum;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

@Component
public class AvgSumSerDes extends Serdes.WrapperSerde<AvgSum> {

  public AvgSumSerDes() {
    super(new JsonSerializer<>(), new JsonDeserializer<>(AvgSum.class));
  }
}
