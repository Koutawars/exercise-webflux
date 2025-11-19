package co.com.bancolombia.dynamodb;

import co.com.bancolombia.dynamodb.entity.UserEntity;
import co.com.bancolombia.dynamodb.mapper.UserMapper;
import co.com.bancolombia.model.users.User;
import co.com.bancolombia.model.users.gateway.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

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
}
