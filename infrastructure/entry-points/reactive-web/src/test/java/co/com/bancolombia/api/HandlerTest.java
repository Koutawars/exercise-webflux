package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.model.users.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

  @Mock
  private UserUseCase userUseCase;

  private Handler handler;

  @BeforeEach
  void setUp() {
    handler = new Handler(userUseCase);
  }

  @Test
  void listenPOSTCreateUser_ShouldReturnUser() {
    CreateUserDto createUserDto = CreateUserDto.builder().id(1).build();
    User user = User.builder().id(1).email("test@test.com").build();

    ServerRequest request = MockServerRequest.builder()
        .body(Mono.just(createUserDto));

    when(userUseCase.saveById(anyInt())).thenReturn(Mono.just(user));

    Mono<ServerResponse> response = handler.listenPOSTCreateUser(request);

    StepVerifier.create(response)
        .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenGETUserById_ShouldReturnUser() {
    User user = User.builder().id(1).email("test@test.com").build();

    ServerRequest request = MockServerRequest.builder()
        .pathVariable("id", "1")
        .build();

    when(userUseCase.getUserById(anyInt())).thenReturn(Mono.just(user));

    Mono<ServerResponse> response = handler.listenGETUserById(request);

    StepVerifier.create(response)
        .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenGETAllUsers_WithoutNameParam_ShouldReturnAllUsers() {
    User user1 = User.builder().id(1).email("test1@test.com").build();
    User user2 = User.builder().id(2).email("test2@test.com").build();

    ServerRequest request = MockServerRequest.builder().build();

    when(userUseCase.getAllUsers()).thenReturn(Flux.just(user1, user2));

    Mono<ServerResponse> response = handler.listenGETAllUsers(request);

    StepVerifier.create(response)
        .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
        .verifyComplete();
  }
}