package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.users.User;
import co.com.bancolombia.model.users.gateway.DirectoryActiveRepository;
import co.com.bancolombia.model.users.gateway.UserRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UserUseCase {
  private final DirectoryActiveRepository directoryActiveRepository;
  private final UserRepository userRepository;

  public Mono<User> saveUser(int id) {
    return directoryActiveRepository
        .getUserById(id)
        .flatMap(userRepository::save);
  }
}
