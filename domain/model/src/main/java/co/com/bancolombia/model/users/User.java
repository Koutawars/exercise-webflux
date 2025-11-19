package co.com.bancolombia.model.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {
  private int id;
  private String email;
  private String firstName;
  private String lastName;
  private String avatar;
}
