package dev.andrylat.kedat.device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andrylat.kedat.common.model.device.Device;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.awaitility.Awaitility;
import org.awaitility.Durations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;

@Slf4j
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = {
      dev.andrylat.kedat.KedatApplication.class,
      dev.andrylat.kedat.common.config.TestConfig.class
    })
@DirtiesContext
@EmbeddedKafka(kraft = true)
public class DeviceCrudComponentTest {
  private static final String DEVICES_TOPIC_NAME = "devices";

  @LocalServerPort private int port;

  @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

  @Autowired private TestRestTemplate testClient;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldProduceNewDeviceSuccessfully() {
    // GIVEN
    var consumer = prepareConsumer();

    var deviceData = DeviceFixtures.getDeviceJson();
    var request =
        RequestEntity.post("/api/v1/devices", new Object[] {})
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(deviceData);

    var expectedDevice = DeviceFixtures.buildDevice();

    // WHEN
    var response = testClient.exchange(request, String.class);

    // THEN
    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    var records = consumer.poll(Durations.ONE_SECOND);

    var foundRecord = catchKafkaMessage(expectedDevice, records);

    assertEquals(expectedDevice.getDeviceId(), foundRecord.key());
  }

  @Test
  void shouldProduceUpdatedUserSuccessfully() {
    // GIVEN
    // Creation of embedded Kafka consumer for checking results of message production in main code.
    var consumer = prepareConsumer();

    var userData = DeviceFixtures.getUpdatedDeviceJson();
    var request =
        RequestEntity.put("/api/v1/devices/" + DeviceFixtures.DEVICE_UUID, new Object[] {})
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .body(userData);

    var expectedDevice = DeviceFixtures.buildUpdatedDevice();

    // WHEN
    var response = testClient.exchange(request, String.class);

    // THEN
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

    var records = consumer.poll(Durations.ONE_SECOND);

    var foundRecord = catchKafkaMessage(expectedDevice, records);

    assertEquals(expectedDevice.getDeviceId(), foundRecord.key());
  }

  @Test
  void shouldProduceTombstoneSuccessfully() {
    // GIVEN
    var consumer = prepareConsumer();

    // WHEN
    testClient.delete("/api/v1/devices/" + DeviceFixtures.DEVICE_UUID, new Object[] {});

    // THEN
    var records = consumer.poll(Durations.ONE_SECOND);

    var foundRecord = catchTombstone(DeviceFixtures.DEVICE_UUID, records);
    assertEquals(DeviceFixtures.DEVICE_UUID, foundRecord.key());
  }

  private ConsumerRecord<String, String> catchKafkaMessage(
      Device expectedDevice, ConsumerRecords<String, String> records) {
    var foundRecord = new AtomicReference<ConsumerRecord<String, String>>();
    Awaitility.await()
        .atMost(Durations.FIVE_SECONDS)
        .until(
            () -> {
              records.forEach(
                  record -> {
                    try {
                      if (record.value() == null) {
                        return;
                      }
                      if (objectMapper
                          .readValue(record.value(), Device.class)
                          .equals(expectedDevice)) {
                        foundRecord.set(record);
                      }
                    } catch (IOException e) {
                      fail();
                    }
                  });
              return foundRecord.get() != null;
            });
    return foundRecord.get();
  }

  private ConsumerRecord<String, String> catchTombstone(
      String deviceId, ConsumerRecords<String, String> records) {

    var foundRecord = new AtomicReference<ConsumerRecord<String, String>>();
    Awaitility.await()
        .atMost(Durations.FIVE_SECONDS)
        .until(
            () -> {
              records.forEach(
                  record -> {
                    if (record.key().equals(deviceId) && record.value() == null) {
                      foundRecord.set(record);
                    }
                  });
              return foundRecord.get() != null;
            });
    return foundRecord.get();
  }

  private Consumer<String, String> prepareConsumer() {
    var configs =
        new HashMap<String, Object>(
            KafkaTestUtils.consumerProps(
                UUID.randomUUID().toString(), "false", embeddedKafkaBroker));

    var consumer =
        new DefaultKafkaConsumerFactory<String, String>(
                configs, new StringDeserializer(), new StringDeserializer())
            .createConsumer();
    consumer.subscribe(Collections.singleton(DEVICES_TOPIC_NAME));

    return consumer;
  }
}
