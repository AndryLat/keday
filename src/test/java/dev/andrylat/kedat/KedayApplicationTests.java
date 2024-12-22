package dev.andrylat.kedat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;

@Slf4j
@SpringBootTest
class KedayApplicationTests {

	@Test
	void contextLoads() {
		log.info("Context ran successfully");
	}
}
