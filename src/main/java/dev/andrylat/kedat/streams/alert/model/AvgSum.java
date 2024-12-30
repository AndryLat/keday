package dev.andrylat.kedat.streams.alert.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class AvgSum {
  private final String deviceId;
  private final Double avgValue;
}
