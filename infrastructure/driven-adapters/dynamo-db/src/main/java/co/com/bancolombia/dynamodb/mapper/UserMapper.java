package co.com.bancolombia.dynamodb.mapper;

import co.com.bancolombia.dynamodb.entity.UserEntity;
import co.com.bancolombia.model.users.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {
  public static UserEntity toEntity(User user) {
    return UserEntity.builder()
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .avatar(user.getAvatar())
        .build();
  }

  public static User toDomain(UserEntity entity) {
    return User.builder()
        .id(entity.getId())
        .email(entity.getEmail())
        .firstName(entity.getFirstName())
        .lastName(entity.getLastName())
        .avatar(entity.getAvatar())
        .build();
  }
}
