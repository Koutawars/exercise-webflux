package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.entity.UserEntity;
import co.com.bancolombia.dynamodb.mapper.UserMapper;
import co.com.bancolombia.model.users.User;
import co.com.bancolombia.model.users.gateway.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.Map;

@Repository
public class UserDynamo implements UserRepository {
  private final DynamoDbAsyncTable<UserEntity> userTable;

  public UserDynamo(DynamoDbEnhancedAsyncClient connectionFactory,
                    @Value("${aws.dynamodb.userTable}") String tableName) {
    this.userTable = connectionFactory.table(tableName, TableSchema.fromBean(UserEntity.class));
  }

  @Override
  public Mono<User> save(User user) {
    UserEntity userEntity = UserMapper.toEntity(user);
    return Mono.fromFuture(userTable.putItem(userEntity))
        .thenReturn(user);
  }

  @Override
  public Mono<User> findById(int id) {
    return Mono.fromFuture(userTable.getItem(UserEntity.builder().id(id).build()))
        .flatMap(entity -> {
          if (entity == null) {
            return Mono.empty();
          }
          return Mono.just(UserMapper.toDomain(entity));
        });
  }

  @Override
  public Flux<User> findAll() {
    return Flux.from(userTable.scan().items())
        .map(UserMapper::toDomain);
  }

  @Override
  public Flux<User> findByName(String name) {
    Expression filterExpression = Expression.builder()
        .expression("contains(firstName, :name) OR contains(lastName, :name)")
        .expressionValues(Map.of(":name", AttributeValue.builder().s(name).build()))
        .build();
    
    ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
        .filterExpression(filterExpression)
        .build();
    
    return Flux.from(userTable.scan(scanRequest).items())
        .map(UserMapper::toDomain);
  }
}
