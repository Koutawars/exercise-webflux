package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
  private final UserUseCase userUseCase;

  public Mono<ServerResponse> listenPOSTCreateUser(ServerRequest serverRequest) {
    return serverRequest.bodyToMono(CreateUserDto.class)
        .flatMap(body -> userUseCase.saveById(body.getId()))
        .flatMap(user -> ServerResponse.ok().bodyValue(user));
  }

  public Mono<ServerResponse> listenGETUserById(ServerRequest serverRequest) {
    return userUseCase.getUserById(Integer.parseInt(serverRequest.pathVariable("id")))
        .flatMap(user -> ServerResponse.ok().bodyValue(user));
  }

  public Mono<ServerResponse> listenGETAllUsers(ServerRequest serverRequest) {
    return serverRequest.queryParam("name")
        .map(name -> userUseCase.getUsersByName(name)
            .collectList()
            .flatMap(users -> ServerResponse.ok().bodyValue(users)))
        .orElse(userUseCase.getAllUsers()
            .collectList()
            .flatMap(users -> ServerResponse.ok().bodyValue(users)));
  }
}
