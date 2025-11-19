package co.com.bancolombia.model.users.gateway;

import co.com.bancolombia.model.users.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
  Mono<User> save(User user);
}
