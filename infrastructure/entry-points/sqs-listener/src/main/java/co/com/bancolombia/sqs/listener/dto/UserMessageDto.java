package co.com.bancolombia.sqs.listener.dto;

import co.com.bancolombia.model.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserMessageDto {
  private int id;
  private String email;
  private String firstName;
  private String lastName;
  private String avatar;

  public User toModel() {
    return User.builder()
        .id(this.id)
        .email(this.email)
        .firstName(this.firstName)
        .lastName(this.lastName)
        .avatar(this.avatar)
        .build();
  }
}
