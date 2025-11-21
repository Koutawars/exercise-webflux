package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.model.users.User;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RouterRest.class, Handler.class, RouterRestIntegrationTest.TestConfig.class})
class RouterRestIntegrationTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        UserUseCase userUseCase() {
            return mock(UserUseCase.class);
        }
    }

    @Autowired
    private RouterFunction<ServerResponse> routerFunction;
    
    private WebTestClient webTestClient;

    @Autowired
    private UserUseCase userUseCase;
    
    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void shouldCreateUser() {
        User mockUser = User.builder()
                .id(1)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .avatar("avatar.jpg")
                .build();

        when(userUseCase.saveById(anyInt())).thenReturn(Mono.just(mockUser));

        CreateUserDto createUserDto = CreateUserDto.builder().id(1).build();

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.email").isEqualTo("test@example.com")
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.lastName").isEqualTo("Doe")
                .jsonPath("$.avatar").isEqualTo("avatar.jpg");
    }

    @Test
    void shouldGetUserById() {
        User mockUser = User.builder()
                .id(1)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .avatar("avatar.jpg")
                .build();

        when(userUseCase.getUserById(1)).thenReturn(Mono.just(mockUser));

        webTestClient.get()
                .uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.email").isEqualTo("test@example.com")
                .jsonPath("$.firstName").isEqualTo("John")
                .jsonPath("$.lastName").isEqualTo("Doe")
                .jsonPath("$.avatar").isEqualTo("avatar.jpg");
    }

    @Test
    void shouldGetAllUsers() {
        List<User> mockUsers = List.of(
                User.builder().id(1).email("user1@example.com").firstName("John").lastName("Doe").build(),
                User.builder().id(2).email("user2@example.com").firstName("Jane").lastName("Smith").build()
        );

        when(userUseCase.getAllUsers()).thenReturn(Flux.fromIterable(mockUsers));

        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].email").isEqualTo("user1@example.com")
                .jsonPath("$[0].firstName").isEqualTo("John")
                .jsonPath("$[0].lastName").isEqualTo("Doe")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].email").isEqualTo("user2@example.com")
                .jsonPath("$[1].firstName").isEqualTo("Jane")
                .jsonPath("$[1].lastName").isEqualTo("Smith");
    }
}