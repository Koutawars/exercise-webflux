package co.com.bancolombia.model.users.gateway;

import co.com.bancolombia.model.users.User;
import reactor.core.publisher.Mono;

public interface UserCacheRepository {
  Mono<User> findById(int id);
  Mono<User> save(int id, User user);
}