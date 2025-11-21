package co.com.bancolombia.sqs.sender;

import co.com.bancolombia.model.users.User;
import co.com.bancolombia.model.users.gateway.UserMessagePublisher;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import co.com.bancolombia.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@RequiredArgsConstructor
public class SQSSender implements UserMessagePublisher {
  private final SQSSenderProperties properties;
  private final SqsAsyncClient client;
  private final Logger logger;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> publishUserCreated(User user) {
    return Mono.deferContextual(ctx -> {
      LogBuilder log = logger.with(ctx).key("user", user);
      
      try {
        String messageBody = objectMapper.writeValueAsString(user);
        
        SendMessageRequest request = SendMessageRequest.builder()
            .queueUrl(properties.queueUrl())
            .messageBody(messageBody)
            .build();
        
        return Mono.fromFuture(client.sendMessage(request))
            .doOnSuccess(response -> log.info("User message sent to SQS"))
            .doOnError(error -> log.error("Failed to send user message to SQS", error))
            .then();
            
      } catch (JsonProcessingException e) {
        log.error("Failed to serialize user to JSON", e);
        return Mono.error(e);
      }
    });
  }
}
