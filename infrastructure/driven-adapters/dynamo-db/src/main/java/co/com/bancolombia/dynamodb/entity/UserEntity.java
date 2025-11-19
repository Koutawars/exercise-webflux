package co.com.bancolombia.dynamodb.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Getter
@Setter
@DynamoDbBean
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
  private int id;
  private String email;
  private String firstName;
  private String lastName;
  private String avatar;

  @DynamoDbPartitionKey
  public int getId() {
    return id;
  }
}
