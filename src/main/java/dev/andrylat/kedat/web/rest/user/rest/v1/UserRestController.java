package dev.andrylat.kedat.web.rest.user.rest.v1;

import dev.andrylat.kedat.common.model.user.User;
import dev.andrylat.kedat.web.rest.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserRestController {
  private final UserService userService;

  @PostMapping
  @ResponseStatus(value = HttpStatus.CREATED)
  void add(@RequestBody User newUser) {
    userService.produce(newUser);
  }

  @PutMapping("/{userId}")
  @ResponseStatus(value = HttpStatus.ACCEPTED)
  void update(@PathVariable String userId, @RequestBody User user) {
    userService.produce(user);
  }

  @DeleteMapping("/{userId}")
  @ResponseStatus(value = HttpStatus.OK)
  void remove(@PathVariable String userId) {
    userService.remove(userId);
  }
}
