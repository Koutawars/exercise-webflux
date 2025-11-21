package co.com.bancolombia.redis.template;

import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class RedisUserAdapterOperationsTest {

  @Mock
  private ReactiveRedisConnectionFactory connectionFactory;

  @Mock
  private ObjectMapper objectMapper;

  private RedisUserAdapter adapter;

  @Mock
  private Logger logger;

  @Mock
  private LogBuilder logBuilder;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    when(objectMapper.map("value", Object.class)).thenReturn("value");

    adapter = new RedisUserAdapter(connectionFactory, objectMapper, logger);
  }

  @Test
  void testFindById() {

    StepVerifier.create(adapter.findById("key"))
        .verifyComplete();
  }

}