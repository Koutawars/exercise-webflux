package co.com.bancolombia;

import co.com.bancolombia.api.dto.CreateUserDto;
import co.com.bancolombia.model.users.User;
import co.com.bancolombia.model.users.gateway.DirectoryActiveRepository;
import co.com.bancolombia.model.users.gateway.UserMessagePublisher;
import co.com.bancolombia.model.users.gateway.UserRepository;
import co.com.bancolombia.sqs.listener.SQSListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.autoconfigure.exclude=io.awspring.cloud.autoconfigure.sqs.SqsAutoConfiguration,io.awspring.cloud.autoconfigure.dynamodb.DynamoDbAutoConfiguration,io.awspring.cloud.autoconfigure.core.AwsAutoConfiguration"
    }
)
@ActiveProfiles("test")
class MainApplicationTest {

    @MockBean
    private SQSListener sqsListener;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DirectoryActiveRepository directoryActiveRepository;

    @MockBean
    private UserMessagePublisher userMessagePublisher;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void contextLoads() {
        // Test that the application context loads successfully
    }

    @Test
    void shouldCreateUserWithMockedInfrastructure() {
        CreateUserDto createUserDto = CreateUserDto.builder().id(1).build();
        User user = User.builder().id(1).build();
        
        when(userRepository.findById(anyInt())).thenReturn(Mono.empty());
        when(directoryActiveRepository.getUserById(anyInt())).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userMessagePublisher.publishUserCreated(any(User.class))).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void shouldGetAllUsersWithMockedInfrastructure() {
        when(userRepository.findAll()).thenReturn(Flux.empty());
        
        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Object.class);
    }

    @Test
    void shouldHandleUserNotFoundWithMockedInfrastructure() {
        when(userRepository.findById(999)).thenReturn(Mono.empty());
        
        webTestClient.get()
                .uri("/api/users/999")
                .exchange()
                .expectStatus().isNotFound();
    }
}