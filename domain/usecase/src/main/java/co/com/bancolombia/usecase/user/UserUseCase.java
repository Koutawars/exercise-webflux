package co.com.bancolombia.usecase.user;

import co.com.bancolombia.model.exception.UserNotFoundException;
import co.com.bancolombia.model.users.User;
import co.com.bancolombia.model.users.gateway.DirectoryActiveRepository;
import co.com.bancolombia.model.users.gateway.UserRepository;
import co.com.bancolombia.model.users.gateway.UserCacheRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class UserUseCase {
  private final DirectoryActiveRepository directoryActiveRepository;
  private final UserRepository userRepository;
  private final UserCacheRepository userCacheRepository;
  private final Logger logger;

  public Mono<User> saveUser(int id) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key("id", id);
      return userRepository
          .findById(id)
          .switchIfEmpty(directoryActiveRepository
              .getUserById(id)
              .switchIfEmpty(Mono.error(new UserNotFoundException()))
              .flatMap(userRepository::save)
          )
          .doOnSubscribe(unused -> logBuilder.info("Saving user"))
          .doOnSuccess(unused -> logBuilder.info("User added"))
          .doOnError(error -> logBuilder.error("Error adding user"));
    });
  }

  public Mono<User> getUserById(int id) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key("id", id);
      return userCacheRepository.findById(id)
          .doOnNext(user -> logBuilder.info("User found in cache"))
          .onErrorResume(error -> {
            logBuilder.info("Cache unavailable, querying database directly");
            return Mono.empty();
          })
          .switchIfEmpty(userRepository.findById(id)
              .switchIfEmpty(Mono.error(new UserNotFoundException()))
              .flatMap(user -> userCacheRepository.save(id, user)
                  .onErrorReturn(user)
                  .doOnNext(u -> logBuilder.info("User cached from database"))
                  .doOnError(e -> logBuilder.info("Failed to cache user, continuing without cache"))))
          .doOnSubscribe(unused -> logBuilder.info("Getting user"))
          .doOnSuccess(unused -> logBuilder.info("User found"))
          .doOnError(error -> logBuilder.error("Error getting user"));
    });
  }

  public Flux<User> getAllUsers() {
    return Flux.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context);
      return userRepository.findAll()
          .doOnSubscribe(unused -> logBuilder.info("Getting all users"))
          .doOnComplete(() -> logBuilder.info("All users found"))
          .doOnError(error -> logBuilder.error("Error getting all users"));
    });
  }

  public Flux<User> getUsersByName(String name) {
    return Flux.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key("name", name);
      return userRepository.findByName(name)
          .doOnSubscribe(unused -> logBuilder.info("Getting users by name"))
          .doOnComplete(() -> logBuilder.info("Users found by name"))
          .doOnError(error -> logBuilder.error("Error getting users by name"));
    });
  }
}
