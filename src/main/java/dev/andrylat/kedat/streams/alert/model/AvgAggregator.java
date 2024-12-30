package dev.andrylat.kedat.streams.alert.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@ToString
@Jacksonized
public class AvgAggregator {
  private double sum;
  private int count;

  public AvgAggregator(double sum, int count) {
    this.sum = sum;
    this.count = count;
  }

  public AvgAggregator add(double value) {
    this.sum += value;
    this.count += 1;
    return this;
  }
}
