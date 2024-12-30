package dev.andrylat.kedat.user.service;

import dev.andrylat.kedat.common.model.user.User;

public interface UserService {
  void produce(User user);

  void remove(String userId);
}
