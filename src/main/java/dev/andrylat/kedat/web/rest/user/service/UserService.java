package dev.andrylat.kedat.web.rest.user.service;

import dev.andrylat.kedat.common.model.user.User;

public interface UserService {
  void produce(User user);

  void remove(String userId);
}
