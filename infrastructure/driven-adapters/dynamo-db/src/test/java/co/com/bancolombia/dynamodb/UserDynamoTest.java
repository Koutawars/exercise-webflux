package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.entity.UserEntity;
import co.com.bancolombia.model.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDynamoTest {

    @Mock
    private DynamoDbEnhancedAsyncClient connectionFactory;
    
    @Mock
    private DynamoDbAsyncTable<UserEntity> userTable;

    private UserDynamo userDynamo;
    private User testUser;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        when(connectionFactory.table(eq("test-table"), any(TableSchema.class)))
            .thenReturn(userTable);
        
        userDynamo = new UserDynamo(connectionFactory, "test-table");
        
        testUser = User.builder()
            .id(1)
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .avatar("avatar.jpg")
            .build();
            
        testUserEntity = UserEntity.builder()
            .id(1)
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .avatar("avatar.jpg")
            .build();
    }

    @Test
    void save_ShouldReturnUser_WhenSuccessful() {
        when(userTable.putItem(any(UserEntity.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        Mono<User> result = userDynamo.save(testUser);

        StepVerifier.create(result)
            .expectNext(testUser)
            .verifyComplete();
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userTable.getItem(any(UserEntity.class)))
            .thenReturn(CompletableFuture.completedFuture(testUserEntity));

        Mono<User> result = userDynamo.findById(1);

        StepVerifier.create(result)
            .expectNextMatches(user -> 
                user.getId() == 1 && 
                "test@example.com".equals(user.getEmail()) &&
                "John".equals(user.getFirstName()))
            .verifyComplete();
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserNotExists() {
        when(userTable.getItem(any(UserEntity.class)))
            .thenReturn(CompletableFuture.completedFuture(null));

        Mono<User> result = userDynamo.findById(999);

        StepVerifier.create(result)
            .verifyComplete();
    }
}