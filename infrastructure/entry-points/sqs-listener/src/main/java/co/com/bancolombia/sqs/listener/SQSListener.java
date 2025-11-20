package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.sqs.listener.dto.UserMessageDto;
import co.com.bancolombia.model.utils.Logger;
import co.com.bancolombia.model.utils.LogBuilder;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.util.context.Context;
import co.com.bancolombia.usecase.user.UserUseCase;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SQSListener {
  private final UserUseCase userUseCase;
  private final Logger logger;

  @SqsListener("user-uppercase")
  public void userUppercase(UserMessageDto user) {
    String traceId = UUID.randomUUID().toString();
    Context context = Context.of("traceId", traceId);
    LogBuilder log = logger.with(context).key("user", user.getFirstName());
    
    log.info("Received SQS message for user");
    userUseCase.saveInUppercase(user.toModel())
        .contextWrite(context)
        .doOnSuccess(result -> log.info("Successfully processed user"))
        .doOnError(error -> log.error("Error processing user", error))
        .subscribe();
  }
}
