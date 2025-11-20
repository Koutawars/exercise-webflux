package co.com.bancolombia.consumer;

import co.com.bancolombia.consumer.response.UserByIdResponse;
import co.com.bancolombia.model.users.User;
import co.com.bancolombia.model.users.gateway.DirectoryActiveRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class ReqresRepository implements DirectoryActiveRepository {
  private final WebClient client;

  public ReqresRepository(WebClient client) {
    this.client = client;
  }

  @Override
  @CircuitBreaker(name = "getUserById")
  public Mono<User> getUserById(int id) {
    return client
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/users/{id}")
            .build(id)
        )
        .retrieve()
        .bodyToMono(UserByIdResponse.class)
        .mapNotNull(UserByIdResponse::getData)
        .map(response -> User.builder()
            .id(response.getId())
            .email(response.getEmail())
            .firstName(response.getFirstName())
            .lastName(response.getLastName())
            .avatar(response.getAvatar())
            .build())
        .onErrorResume(WebClientResponseException.NotFound.class, ex -> Mono.empty());
  }
}
