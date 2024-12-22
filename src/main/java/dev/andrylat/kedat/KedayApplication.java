package dev.andrylat.kedat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class KedayApplication {

  public static void main(String[] args) {
    SpringApplication.run(KedayApplication.class, args);
  }
}
