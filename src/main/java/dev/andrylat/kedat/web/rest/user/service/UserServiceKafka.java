package dev.andrylat.kedat.web.rest.user.service;

import dev.andrylat.kedat.common.exception.KafkaMessageIsNotSentException;
import dev.andrylat.kedat.common.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceKafka implements UserService {
  private final String usersTopicName;
  private final KafkaTemplate<String, User> kafkaTemplate;

  public UserServiceKafka(
      @Value("${kafka.topics.users.name}") String usersTopicName,
      KafkaTemplate<String, User> kafkaTemplate) {
    this.usersTopicName = usersTopicName;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Override
  public void produce(User user) {
    kafkaTemplate
        .send(usersTopicName, user.getUserId(), user)
        .handle(
            (result, throwable) -> {
              if (throwable != null) {
                throw new KafkaMessageIsNotSentException(
                    "User with ID " + user.getUserId() + " is not processed.", throwable);
              }
              return result;
            });
  }

  @Override
  public void remove(String userId) {
    kafkaTemplate.send(usersTopicName, userId, null);
  }
}
