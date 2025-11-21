package co.com.bancolombia.config;

import co.com.bancolombia.model.users.gateway.DirectoryActiveRepository;
import co.com.bancolombia.model.users.gateway.UserCacheRepository;
import co.com.bancolombia.model.users.gateway.UserMessagePublisher;
import co.com.bancolombia.model.users.gateway.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@TestConfiguration
public class TestConfig {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserCacheRepository userCacheRepository;

    @MockitoBean
    private UserMessagePublisher userMessagePublisher;

    @MockitoBean
    private DirectoryActiveRepository directoryActiveRepository;
}