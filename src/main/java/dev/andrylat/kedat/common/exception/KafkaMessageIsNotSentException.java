package dev.andrylat.kedat.common.exception;

public class KafkaMessageIsNotSentException extends RuntimeException {

  public KafkaMessageIsNotSentException(String message) {
    super(message);
  }

  public KafkaMessageIsNotSentException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
