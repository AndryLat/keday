package dev.andrylat.kedat.streams.alert;

import dev.andrylat.kedat.common.model.metric.DeviceMetric;
import dev.andrylat.kedat.streams.alert.mapper.AvgSumToAlertMapper;
import dev.andrylat.kedat.streams.alert.model.AvgAggregator;
import dev.andrylat.kedat.streams.alert.model.AvgSum;
import dev.andrylat.kedat.streams.alert.serdes.AggregatorSerDes;
import dev.andrylat.kedat.streams.alert.serdes.AlertSerDes;
import dev.andrylat.kedat.streams.alert.serdes.DeviceMetricSerdes;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AlertsKafkaStream {
    private final String deviceMetricTopicName;
    private final String alertsTopicName;
    private final int cpuMediumAlertSeconds;
    private final int cpuMediumAlertDegrees;

    private final DeviceMetricSerdes deviceMetricSerdes;
    private final AggregatorSerDes aggregatorSerdes;
    private final AlertSerDes alertSerdes;

    private final AvgSumToAlertMapper mapper;

    public AlertsKafkaStream(
            @Value("${kafka.topics.device-metrics.name}") String deviceMetricTopicName,
            @Value("${kafka.topics.alerts.name}") String alertsTopicName,
            @Value("${alerts.cpu-medium.seconds:300}") Integer cpuMediumAlertSeconds,
            @Value("${alerts.cpu-medium.degrees:90}") Integer cpuMediumAlertDegrees,
            DeviceMetricSerdes deviceMetricSerdes,
            AggregatorSerDes aggregatorSerdes,
            AlertSerDes alertSerdes,
            AvgSumToAlertMapper mapper) {
        this.deviceMetricTopicName = deviceMetricTopicName;
        this.alertsTopicName = alertsTopicName;
        this.deviceMetricSerdes = deviceMetricSerdes;
        this.mapper = mapper;
        this.aggregatorSerdes = aggregatorSerdes;
        this.alertSerdes = alertSerdes;
        this.cpuMediumAlertSeconds = cpuMediumAlertSeconds;
        this.cpuMediumAlertDegrees = cpuMediumAlertDegrees;
    }

    @Bean
    public KStream<String, DeviceMetric> kStream(StreamsBuilder kStreamBuilder) {

        KStream<String, DeviceMetric> deviceMetricsStream = kStreamBuilder.stream(
                deviceMetricTopicName, Consumed.with(Serdes.String(), deviceMetricSerdes));

        buildCpuTemperatureMediumAlert(deviceMetricsStream);

        return deviceMetricsStream;
    }

    private void buildCpuTemperatureMediumAlert(KStream<String, DeviceMetric> deviceMetricsStream) {
        deviceMetricsStream
                .groupByKey()
                .windowedBy(
                        TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(cpuMediumAlertSeconds)))
                .aggregate(
                        () -> new AvgAggregator(0, 0),
                        (_, value, aggregator) -> {
                            return aggregator.add(value.getCpu().getTemperatureCelsius());
                        },
                        Materialized.<String, AvgAggregator, WindowStore<org.apache.kafka.common.utils.Bytes, byte[]>>as(
                                "avg-cpu-5-min-storage")
                                .withValueSerde(aggregatorSerdes))
                .suppress(
                        Suppressed.untilTimeLimit(Duration.ofSeconds(cpuMediumAlertSeconds),
                                Suppressed.BufferConfig.unbounded()))
                .toStream()
                .map((windowedKey, aggregator) -> KeyValue.pair(windowedKey.key(), aggregator))
                .mapValues((readOnlyKey, value) -> new AvgSum(readOnlyKey,
                        value.getCount() == 0 ? 0.0 : value.getSum() / value.getCount()))
                .filter((_, value) -> value.getAvgValue() > cpuMediumAlertDegrees)
                .map(mapper)
                .to(alertsTopicName, Produced.with(Serdes.String(), alertSerdes));
    }
}
