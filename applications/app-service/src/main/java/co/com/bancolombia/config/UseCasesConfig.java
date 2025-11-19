package co.com.bancolombia.config;

import co.com.bancolombia.model.users.gateway.DirectoryActiveRepository;
import co.com.bancolombia.model.users.gateway.UserRepository;
import co.com.bancolombia.usecase.user.UserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {
  @Bean
  public UserUseCase userUseCase(
      DirectoryActiveRepository directoryActiveRepository,
      UserRepository userRepository
  ) {
    return new UserUseCase(directoryActiveRepository, userRepository);
  }
}
