package co.com.bancolombia.redis.template;

import co.com.bancolombia.model.users.User;
import co.com.bancolombia.model.users.gateway.UserCacheRepository;
import co.com.bancolombia.redis.template.helper.ReactiveTemplateAdapterOperations;
import co.com.bancolombia.model.utils.Logger;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
public class RedisUserAdapter extends ReactiveTemplateAdapterOperations<User, String, User>
    implements UserCacheRepository {

  private final Logger logger;

  public RedisUserAdapter(ReactiveRedisConnectionFactory connectionFactory, ObjectMapper mapper, Logger logger) {
    super(connectionFactory, mapper, d -> mapper.map(d, User.class));
    this.logger = logger;
  }

  @Override
  public Mono<User> findById(int id) {
    String key = "user:" + id;
    return Mono.deferContextual(context -> {
      var logBuilder = logger.with(context).key("key", key);
      return super.findById(key)
          .doOnSubscribe(s -> logBuilder.info("Looking for key in Redis"))
          .doOnNext(user -> logBuilder.key("userId", user.getId()).info("Found user in cache"))
          .doOnError(error -> logBuilder.key("error", error.getMessage()).error("Error finding user in cache"))
          .doFinally(signal -> logBuilder.key("signal", signal.toString()).info("Find operation completed"));
    });
  }

  @Override
  public Mono<User> save(int id, User user) {
    String key = "user:" + id;
    return Mono.deferContextual(context -> {
      var logBuilder = logger.with(context).key("key", key).key("userId", user.getId());
      return super.save(key, user, 300000)
          .doOnSubscribe(s -> logBuilder.info("Saving user to Redis"))
          .doOnSuccess(u -> logBuilder.info("User saved successfully to cache"))
          .doOnError(error -> logBuilder.key("error", error.getMessage()).error("Error saving user to cache"));
    });
  }
}
